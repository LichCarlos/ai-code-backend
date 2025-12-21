package com.carlos.aicodebackend.core;

import java.io.File;
import org.springframework.stereotype.Service;
import com.carlos.aicodebackend.ai.AiCodeGeneratorService;
import com.carlos.aicodebackend.ai.AiCodeGeneratorServiceFactory;
import com.carlos.aicodebackend.ai.model.HtmlCodeResult;
import com.carlos.aicodebackend.ai.model.MultiFileCodeResult;
import com.carlos.aicodebackend.core.parser.CodeParserExecutor;
import com.carlos.aicodebackend.core.saver.CodeFileSaverExecutor;
import com.carlos.aicodebackend.exception.BusinessException;
import com.carlos.aicodebackend.exception.ErrorCode;
import com.carlos.aicodebackend.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * AI 代码生成外观类，组合生成和保存功能
 */
@Service
@Slf4j
public class AiCodeGeneratorFacade {

  @Resource
  private AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;

  /**
   * 生成 HTML 模式的代码并保存
   *
   * @param userMessage 用户提示词
   * @param appId       应用ID
   * @return 保存的目录
   */
  private File generateAndSaveHtmlCode(String userMessage, Long appId) {
    // 根据 appId 获取对应的 AI 服务实例
    AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
    Flux<String> result = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
    String codeResult = result.collect(StringBuilder::new, StringBuilder::append)
        .block()
        .toString();
    HtmlCodeResult htmlCodeResult = (HtmlCodeResult) CodeParserExecutor.executeParser(codeResult, CodeGenTypeEnum.HTML);
    return CodeFileSaverExecutor.executeSaver(htmlCodeResult, CodeGenTypeEnum.HTML);
  }

  /**
   * 生成多文件模式的代码并保存
   *
   * @param userMessage 用户提示词
   * @param appId       应用ID
   * @return 保存的目录
   */
  private File generateAndSaveMultiFileCode(String userMessage, Long appId) {
    // 根据 appId 获取对应的 AI 服务实例
    AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
    Flux<String> result = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
    String codeResult = result.collect(StringBuilder::new, StringBuilder::append)
        .block()
        .toString();
    MultiFileCodeResult multiFileCodeResult = (MultiFileCodeResult) CodeParserExecutor.executeParser(codeResult,
        CodeGenTypeEnum.MULTI_FILE);
    return CodeFileSaverExecutor.executeSaver(multiFileCodeResult, CodeGenTypeEnum.MULTI_FILE);
  }

  /**
   * 通用流式代码处理方法（使用 appId）
   *
   * @param codeStream  代码流
   * @param codeGenType 代码生成类型
   * @param appId       应用 ID
   * @return 流式响应
   */
  private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenType, Long appId) {
    StringBuilder codeBuilder = new StringBuilder();
    return codeStream.doOnNext(chunk -> {
      // 实时收集代码片段
      codeBuilder.append(chunk);
    }).doOnComplete(() -> {
      // 流式返回完成后保存代码
      try {
        String completeCode = codeBuilder.toString();
        // 使用执行器解析代码
        Object parsedResult = CodeParserExecutor.executeParser(completeCode, codeGenType);
        // 使用执行器保存代码
        File savedDir = CodeFileSaverExecutor.executeSaver(parsedResult, codeGenType, appId);
        log.info("保存成功，路径为：" + savedDir.getAbsolutePath());
      } catch (Exception e) {
        log.error("保存失败: {}", e.getMessage());
      }
    });
  }

  /**
   * 统一入口：根据类型生成并保存代码
   *
   * @param userMessage     用户提示词
   * @param codeGenTypeEnum 生成类型
   * @return 保存的目录
   */
  public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
    // 使用默认的 appId (0) 保持向后兼容
    return generateAndSaveCode(userMessage, codeGenTypeEnum, 0L);
  }

  /**
   * 统一入口：根据类型生成并保存代码（流式）
   *
   * @param userMessage     用户提示词
   * @param codeGenTypeEnum 生成类型
   */
  public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
    // 使用默认的 appId (0) 保持向后兼容
    return generateAndSaveCodeStream(userMessage, codeGenTypeEnum, 0L);
  }

  /**
   * 统一入口：根据类型生成并保存代码（使用 appId）
   *
   * @param userMessage     用户提示词
   * @param codeGenTypeEnum 生成类型
   * @param appId           应用 ID
   * @return 保存的目录
   */
  public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
    if (codeGenTypeEnum == null) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
    }
    return switch (codeGenTypeEnum) {
      case HTML -> generateAndSaveHtmlCode(userMessage, appId);
      case MULTI_FILE -> generateAndSaveMultiFileCode(userMessage, appId);
      default -> {
        String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
      }
    };
  }

  /**
   * 统一入口：根据类型生成并保存代码（流式，使用 appId）
   *
   * @param userMessage     用户提示词
   * @param codeGenTypeEnum 生成类型
   * @param appId           应用 ID
   */
  public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
    if (codeGenTypeEnum == null) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
    }
    // 根据 appId 获取对应的 AI 服务实例
    AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
    return switch (codeGenTypeEnum) {
      case HTML -> {
        Flux<String> codeStream = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
        yield processCodeStream(codeStream, CodeGenTypeEnum.HTML, appId);
      }
      case MULTI_FILE -> {
        Flux<String> codeStream = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
        yield processCodeStream(codeStream, CodeGenTypeEnum.MULTI_FILE, appId);
      }
      default -> {
        String errorMessage = "不支持的生成类型：" + codeGenTypeEnum.getValue();
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
      }
    };
  }
}

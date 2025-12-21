package com.carlos.aicodebackend.controller;

import com.carlos.aicodebackend.model.entity.App;
import com.carlos.aicodebackend.model.entity.User;
import com.carlos.aicodebackend.model.enums.CodeGenTypeEnum;
import com.carlos.aicodebackend.service.AppService;
import com.carlos.aicodebackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.carlos.aicodebackend.config.TestConfig;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * 应用控制器测试类
 * 主要测试流式生成代码功能
 *
 * @author <a href="https://github.com/LichCarlos">LichCarlos</a>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "logging.level.org.springframework.test=WARN",
    "logging.level.org.springframework.boot.test=WARN",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
})
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import(TestConfig.class)
class AppControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private AppService appService;

  @MockitoBean
  private UserService userService;

  private User testUser;
  private App testApp;

  @BeforeEach
  void setUp() {
    // 创建测试用户
    testUser = new User();
    testUser.setId(1L);
    testUser.setUserAccount("test_user");
    testUser.setUserName("测试用户");
    testUser.setUserRole("user");

    // 创建测试应用
    testApp = new App();
    testApp.setId(1L);
    testApp.setAppName("测试应用");
    testApp.setInitPrompt("创建一个简单的任务管理网站");
    testApp.setCodeGenType(CodeGenTypeEnum.MULTI_FILE.getValue());
    testApp.setUserId(1L);
    testApp.setCreateTime(LocalDateTime.now());
    testApp.setUpdateTime(LocalDateTime.now());
    testApp.setEditTime(LocalDateTime.now());
  }

  @Test
  void testChatToGenCode_Stream() {
    // 模拟用户登录
    when(userService.getLoginUser(any(HttpServletRequest.class))).thenReturn(testUser);

    // 模拟应用查询
    when(appService.getById(1L)).thenReturn(testApp);

    // 模拟流式生成代码
    Flux<String> mockCodeStream = Flux.just(
        "```html\n",
        "<!DOCTYPE html>\n",
        "<html>\n",
        "<head>\n",
        "    <title>任务管理</title>\n",
        "</head>\n",
        "<body>\n",
        "    <h1>任务管理</h1>\n",
        "</body>\n",
        "</html>\n",
        "```\n");

    when(appService.chatToGenCode(eq(1L), eq("添加一个任务统计功能"), eq(testUser)))
        .thenReturn(mockCodeStream);

    // 测试流式接口
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path("/app/chat/gen/code")
            .queryParam("appId", 1L)
            .queryParam("message", "添加一个任务统计功能")
            .build())
        .accept(MediaType.TEXT_EVENT_STREAM)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM)
        .returnResult(ServerSentEvent.class)
        .getResponseBody()
        .as(StepVerifier::create)
        .expectNextMatches(event -> {
          // 验证数据格式
          Object dataObj = event.data();
          String data = dataObj != null ? dataObj.toString() : null;
          return data != null && data.contains("\"d\"");
        })
        .expectNextCount(8) // 验证接收到剩余8个数据块（总共9个，第一个已验证）
        .expectNextMatches(event -> {
          // 验证结束事件
          return "done".equals(event.event());
        })
        .thenCancel()
        .verify();
  }

  @Test
  void testChatToGenCode_InvalidAppId() {
    // 模拟用户登录
    when(userService.getLoginUser(any(HttpServletRequest.class))).thenReturn(testUser);

    // 测试无效的应用ID
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path("/app/chat/gen/code")
            .queryParam("appId", -1L)
            .queryParam("message", "测试消息")
            .build())
        .accept(MediaType.TEXT_EVENT_STREAM)
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void testChatToGenCode_EmptyMessage() {
    // 模拟用户登录
    when(userService.getLoginUser(any(HttpServletRequest.class))).thenReturn(testUser);

    // 测试空消息
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path("/app/chat/gen/code")
            .queryParam("appId", 1L)
            .queryParam("message", "")
            .build())
        .accept(MediaType.TEXT_EVENT_STREAM)
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void testChatToGenCode_AppNotFound() {
    // 模拟用户登录
    when(userService.getLoginUser(any(HttpServletRequest.class))).thenReturn(testUser);

    // 模拟应用不存在 - 注意：这里不需要 mock getById，因为控制器会先调用它
    // 但实际测试中，如果 getById 返回 null，chatToGenCode 方法内部会抛出异常
    // 所以我们需要让 chatToGenCode 抛出异常，而不是 getById
    when(appService.chatToGenCode(eq(999L), anyString(), any(User.class)))
        .thenReturn(Flux.error(new com.carlos.aicodebackend.exception.BusinessException(
            com.carlos.aicodebackend.exception.ErrorCode.NOT_FOUND_ERROR, "应用不存在")));

    // 测试应用不存在的情况
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path("/app/chat/gen/code")
            .queryParam("appId", 999L)
            .queryParam("message", "测试消息")
            .build())
        .accept(MediaType.TEXT_EVENT_STREAM)
        .exchange()
        .expectStatus().is5xxServerError();
  }

  @Test
  void testChatToGenCode_NoPermission() {
    // 创建另一个用户
    User otherUser = new User();
    otherUser.setId(2L);
    otherUser.setUserAccount("other_user");
    otherUser.setUserName("其他用户");
    otherUser.setUserRole("user");

    // 模拟其他用户登录
    when(userService.getLoginUser(any(HttpServletRequest.class))).thenReturn(otherUser);

    // 模拟应用查询（应用属于用户1）
    when(appService.getById(1L)).thenReturn(testApp);

    // 模拟无权限访问 - 返回错误流而不是抛出异常
    when(appService.chatToGenCode(eq(1L), anyString(), eq(otherUser)))
        .thenReturn(Flux.error(new com.carlos.aicodebackend.exception.BusinessException(
            com.carlos.aicodebackend.exception.ErrorCode.NO_AUTH_ERROR, "无权限访问该应用")));

    // 测试无权限的情况
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path("/app/chat/gen/code")
            .queryParam("appId", 1L)
            .queryParam("message", "测试消息")
            .build())
        .accept(MediaType.TEXT_EVENT_STREAM)
        .exchange()
        .expectStatus().is5xxServerError();
  }

  @Test
  void testChatToGenCode_LargeStream() {
    // 模拟用户登录
    when(userService.getLoginUser(any(HttpServletRequest.class))).thenReturn(testUser);

    // 模拟应用查询
    when(appService.getById(1L)).thenReturn(testApp);

    // 模拟大量数据的流式生成
    Flux<String> largeCodeStream = Flux.range(1, 100)
        .map(i -> "代码块 " + i + "\n");

    when(appService.chatToGenCode(eq(1L), eq("生成大量代码"), eq(testUser)))
        .thenReturn(largeCodeStream);

    // 测试大量数据的流式接口
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path("/app/chat/gen/code")
            .queryParam("appId", 1L)
            .queryParam("message", "生成大量代码")
            .build())
        .accept(MediaType.TEXT_EVENT_STREAM)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM)
        .returnResult(ServerSentEvent.class)
        .getResponseBody()
        .as(StepVerifier::create)
        .expectNextCount(100) // 验证接收到100个数据块
        .expectNextMatches(event -> "done".equals(event.event())) // 验证结束事件
        .thenCancel()
        .verify();
  }

  @Test
  void testChatToGenCode_StreamFormat() {
    // 模拟用户登录
    when(userService.getLoginUser(any(HttpServletRequest.class))).thenReturn(testUser);

    // 模拟应用查询
    when(appService.getById(1L)).thenReturn(testApp);

    // 模拟流式生成代码
    Flux<String> mockCodeStream = Flux.just("测试内容1", "测试内容2", "测试内容3");

    when(appService.chatToGenCode(eq(1L), eq("测试消息"), eq(testUser)))
        .thenReturn(mockCodeStream);

    // 测试流式接口的数据格式
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path("/app/chat/gen/code")
            .queryParam("appId", 1L)
            .queryParam("message", "测试消息")
            .build())
        .accept(MediaType.TEXT_EVENT_STREAM)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM)
        .returnResult(ServerSentEvent.class)
        .getResponseBody()
        .take(3)
        .as(StepVerifier::create)
        .expectNextMatches(event -> {
          // 验证每个事件都包含JSON格式的数据
          Object dataObj = event.data();
          String data = dataObj != null ? dataObj.toString() : null;
          return data != null &&
              data.startsWith("{") &&
              data.contains("\"d\"") &&
              data.contains("测试内容");
        })
        .expectNextCount(2)
        .thenCancel()
        .verify();
  }
}

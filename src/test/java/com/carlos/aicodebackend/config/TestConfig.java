package com.carlos.aicodebackend.config;

import com.carlos.aicodebackend.ai.AiCodeGeneratorService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

/**
 * 测试配置类
 * 提供 Mock Bean 以替代需要真实 AI 服务的 Bean
 */
@TestConfiguration
public class TestConfig {

    /**
     * Mock ChatModel bean
     * 用于替代真实的 AI ChatModel，避免在测试环境中需要真实的 API 密钥
     */
    @Bean
    @Primary
    public ChatModel chatModel() {
        return mock(ChatModel.class);
    }

    /**
     * Mock StreamingChatModel bean
     * 用于替代真实的 AI StreamingChatModel，避免在测试环境中需要真实的 API 密钥
     */
    @Bean
    @Primary
    public StreamingChatModel streamingChatModel() {
        return mock(StreamingChatModel.class);
    }

    /**
     * Mock AiCodeGeneratorService bean
     * 用于替代真实的 AI 代码生成服务
     */
    @Bean
    @Primary
    public AiCodeGeneratorService aiCodeGeneratorService() {
        AiCodeGeneratorService mockService = mock(AiCodeGeneratorService.class);
        // 默认返回空流，测试中可以覆盖
        return mockService;
    }
}

package com.carlos.aicodebackend;

import com.carlos.aicodebackend.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "langchain4j.community.redis.enabled=false",
        "spring.main.allow-bean-definition-overriding=true"
})
@ActiveProfiles("test")
@Import(TestConfig.class)
class AiCodeBackendApplicationTests {

    @Test
    void contextLoads() {
    }

}

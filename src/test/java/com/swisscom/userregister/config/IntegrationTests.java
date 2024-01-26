package com.swisscom.userregister.config;

import com.swisscom.userregister.UserRegisterApplication;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@Tag("integration")
@ActiveProfiles("test")
@ContextConfiguration(initializers = DockerTestInitializer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = UserRegisterApplication.class)
public class IntegrationTests {
}

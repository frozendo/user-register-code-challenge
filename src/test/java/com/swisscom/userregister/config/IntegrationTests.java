package com.swisscom.userregister.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.swisscom.userregister.UserRegisterApplication;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@Tag("integration")
@ActiveProfiles("test")
@ContextConfiguration(initializers = DockerTestInitializer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = UserRegisterApplication.class)
public abstract class IntegrationTests {

    @LocalServerPort
    private Integer port;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private OpaServerConfiguration opaServerConfiguration;

    private static boolean shouldConfigureOpaServer = true;

    @BeforeEach
    void configureOpaServerForTest() throws IOException {
        if (shouldConfigureOpaServer) {
            opaServerConfiguration.configureServer();
            shouldConfigureOpaServer = false;
        }
    }

    protected RequestSpecification getRequest() {
        var spec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
        return RestAssured.given()
                .when()
                .spec(spec)
                .log().all()
                .port(port);
    }

    protected String getJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

}

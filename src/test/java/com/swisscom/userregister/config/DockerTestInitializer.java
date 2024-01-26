package com.swisscom.userregister.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;

public class DockerTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final PostgreSQLContainer postgreSqlContainer = new PostgreSQLContainer("postgres:14-alpine")
            .withDatabaseName("tests-db")
            .withUsername("root")
            .withPassword("root");

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
        postgreSqlContainer.withExposedPorts(5432);
        postgreSqlContainer.withInitScript("scripts/database-test.sql");
        postgreSqlContainer.start();

        var databaseUrl = "spring.datasource.url=" + postgreSqlContainer.getJdbcUrl();
        var username = "spring.datasource.username=" + postgreSqlContainer.getUsername();
        var password = "spring.datasource.password=" + postgreSqlContainer.getPassword();

        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext, databaseUrl, username, password);

    }
}

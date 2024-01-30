package com.swisscom.userregister.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class DockerTestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final PostgreSQLContainer postgreSqlContainer = new PostgreSQLContainer("postgres:14-alpine")
            .withDatabaseName("tests-db")
            .withUsername("root")
            .withPassword("root");

    public static GenericContainer opaContainer = new GenericContainer(
            DockerImageName.parse("openpolicyagent/opa:edge-rootless"))
            .withCommand("run --server");

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
        postgreSqlContainer.withExposedPorts(5432);
        postgreSqlContainer.withInitScript("scripts/database-test.sql");
        postgreSqlContainer.start();

        opaContainer.withExposedPorts(8181);
        opaContainer.waitingFor(Wait.forHttp("/health"));
        opaContainer.start();

        var databaseUrl = "spring.datasource.url=" + postgreSqlContainer.getJdbcUrl();
        var username = "spring.datasource.username=" + postgreSqlContainer.getUsername();
        var password = "spring.datasource.password=" + postgreSqlContainer.getPassword();

        var opaServer = "opa.server= http://%s:%d".formatted(opaContainer.getHost(), opaContainer.getFirstMappedPort());

        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                applicationContext, databaseUrl, username, password, opaServer);

    }
}

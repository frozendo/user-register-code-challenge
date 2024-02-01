package com.swisscom.userregister.config;

import jakarta.annotation.PostConstruct;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("!test")
public class FlywayMigrate {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void migrate() {
        Flyway.configure()
                .dataSource(dataSource)
                .mixed(true)
                .load()
                .migrate();
    }

}

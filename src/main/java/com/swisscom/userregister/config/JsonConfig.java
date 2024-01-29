package com.swisscom.userregister.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;

public class JsonConfig {

    @Bean
    public ObjectMapper mapper() {
        return new ObjectMapper();
    }

}

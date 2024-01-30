package com.swisscom.userregister.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

public class WebClientConfig {

    @Bean
    public WebClient buildInstance() {
        return WebClient.builder().build();
    }

}

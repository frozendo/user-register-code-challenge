package com.swisscom.userregister.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

@Configuration
@EnableWebSecurity
public class ApiWebSecurityConfig {

    private final AuthorizationManager<RequestAuthorizationContext> opaAuthenticateManager;

    public ApiWebSecurityConfig(AuthorizationManager<RequestAuthorizationContext> opaAuthenticateManager) {
        this.opaAuthenticateManager = opaAuthenticateManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> {
                            request.requestMatchers("/actuator/**").permitAll();
                            request.requestMatchers("/error").permitAll();
                            request.requestMatchers("/favicon.ico").permitAll();
                            request.anyRequest()
                                    .access(opaAuthenticateManager);
                        }
                );
        return http.build();
    }

}

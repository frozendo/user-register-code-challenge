package com.swisscom.userregister.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Configuration
public class OpaServerConfiguration {

    @Value("classpath:opa-resources/user_policy.rego")
    private Resource userPolicyFile;

    @Value("classpath:opa-resources/role_grants.json")
    private Resource roleJsonFile;

    @Value("classpath:opa-resources/user_roles.json")
    private Resource userJsonFile;

    @Value("${opa.server}")
    private String opaServer;

    @Value("${opa.policy-endpoint}")
    private String policyEndpoint;

    @Value("${opa.roles-endpoint}")
    private String rolesEndpoint;

    @Value("${opa.users-endpoint}")
    private String usersEndpoint;

    public void configureServer() throws IOException {
        configurePolicy();
        configureRolesGranted();
        configureUserRoles();
    }

    private void configurePolicy() throws IOException {
        Reader reader = new InputStreamReader(userPolicyFile.getInputStream(), StandardCharsets.UTF_8);
        String policyData = FileCopyUtils.copyToString(reader);
        executeRequest(policyData, policyEndpoint);
    }

    private void configureRolesGranted() throws IOException {
        Reader reader = new InputStreamReader(roleJsonFile.getInputStream(), StandardCharsets.UTF_8);
        String policyData = FileCopyUtils.copyToString(reader);
        executeRequest(policyData, rolesEndpoint);
    }

    private void configureUserRoles() throws IOException {
        Reader reader = new InputStreamReader(userJsonFile.getInputStream(), StandardCharsets.UTF_8);
        String policyData = FileCopyUtils.copyToString(reader);
        executeRequest(policyData, usersEndpoint);
    }

    private void executeRequest(String policyData, String endpoint) {
        var client = WebClient.builder().build();
        var uri = "%s%s".formatted(opaServer, endpoint);

        client.put()
                .uri(uri)
                .accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(policyData)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }



}

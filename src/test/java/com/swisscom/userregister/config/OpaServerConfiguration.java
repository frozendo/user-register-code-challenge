package com.swisscom.userregister.config;

import com.swisscom.userregister.config.properties.OpaServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Value("classpath:opa-resources/opa_data.json")
    private Resource opaDataFile;

    @Autowired
    private OpaServerProperties opaServerProperties;

    public void configureServer() throws IOException {
        configurePolicy();
        configureData();
    }

    private void configurePolicy() throws IOException {
        Reader reader = new InputStreamReader(userPolicyFile.getInputStream(), StandardCharsets.UTF_8);
        String policyData = FileCopyUtils.copyToString(reader);
        executeRequest(policyData, opaServerProperties.policyEndpoint());
    }

    private void configureData() throws IOException {
        Reader reader = new InputStreamReader(opaDataFile.getInputStream(), StandardCharsets.UTF_8);
        String opaData = FileCopyUtils.copyToString(reader);
        executeRequest(opaData, opaServerProperties.dataEndpoint());
    }

    private void executeRequest(String policyData, String endpoint) {
        var client = WebClient.builder().build();
        var uri = opaServerProperties.server().concat(endpoint);

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

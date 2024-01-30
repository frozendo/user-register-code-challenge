package com.swisscom.userregister.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swisscom.userregister.domain.entity.User;
import com.swisscom.userregister.domain.exceptions.BusinessException;
import com.swisscom.userregister.domain.request.OpaUserRequest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public class OpaUserService {

    private final ObjectMapper objectMapper;
    private final WebClient client;
    private String opaServer;
    private String usersEndpoint;

    public OpaUserService(ObjectMapper objectMapper,
                          WebClient webClient,
                          String opaServer,
                          String usersEndpoint) {
        this.objectMapper = objectMapper;
        this.client = webClient;
        this.opaServer = opaServer;
        this.usersEndpoint = usersEndpoint;
    }

    public void synchronizedUserToOpa(List<User> users) {
        try {
            var usersJson = getRequestJson(users);
            executeRequest(usersJson);
        } catch (JsonProcessingException e) {
            throw new BusinessException("Error on synchronizing users to OPA server");
        }
    }

    private String getRequestJson(List<User> users) throws JsonProcessingException {
        List<OpaUserRequest> requests = users.stream().map(user ->
                new OpaUserRequest(user.getEmail(), user.getRoleName())
        ).toList();

        return objectMapper.writeValueAsString(requests);
    }

    private void executeRequest(String usersJson) {
        var uri = "%s%s".formatted(opaServer, usersEndpoint);

        client.post()
                .uri(uri)
                .accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(usersJson)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}

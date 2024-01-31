package com.swisscom.userregister.service;

import com.swisscom.userregister.config.properties.OpaServerProperties;
import com.swisscom.userregister.domain.entity.User;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.json.JSONObject;

import java.util.List;

@Service
public class OpaUserService {

    private final WebClient client;
    private final OpaServerProperties opaServerProperties;

    public OpaUserService(WebClient webClient,
                          OpaServerProperties opaServerProperties) {
        this.client = webClient;
        this.opaServerProperties = opaServerProperties;
    }

    public void synchronizedUserToOpa(List<User> users) {
        var usersJson = getRequestJson(users);
        executeRequest(usersJson);
    }

    private String getRequestJson(List<User> users) {
        var jsonObject = new JSONObject();

        for (User user : users) {
            jsonObject.put(user.getEmail(), user.getRole());
        }

        return jsonObject.toString();
    }

    private void executeRequest(String usersJson) {
        client.put()
                .uri(opaServerProperties.getUserDataUri())
                .accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(usersJson)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}

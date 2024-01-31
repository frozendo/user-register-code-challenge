package com.swisscom.userregister.service;

import com.swisscom.userregister.config.properties.OpaServerProperties;
import com.swisscom.userregister.domain.entity.User;
import com.swisscom.userregister.domain.enums.ApiActionEnum;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.json.JSONObject;

import java.util.List;

@Service
public class OpaServerService {

    private final WebClient client;
    private final OpaServerProperties opaServerProperties;

    public OpaServerService(WebClient webClient,
                            OpaServerProperties opaServerProperties) {
        this.client = webClient;
        this.opaServerProperties = opaServerProperties;
    }

    public void synchronizedUserToOpa(List<User> users) {
        var usersJson = getSynchronizeRequestJson(users);
        executePutRequest(usersJson, opaServerProperties.getUserDataUri());
    }

    public boolean authorizeUserAction(String email, ApiActionEnum action) {
        var authorizeJson = getAuthorizeRequestJson(email, action);
        var response = executePostRequest(authorizeJson, opaServerProperties.getAuthorizeUri());
        return getAuthorizeResponse(response);
    }

    private String getSynchronizeRequestJson(List<User> users) {
        var jsonObject = new JSONObject();

        for (User user : users) {
            jsonObject.put(user.getEmail(), user.getRoleName());
        }

        return jsonObject.toString();
    }

    private String getAuthorizeRequestJson(String email, ApiActionEnum action) {
        var inputObject = new JSONObject();
        var jsonDataObject = new JSONObject();

        jsonDataObject.put("email", email);
        jsonDataObject.put("action", action.getKeyValue());

        inputObject.put("input", jsonDataObject);

        return inputObject.toString();
    }

    private boolean getAuthorizeResponse(String response) {
        var jsonObject = new JSONObject(response);
        return jsonObject.getBoolean("result");
    }

    private void executePutRequest(String usersJson, String uri) {
        client.put()
                .uri(uri)
                .accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(usersJson)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private String executePostRequest(String usersJson, String uri) {
        return client.post()
                .uri(uri)
                .accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(usersJson)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}
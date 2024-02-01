package com.swisscom.userregister.service;

import com.swisscom.userregister.config.properties.OpaServerProperties;
import com.swisscom.userregister.domain.entity.User;
import com.swisscom.userregister.domain.enums.ApiActionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.json.JSONObject;

import java.util.List;

@Service
public class OpaServerService {

    private static final Logger logger = LoggerFactory.getLogger(OpaServerService.class);

    private final WebClient client;
    private final OpaServerProperties opaServerProperties;

    public OpaServerService(WebClient webClient,
                            OpaServerProperties opaServerProperties) {
        this.client = webClient;
        this.opaServerProperties = opaServerProperties;
    }

    public void synchronizeUsersToOpa(List<User> users) {
        var usersJson = getSynchronizeRequestJson(users);
        logger.info("Synchronize users to OPA server");
        var uri = opaServerProperties.getUserRolesUri();
        executePutRequest(usersJson, uri);
    }

    public boolean authorizeUserAction(String email, ApiActionEnum action) {
        var authorizeJson = getAuthorizeRequestJson(email, action);
        logger.info("Send request to OPA server to authorize user {}", email);
        var response = executePostRequest(authorizeJson, opaServerProperties.getAuthorizeUri());
        return getAuthorizeResponse(response);
    }

    private String getSynchronizeRequestJson(List<User> users) {
        var userObject = new JSONObject();

        for (User user : users) {
            userObject.put(user.getEmail(), user.getRoleName());
        }

        return userObject.toString();
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
        if (jsonObject.has("result")) {
            return jsonObject.getBoolean("result");
        }
        return false;
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

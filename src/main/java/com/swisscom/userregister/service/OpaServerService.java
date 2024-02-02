package com.swisscom.userregister.service;

import com.swisscom.userregister.config.properties.OpaServerProperties;
import com.swisscom.userregister.domain.entity.User;
import com.swisscom.userregister.domain.enums.ApiActionEnum;
import org.json.JSONArray;
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

    public void synchronizeUsersToOpa(User user) {
        var usersJson = getSynchronizeUsersRequestJson(user);
        logger.info("Synchronize users to OPA server");
        var uri = opaServerProperties.getUserRolesUri();
        executePatchRequest(usersJson, uri);
    }

    private String getSynchronizeUsersRequestJson(User user) {
        var jsonObject = new JSONObject();
        jsonObject.put("op", "add");
        jsonObject.put("path", user.getEmail());

        jsonObject.put("value", user.getRoleName());

        var array = new JSONArray();
        array.put(jsonObject);

        return array.toString();
    }

    private void executePatchRequest(String usersJson, String uri) {
        client.patch()
                .uri(uri)
                .accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(usersJson)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public boolean authorizeUserAction(String token, ApiActionEnum action) {
        var authorizeJson = getAuthorizeRequestJson(token, action);
        logger.info("Send request to OPA server to authorize user {}", token);
        var response = executePostRequest(authorizeJson, opaServerProperties.getAuthorizeUri());
        return getAuthorizeResponse(response);
    }

    private String getAuthorizeRequestJson(String token, ApiActionEnum action) {
        var inputObject = new JSONObject();
        var jsonDataObject = new JSONObject();

        jsonDataObject.put("token", token);
        jsonDataObject.put("action", action.getKeyValue());

        inputObject.put("input", jsonDataObject);

        return inputObject.toString();
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

    private boolean getAuthorizeResponse(String response) {
        var jsonObject = new JSONObject(response);
        if (jsonObject.has("result")) {
            return jsonObject.getBoolean("result");
        }
        return false;
    }

    public void synchronizeTokenToOpa(String token, String email) {
        var json = getSynchronizeTokenRequestJson(token, email);
        executePatchRequest(json, opaServerProperties.getUserTokenUri());
    }

    private String getSynchronizeTokenRequestJson(String token, String email) {
        var jsonObject = new JSONObject();
        jsonObject.put("op", "add");
        jsonObject.put("path", token);

        jsonObject.put("value", email);

        var array = new JSONArray();
        array.put(jsonObject);

        return array.toString();
    }

}

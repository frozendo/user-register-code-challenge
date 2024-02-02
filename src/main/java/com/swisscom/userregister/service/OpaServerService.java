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

@Service
public class OpaServerService {

    private static final Logger logger = LoggerFactory.getLogger(OpaServerService.class);

    private static final String PATCH_JSON_OPERATION_KEY = "op";
    private static final String PATCH_JSON_PATH_KEY = "path";
    private static final String PATCH_JSON_VALUE_KEY = "value";
    private static final String PATCH_JSON_ACTION = "add";

    private final WebClient client;
    private final OpaServerProperties opaServerProperties;

    public OpaServerService(WebClient webClient,
                            OpaServerProperties opaServerProperties) {
        this.client = webClient;
        this.opaServerProperties = opaServerProperties;
    }

    public void synchronizeUsersToOpa(User user) {
        var usersJson = getSynchronizeUsersRequestJson(user);
        logger.info("Synchronize new user to OPA server");
        var uri = opaServerProperties.getUserRolesUri();
        executePatchRequest(usersJson, uri);
    }

    private String getSynchronizeUsersRequestJson(User user) {
        var jsonObject = new JSONObject();
        jsonObject.put(PATCH_JSON_OPERATION_KEY, PATCH_JSON_ACTION);
        jsonObject.put(PATCH_JSON_PATH_KEY, user.getEmail());

        jsonObject.put(PATCH_JSON_VALUE_KEY, user.getRoleName());

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
        logger.info("Authorize user {} with OPA server", token);
        var response = executePostRequest(authorizeJson, opaServerProperties.getAuthorizeUri());

        logger.info("Server response with result: {}", response);
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
        jsonObject.put(PATCH_JSON_OPERATION_KEY, PATCH_JSON_ACTION);
        jsonObject.put(PATCH_JSON_PATH_KEY, token);

        jsonObject.put(PATCH_JSON_VALUE_KEY, email);

        var array = new JSONArray();
        array.put(jsonObject);

        return array.toString();
    }

}

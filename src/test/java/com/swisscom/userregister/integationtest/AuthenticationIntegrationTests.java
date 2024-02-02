package com.swisscom.userregister.integationtest;

import com.swisscom.userregister.config.IntegrationTests;
import com.swisscom.userregister.config.properties.OpaServerProperties;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.reactive.function.client.WebClient;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Sql(value = {"/scripts/user.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/scripts/clear.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AuthenticationIntegrationTests extends IntegrationTests {

    private static final String EMAIL = "elrond@email.com";
    private static final String PASSWORD = "456789";
    private static final String LOGIN_URI = "/login";

    @Autowired
    private OpaServerProperties opaServerProperties;

    @Autowired
    private WebClient client;

    @Test
    void testAuthenticateUser() {
        getRequest(EMAIL, PASSWORD)
                .get(LOGIN_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .header(HttpHeaders.AUTHORIZATION, not(empty()));

        var generateToken = getSynchronizeTokenInOpaServer(EMAIL);
        assertNotNull(generateToken);
        assertNotEquals("", generateToken);
    }

    @Test
    void testAuthenticateWithInvalidUser() {
        String wrongEmail = "elrond@test.com";
        getRequest(wrongEmail, PASSWORD)
                .get(LOGIN_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .header(HttpHeaders.AUTHORIZATION, blankOrNullString());


        var generateToken = getSynchronizeTokenInOpaServer(wrongEmail);
        assertEquals("", generateToken);
    }

    @Test
    void testAuthenticateWithInvalidPassword() {
        String wrongPassword = "654987";
        getRequest(EMAIL, wrongPassword)
                .get(LOGIN_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .header(HttpHeaders.AUTHORIZATION, not(empty()));

        var generateToken = getSynchronizeTokenInOpaServer(EMAIL);
        assertEquals("", generateToken);
    }

    private String getSynchronizeTokenInOpaServer(String email) throws JSONException {
        var uri = opaServerProperties.getUserTokenUri();
        var usersJson = client.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        var responseArray = new JSONObject(usersJson);
        var results = responseArray.getJSONObject("result");

        for (int i = 0; i < results.names().length(); i++) {
            var key = results.names().getString(i);
            var toCheckEmail = results.getString(key);
            if (email.equals(toCheckEmail)) {
                return key;
            }
        }

        return "";
    }

}

package com.swisscom.userregister.integationtest;

import com.swisscom.userregister.config.IntegrationTests;
import com.swisscom.userregister.config.properties.OpaServerProperties;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private OpaServerProperties opaServerProperties;

    @Autowired
    private WebClient client;

    @Test
    void testAuthenticateUser() {
        String email = "elrond@email.com";
        getRequest(email, "456789")
                .get("/login")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .header("Authorization", not(empty()));

        var generateToken = getSynchronizeTokenInOpaServer(email);
        assertNotNull(generateToken);
        assertNotEquals("", generateToken);
    }

    @Test
    void testAuthenticateWithInvalidUser() {
        String email = "elrond@test.com";
        getRequest(email, "456789")
                .get("/login")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .header("Authorization", blankOrNullString());


        var generateToken = getSynchronizeTokenInOpaServer(email);
        assertEquals("", generateToken);
    }

    @Test
    void testAuthenticateWithInvalidPassword() {
        String email = "elrond@email.com";
        getRequest(email, "654987")
                .get("/login")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .header("Authorization", not(empty()));

        var generateToken = getSynchronizeTokenInOpaServer(email);
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

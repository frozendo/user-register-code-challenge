package com.swisscom.userregister.integationtest;

import com.swisscom.userregister.config.IntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.empty;

@Sql(value = {"/scripts/user.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/scripts/clear.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AuthenticationIntegrationTests extends IntegrationTests {

    @Test
    void testAuthenticateUser() {
        getRequest("gandalf@whitewizard.com", "123456")
                .get("/login")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .header("Authorization", not(empty()));
    }

    @Test
    void testAuthenticateWithInvalidUser() {
        getRequest("gandalf@test.com", "123456")
                .get("/login")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .header("Authorization", blankOrNullString());
    }

    @Test
    void testAuthenticateWithInvalidPassword() {
        getRequest("gandalf@whitewizard.com", "654987")
                .get("/login")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .header("Authorization", not(empty()));
    }

}

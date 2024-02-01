package com.swisscom.userregister.integationtest;

import com.swisscom.userregister.config.IntegrationTests;
import com.swisscom.userregister.repository.SessionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql(value = {"/scripts/user.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/scripts/clear.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AuthenticationIntegrationTests extends IntegrationTests {

    @Autowired
    private SessionRepository sessionRepository;

    @Test
    void testAuthenticateUser() {
        getRequest("elrond@email.com", "456789")
                .get("/login")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .header("Authorization", not(empty()));

        var optSession = sessionRepository.findByEmail("elrond@email.com");
        assertTrue(optSession.isPresent());

        var session = optSession.get();
        assertNotNull(session.getToken());
    }

    @Test
    void testAuthenticateWithInvalidUser() {
        getRequest("elrond@test.com", "456789")
                .get("/login")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .header("Authorization", blankOrNullString());

        var optSession = sessionRepository.findByEmail("elrond@test.com");
        assertFalse(optSession.isPresent());
    }

    @Test
    void testAuthenticateWithInvalidPassword() {
        getRequest("elrond@email.com", "654987")
                .get("/login")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .header("Authorization", not(empty()));

        var optSession = sessionRepository.findByEmail("elrond@email.com");
        assertFalse(optSession.isPresent());
    }

}

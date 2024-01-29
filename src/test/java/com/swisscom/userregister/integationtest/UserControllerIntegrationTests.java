package com.swisscom.userregister.integationtest;

import com.swisscom.userregister.config.IntegrationTests;
import com.swisscom.userregister.controller.UserController;
import com.swisscom.userregister.domain.request.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.hasSize;

class UserControllerIntegrationTests extends IntegrationTests {

    @Test
    void testCreateUser() {

        var createUserRequest = new CreateUserRequest("test", "test@email.com", "ADMIN");

        getRequest()
                .body(getJson(createUserRequest))
                .post(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void testCreateUserWhenParameterDataIsNull() {

        var createUserRequest = new CreateUserRequest(null, null, null);

        getRequest()
                .body(getJson(createUserRequest))
                .post(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("size", hasSize(2));

    }

    @Test
    void testCreateUserWhenParameterDataIsEmpty() {

        var createUserRequest = new CreateUserRequest("", "", "");

        getRequest()
                .body(getJson(createUserRequest))
                .post(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("size", hasSize(2));
    }

}

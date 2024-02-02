package com.swisscom.userregister.integationtest;

import com.swisscom.userregister.config.IntegrationTests;
import com.swisscom.userregister.controller.UserController;
import com.swisscom.userregister.domain.enums.RoleEnum;
import com.swisscom.userregister.domain.request.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@Sql(value = {"/scripts/user.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/scripts/clear.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AuthorizationIntegrationTests extends IntegrationTests {

    private static final String COMMON_VALID_TOKEN = "Bearer 544034dd06a4465cb2e5005338b90e85";
    private static final String NOT_EXIST_TOKEN = "Bearer 428034dd06a4465ba1d4995338b12345";
    private static final String EXPIRED_TOKEN = "Bearer 539145ee06a4465ba1d4995338b12345";
    public static final String USER_EMAIL = "test@email.com";
    public static final String USER_NAME = "test";
    public static final String PASSWORD = "123456";

    @Test
    void testListUsersWithAdminUser() {
        getRequest()
                .get(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("size", hasSize(5));
    }

    @Test
    void testListUsersWithCommonUser() {
        getRequest(COMMON_VALID_TOKEN)
                .get(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("size", hasSize(5));
    }

    @Test
    void testCreateUserWithAdminUser() {

        var createUserRequest = new CreateUserRequest(USER_NAME, USER_EMAIL, PASSWORD, RoleEnum.COMMON);

        getRequest()
                .body(getJson(createUserRequest))
                .post(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", equalTo(USER_NAME))
                .body("email", equalTo(USER_EMAIL));
    }

    @Test
    void testCreateUserWithCommonUser() {

        var createUserRequest = new CreateUserRequest(USER_NAME, USER_EMAIL, PASSWORD, RoleEnum.COMMON);

        getRequest(COMMON_VALID_TOKEN)
                .body(getJson(createUserRequest))
                .post(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void testCreateUserWithTokenThatNotExist() {

        var createUserRequest = new CreateUserRequest(USER_NAME, USER_EMAIL, PASSWORD, RoleEnum.COMMON);

        getRequest(NOT_EXIST_TOKEN)
                .body(getJson(createUserRequest))
                .post(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void testCreateUserWithExpiredToken() {

        var createUserRequest = new CreateUserRequest(USER_NAME, USER_EMAIL, PASSWORD, RoleEnum.COMMON);

        getRequest(EXPIRED_TOKEN)
                .body(getJson(createUserRequest))
                .post(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void testCreateUserWithEmptyToken() {

        var createUserRequest = new CreateUserRequest(USER_NAME, USER_EMAIL, PASSWORD, RoleEnum.COMMON);

        getRequest("")
                .body(getJson(createUserRequest))
                .post(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

}

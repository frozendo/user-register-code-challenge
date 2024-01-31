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
class UserAuthorizationIntegrationTests extends IntegrationTests {

    private static final String ADMIN_USER_EMAIL = "gandalf@whitewizard.com";
    private static final String COMMON_USER_EMAIL = "fangorn@email.com";
    public static final String USER_EMAIL = "test@email.com";
    public static final String USER_NAME = "test";

    @Test
    void testListUsersWithAdminUser() {
        getRequest(ADMIN_USER_EMAIL)
                .get(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("size", hasSize(4));
    }

    @Test
    void testListUsersWithCommonUser() {
        getRequest(COMMON_USER_EMAIL)
                .get(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("size", hasSize(4));
    }

    @Test
    void testCreateUserWithAdminUser() {

        var createUserRequest = new CreateUserRequest(USER_NAME, USER_EMAIL, RoleEnum.COMMON);

        getRequest(ADMIN_USER_EMAIL)
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

        var createUserRequest = new CreateUserRequest(USER_NAME, USER_EMAIL, RoleEnum.COMMON);

        getRequest(COMMON_USER_EMAIL)
                .body(getJson(createUserRequest))
                .post(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

}

package com.swisscom.userregister.integationtest;

import com.swisscom.userregister.config.IntegrationTests;
import com.swisscom.userregister.controller.UserController;
import com.swisscom.userregister.domain.enums.RoleEnum;
import com.swisscom.userregister.domain.request.CreateUserRequest;
import com.swisscom.userregister.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql(value = {"/scripts/user.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/scripts/clear.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserControllerIntegrationTests extends IntegrationTests {

    public static final String DUPLICATE_EMAIL = "samgamgee@theshire.com";
    public static final String USER_EMAIL = "test@email.com";
    public static final String USER_NAME = "test";

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateAdminUser() {

        var createUserRequest = new CreateUserRequest(USER_NAME, USER_EMAIL, RoleEnum.ADMIN);

        getRequest()
                .body(getJson(createUserRequest))
                .post(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", equalTo(USER_NAME))
                .body("email", equalTo(USER_EMAIL));

        var savedUser = userRepository.findByEmail(USER_EMAIL);
        assertTrue(savedUser.isPresent());

        var user = savedUser.get();
        assertNotNull(user.getRole());
        assertEquals(RoleEnum.ADMIN, user.getRole());
    }

    @Test
    void testCreateCommonUser() {

        var createUserRequest = new CreateUserRequest(USER_NAME, USER_EMAIL, RoleEnum.COMMON);

        getRequest()
                .body(getJson(createUserRequest))
                .post(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", equalTo(USER_NAME))
                .body("email", equalTo(USER_EMAIL));

        var savedUser = userRepository.findByEmail(USER_EMAIL);
        assertTrue(savedUser.isPresent());

        var user = savedUser.get();
        assertNotNull(user.getRole());
        assertEquals(RoleEnum.COMMON, user.getRole());
    }

    @Test
    void testCreateUserWhenRoleIsNotInformed() {

        var createUserRequest = new CreateUserRequest(USER_NAME, USER_EMAIL, null);

        getRequest()
                .body(getJson(createUserRequest))
                .post(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", equalTo(USER_NAME))
                .body("email", equalTo(USER_EMAIL));

        var savedUser = userRepository.findByEmail(USER_EMAIL);
        assertTrue(savedUser.isPresent());

        var user = savedUser.get();
        assertNotNull(user.getRole());
        assertEquals(RoleEnum.COMMON, user.getRole());
    }

    @Test
    void testCreateUserWhenEmailAlreadyExist() {

        var createUserRequest = new CreateUserRequest(USER_NAME, DUPLICATE_EMAIL, RoleEnum.ADMIN);
        var expectedMessage = "Email %s already exist!".formatted(DUPLICATE_EMAIL);

        getRequest()
                .body(getJson(createUserRequest))
                .post(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .body("message", equalTo(expectedMessage));

        var savedUser = userRepository.findByEmail(DUPLICATE_EMAIL);
        assertTrue(savedUser.isPresent());
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

        var createUserRequest = new CreateUserRequest("", "", null);

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
    void testListUsers() {
        getRequest()
                .get(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("size", hasSize(4));
    }

}

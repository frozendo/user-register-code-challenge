package com.swisscom.userregister.integationtest;

import com.swisscom.userregister.config.IntegrationTests;
import com.swisscom.userregister.controller.UserController;
import com.swisscom.userregister.domain.enums.RoleEnum;
import com.swisscom.userregister.domain.request.CreateUserRequest;
import com.swisscom.userregister.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserControllerIntegrationTests extends IntegrationTests {

    public static final String CREATE_ADMIN_USER_EMAIL = "createUser@email.com";
    public static final String DEFAULT_ROLE_EMAIL = "usingDefaultRole@email.com";
    public static final String COMMON_USER_EMAIL = "commonUser@email.com";
    public static final String DUPLICATE_EMAIL = "samgamgee@theshire.com";
    public static final String USER_NAME = "test";
    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateAdminUser() {

        var createUserRequest = new CreateUserRequest(USER_NAME, CREATE_ADMIN_USER_EMAIL, RoleEnum.ADMIN);

        getRequest()
                .body(getJson(createUserRequest))
                .post(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", equalTo(USER_NAME))
                .body("email", equalTo(CREATE_ADMIN_USER_EMAIL));

        var savedUser = userRepository.findByEmail(CREATE_ADMIN_USER_EMAIL);
        assertTrue(savedUser.isPresent());

        var user = savedUser.get();
        assertNotNull(user.getRole());
        assertEquals(RoleEnum.ADMIN, user.getRole());
    }

    @Test
    void testCreateCommonUser() {

        var createUserRequest = new CreateUserRequest(USER_NAME, COMMON_USER_EMAIL, RoleEnum.COMMON);

        getRequest()
                .body(getJson(createUserRequest))
                .post(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", equalTo(USER_NAME))
                .body("email", equalTo(COMMON_USER_EMAIL));

        var savedUser = userRepository.findByEmail(COMMON_USER_EMAIL);
        assertTrue(savedUser.isPresent());

        var user = savedUser.get();
        assertNotNull(user.getRole());
        assertEquals(RoleEnum.COMMON, user.getRole());
    }

    @Test
    void testCreateUserWhenRoleIsNotInformed() {

        var createUserRequest = new CreateUserRequest(USER_NAME, DEFAULT_ROLE_EMAIL, null);

        getRequest()
                .body(getJson(createUserRequest))
                .post(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", equalTo(USER_NAME))
                .body("email", equalTo(DEFAULT_ROLE_EMAIL));

        var savedUser = userRepository.findByEmail(DEFAULT_ROLE_EMAIL);
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
                .body("name", equalTo("BusinessException"))
                .body("message", equalTo(expectedMessage));

        var savedUser = userRepository.findByEmail(CREATE_ADMIN_USER_EMAIL);
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

}

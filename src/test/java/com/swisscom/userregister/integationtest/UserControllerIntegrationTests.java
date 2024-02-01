package com.swisscom.userregister.integationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swisscom.userregister.config.IntegrationTests;
import com.swisscom.userregister.config.properties.OpaServerProperties;
import com.swisscom.userregister.controller.UserController;
import com.swisscom.userregister.domain.entity.User;
import com.swisscom.userregister.domain.enums.RoleEnum;
import com.swisscom.userregister.domain.request.CreateUserRequest;
import com.swisscom.userregister.repository.UserRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

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
    public static final String PASSWORD = "123456";
    public static final String SYNCHRONIZE_EMAIL = "validateSynchroinizeToOpa@test.com";
    public static final String USER_NAME = "test";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebClient client;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private OpaServerProperties opaServerProperties;

    @Test
    void testListUsers() {
        getRequest()
                .get(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("size", hasSize(5));
    }

    @Test
    void testCreateUserWhenParameterDataIsNull() {
        var createUserRequest = new CreateUserRequest(null, null, null, null);

        getRequest()
                .body(getJson(createUserRequest))
                .post(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("size", hasSize(3));

    }

    @Test
    void testCreateUserWhenParameterDataIsEmpty() {
        var createUserRequest = new CreateUserRequest("", "", "",  null);

        getRequest()
                .body(getJson(createUserRequest))
                .post(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("size", hasSize(3));
    }

    @Test
    void testCreateAdminUser() {
        var createUserRequest = new CreateUserRequest(USER_NAME, USER_EMAIL, PASSWORD, RoleEnum.ADMIN);

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
        assertNotNull(user.getPassword());
    }

    @Test
    void testCreateCommonUser() {
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

        var savedUser = userRepository.findByEmail(USER_EMAIL);
        assertTrue(savedUser.isPresent());

        var user = savedUser.get();
        assertNotNull(user.getRole());
        assertEquals(RoleEnum.COMMON, user.getRole());
        assertNotNull(user.getPassword());
    }

    @Test
    void testCreateUserWhenRoleWasNotInformed() {
        var createUserRequest = new CreateUserRequest(USER_NAME, USER_EMAIL, PASSWORD, null);

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
        assertNotNull(user.getPassword());
    }

    @Test
    void testCreateUserWhenEmailAlreadyExist() {
        var createUserRequest = new CreateUserRequest(USER_NAME, DUPLICATE_EMAIL, PASSWORD, RoleEnum.ADMIN);
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
    void testUserSynchronizedToOpaServer() throws JSONException {
        var createUserRequest = new CreateUserRequest(USER_NAME, SYNCHRONIZE_EMAIL, PASSWORD, RoleEnum.ADMIN);

        getRequest()
                .body(getJson(createUserRequest))
                .post(UserController.PATH)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", equalTo(USER_NAME))
                .body("email", equalTo(SYNCHRONIZE_EMAIL));

        var savedUser = userRepository.findByEmail(SYNCHRONIZE_EMAIL);
        assertTrue(savedUser.isPresent());

        var user = savedUser.get();
        assertNotNull(user.getRole());
        assertEquals(RoleEnum.ADMIN, user.getRole());
        assertNotNull(user.getPassword());

        var userFromOpa = getSynchronizeEmailInOpaServer();
        assertTrue(userFromOpa.isPresent());
        assertEquals(RoleEnum.ADMIN, userFromOpa.get().getRole());
    }

    private Optional<User> getSynchronizeEmailInOpaServer() throws JSONException {
        var uri = opaServerProperties.getUserRolesUri();
        var usersJson = client.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        var responseArray = new JSONObject(usersJson);
        var results = responseArray.getJSONObject("result");

        for (int i = 0; i < results.names().length(); i++) {
            var key = results.names().getString(i);
            if (SYNCHRONIZE_EMAIL.equals(key)) {
                var value = results.getString(key);
                return Optional.of(new User("", key, "", RoleEnum.getEnumValue(value)));
            }
        }

        return Optional.empty();
    }

}

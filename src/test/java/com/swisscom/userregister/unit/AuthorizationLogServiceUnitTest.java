package com.swisscom.userregister.unit;

import com.swisscom.userregister.domain.entity.AuthorizationLog;
import com.swisscom.userregister.repository.AuthorizationLogRepository;
import com.swisscom.userregister.service.AuthorizationLogService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class AuthorizationLogServiceUnitTest {


    private static final String TARGET_PATH = "users/register/allow";
    private static final String WRONG_PATH = "error/allow";

    private final AuthorizationLogRepository repository;
    private final AuthorizationLogService service;

    AuthorizationLogServiceUnitTest() {
        this.repository = mock(AuthorizationLogRepository.class);
        this.service = new AuthorizationLogService(repository);
    }

    @Test
    void testSaveValidAuthorizationLog() {
        var inputObject = new JSONObject();
        inputObject.put("action", "read");
        inputObject.put("email", "test@email.com");

        var jsonObject = new JSONObject();
        jsonObject.put("decision_id", "123456789");
        jsonObject.put("path", TARGET_PATH);
        jsonObject.put("result", true);
        jsonObject.put("input", inputObject);

        service.saveLog(jsonObject);

        verify(repository, times(1)).save(any(AuthorizationLog.class));
    }

    @Test
    void testSaveWithWrongPath() {
        var inputObject = new JSONObject();
        inputObject.put("action", "read");
        inputObject.put("email", "test@email.com");

        var jsonObject = new JSONObject();
        jsonObject.put("decision_id", "123456789");
        jsonObject.put("path", WRONG_PATH);
        jsonObject.put("result", true);
        jsonObject.put("input", inputObject);

        service.saveLog(jsonObject);

        verifyNoInteractions(repository);
    }

    @Test
    void testSaveWithWrongInput() {
        var inputObject = new JSONObject();
        inputObject.put("action", "read");
        inputObject.put("email", "test@email.com");

        var jsonObject = new JSONObject();
        jsonObject.put("input", inputObject);

        service.saveLog(jsonObject);

        verifyNoInteractions(repository);
    }
}
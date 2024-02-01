package com.swisscom.userregister.service;

import com.swisscom.userregister.domain.convert.AuthorizationResultEnumConverter;
import com.swisscom.userregister.repository.AuthorizationLogRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationLogService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationLogService.class);

    private static final String TARGET_PATH = "users/register/allow";
    private final AuthorizationLogRepository repository;

    public AuthorizationLogService(AuthorizationLogRepository repository) {
        this.repository = repository;
    }

    public void saveLog(JSONObject requestBody) {
        try {
            var authorizationLog = AuthorizationResultEnumConverter.convertLogAuthorizationRequest(requestBody);
            if (authorizationLog.getPath().equals(TARGET_PATH)) {
                repository.save(authorizationLog);
            }
        } catch (Exception ex) {
            logger.error("Error on process logs authorization! Ignoring request");
        }
    }

}

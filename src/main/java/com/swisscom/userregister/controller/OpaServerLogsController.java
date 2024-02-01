package com.swisscom.userregister.controller;

import com.swisscom.userregister.service.AuthorizationLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

@RestController
@RequestMapping("/authentication/logs")
public class OpaServerLogsController {

    private static final Logger logger = LoggerFactory.getLogger(OpaServerLogsController.class);

    private final AuthorizationLogService service;

    public OpaServerLogsController(AuthorizationLogService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void authenticationLogs(HttpServletRequest request) {
        var requestBody = extractJsonBody(request);
        if (requestBody != null) {
            service.saveLog(requestBody);
        }
    }

    private JSONObject extractJsonBody(HttpServletRequest request) {
        try {
            final InputStream in = new GZIPInputStream(request.getInputStream());
            var bytes = in.readAllBytes();
            String requestBody = new String(bytes, StandardCharsets.UTF_8);
            return new JSONObject(requestBody.substring(1, requestBody.length() - 1));
        } catch (IOException ex) {
            logger.error("Error on extract logs information! Ignoring request");
        }
        return null;
    }

}

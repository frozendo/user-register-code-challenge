package com.swisscom.userregister.security;

import com.swisscom.userregister.domain.enums.ApiActionEnum;
import com.swisscom.userregister.service.OpaServerService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class OpaAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final Logger logger = LoggerFactory.getLogger(OpaAuthorizationManager.class);

    private final OpaServerService opaServerService;

    public OpaAuthorizationManager(OpaServerService opaServerService) {
        this.opaServerService = opaServerService;
    }

    @Override
    public void verify(Supplier<Authentication> authentication,
                       RequestAuthorizationContext requestAuthorizationContext) {
        AuthorizationManager.super.verify(authentication, requestAuthorizationContext);
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication,
                                       RequestAuthorizationContext requestAuthorizationContext) {
        var request = requestAuthorizationContext.getRequest();
        var token = extractBearerToken(request);
        if (token.isEmpty()) {
            logger.warn("Header token not provided! Deny request");
            return new AuthorizationDecision(false);
        }

        return validateUserRoles(request, token);
    }

    private String extractBearerToken(HttpServletRequest request) {
        var headerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (headerToken == null || headerToken.isEmpty()) {
            return "";
        }
        return headerToken.replace("Bearer ", "");
    }

    private ApiActionEnum defineAction(HttpServletRequest request) {
        var method = request.getMethod();
        if (HttpMethod.GET.name().equals(method)) {
            return ApiActionEnum.READ;
        }
        return ApiActionEnum.WRITE;
    }

    private AuthorizationDecision validateUserRoles(HttpServletRequest request, String token) {
        var action = defineAction(request);

        logger.info("Check token {} has authorization to perform the {} action", token, action);
        var authorizationResult = opaServerService.authorizeUserAction(token, action);

        logger.info("Token {} is authorized", token);
        return new AuthorizationDecision(authorizationResult);
    }
}

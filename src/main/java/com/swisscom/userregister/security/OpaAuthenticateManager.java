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
public class OpaAuthenticateManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final Logger logger = LoggerFactory.getLogger(OpaAuthenticateManager.class);

    private final OpaServerService opaServerService;

    public OpaAuthenticateManager(OpaServerService opaServerService) {
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
        var requestUri = request.getRequestURI();

        if (requestUri.contains("/error")) {
            logger.warn("Uri don't need authorization");
            return new AuthorizationDecision(true);
        }
        var headerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (headerToken == null || headerToken.isEmpty()) {
            logger.warn("Header token not provided! Deny request");
            return new AuthorizationDecision(false);
        }
        var action = defineAction(request);
        logger.info("Check {} authorization to perform the {} action", headerToken, action);
        var authorizationResult = opaServerService.authorizeUserAction(headerToken, action);

        logger.info("User {} is authorized = {} to perform the {} the uri {}",
                headerToken, authorizationResult, action, requestUri);
        return new AuthorizationDecision(authorizationResult);
    }

    private ApiActionEnum defineAction(HttpServletRequest request) {
        var method = request.getMethod();
        if (HttpMethod.GET.name().equals(method)) {
            return ApiActionEnum.READ;
        }
        return ApiActionEnum.WRITE;
    }
}

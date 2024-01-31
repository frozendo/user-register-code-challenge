package com.swisscom.userregister.security;

import com.swisscom.userregister.domain.enums.ApiActionEnum;
import com.swisscom.userregister.service.OpaServerService;
import jakarta.servlet.http.HttpServletRequest;
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
        var headerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (headerToken == null || headerToken.isEmpty()) {
            return new AuthorizationDecision(false);
        }
        var action = defineAction(request);
        var authorizationResult = opaServerService.authorizeUserAction(headerToken, action);
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

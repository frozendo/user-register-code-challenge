package com.swisscom.userregister.security;

import com.swisscom.userregister.service.OpaServerService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationConverter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.UUID;

public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationFilter.class);

    private static final String LOGIN_URI = "/login";
    private static final AntPathRequestMatcher DEFAULT_LOGIN_REQUEST_MATCHER = new AntPathRequestMatcher(LOGIN_URI);

    private final BasicAuthenticationConverter authenticationConverter;
    private final OpaServerService opaServerService;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager,
                                      OpaServerService opaServerService) {
        super(DEFAULT_LOGIN_REQUEST_MATCHER, authenticationManager);
        this.opaServerService = opaServerService;
        this.authenticationConverter = new BasicAuthenticationConverter();
        setFilterProcessesUrl(LOGIN_URI);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authRequest = this.authenticationConverter.convert(request);
        return getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    public void successfulAuthentication(HttpServletRequest request,
                                         HttpServletResponse response,
                                         FilterChain chain,
                                         Authentication authentication) {
        final var principal = (User) authentication.getPrincipal();
        var token = generateAndRegisterToken(principal.getUsername());
        var bearer = "Bearer ".concat(token);
        response.addHeader("access-control-expose-headers", HttpHeaders.AUTHORIZATION);
        response.addHeader(HttpHeaders.AUTHORIZATION, bearer);
    }

    public String generateAndRegisterToken(String email) {
        log.info("Generate a token for {} and send token to OPA server!", email);
        var token = generateOpaqueToken();
        opaServerService.synchronizeTokenToOpa(token, email);
        return token;
    }

    private String generateOpaqueToken() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "");
    }

}

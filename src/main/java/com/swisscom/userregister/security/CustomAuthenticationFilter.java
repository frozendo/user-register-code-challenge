package com.swisscom.userregister.security;

import com.swisscom.userregister.service.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    private static final AntPathRequestMatcher DEFAULT_LOGIN_REQUEST_MATCHER = new AntPathRequestMatcher("/login");

    private final BasicAuthenticationConverter authenticationConverter;
    private final SessionService sessionService;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager,
                                      SessionService sessionService) {
        super(DEFAULT_LOGIN_REQUEST_MATCHER, authenticationManager);
        this.authenticationConverter = new BasicAuthenticationConverter();
        this.sessionService = sessionService;
        setFilterProcessesUrl("/login");
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
        var token = sessionService.generateAndRegisterToken(principal.getUsername());
        var bearer = "Bearer ".concat(token);
        response.addHeader("access-control-expose-headers", HttpHeaders.AUTHORIZATION);
        response.addHeader(HttpHeaders.AUTHORIZATION, bearer);
    }

}
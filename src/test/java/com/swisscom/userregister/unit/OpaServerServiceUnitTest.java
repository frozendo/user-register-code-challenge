package com.swisscom.userregister.unit;

import com.swisscom.userregister.config.properties.OpaServerProperties;
import com.swisscom.userregister.domain.entity.User;
import com.swisscom.userregister.domain.enums.ApiActionEnum;
import com.swisscom.userregister.domain.enums.RoleEnum;
import com.swisscom.userregister.service.OpaServerService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OpaServerServiceUnitTest {

    private final WebClient webClient;
    private final OpaServerService opaServerService;
    private final WebClient.ResponseSpec responseSpec;

    public OpaServerServiceUnitTest() {
        this.webClient = mock(WebClient.class);
        var opaServerProperties = createOpaServerProperties();

        this.responseSpec = mock(WebClient.ResponseSpec.class);

        this.opaServerService = new OpaServerService(webClient, opaServerProperties);

        configureWebClientMocks();
    }

    private static OpaServerProperties createOpaServerProperties() {
        return new OpaServerProperties("localhsot:8080",
                "/v1/policies/register",
                "/v1/data/role_grants",
                "/v1/data/user_roles",
                "/v1/data/users/register/allow");
    }

    private void configureWebClientMocks() {
        var requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        var requestBodySpec = mock(WebClient.RequestBodySpec.class);
        var requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);

        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(any(MediaType.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void testSynchronizedUserToOpa() {
        var userAlice = new User("Alice", "alice@email.com", RoleEnum.ADMIN);
        var userBob = new User("Bob", "bob@email.com", RoleEnum.COMMON);
        var users = List.of(userAlice, userBob);

        Mono<String> mono = Mono.just("");

        when(responseSpec.bodyToMono(String.class)).thenReturn(mono);

        opaServerService.synchronizedUserToOpa(users);

        verify(webClient, times(1)).put();
    }

    @Test
    void testAuthorizeUserActionWhenUserHasRight() {
        var response = "{'result':true}";
        Mono<String> mono = Mono.just(response);

        when(responseSpec.bodyToMono(String.class)).thenReturn(mono);

        var result = opaServerService.authorizeUserAction("alice@email.com", ApiActionEnum.READ);

        assertTrue(result);

        verify(webClient, times(1)).post();

    }

    @Test
    void testAuthorizeUserActionWhenUserHasNoRight() {
        var response = "{'result':false}";
        Mono<String> mono = Mono.just(response);

        when(responseSpec.bodyToMono(String.class)).thenReturn(mono);

        var result = opaServerService.authorizeUserAction("alice@email.com", ApiActionEnum.READ);

        assertFalse(result);

        verify(webClient, times(1)).post();

    }

}
package com.swisscom.userregister.unit;

import com.swisscom.userregister.config.properties.OpaServerProperties;
import com.swisscom.userregister.domain.entity.User;
import com.swisscom.userregister.domain.enums.RoleEnum;
import com.swisscom.userregister.service.OpaUserService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OpaUserServiceTest {

    private final WebClient webClient;
    private final OpaUserService opaUserService;

    public OpaUserServiceTest() {
        this.webClient = mock(WebClient.class);
        var opaServerProperties = new OpaServerProperties("localhsot:8080", "/v1/data/user_roles");

        this.opaUserService = new OpaUserService(webClient, opaServerProperties);

        configureWebClientMocks();
    }

    private void configureWebClientMocks() {
        var requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        var requestBodySpec = mock(WebClient.RequestBodySpec.class);
        var requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        var responseSpec = mock(WebClient.ResponseSpec.class);
        var monoResponse = mock(Mono.class);

        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(any(MediaType.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(monoResponse);
    }

    @Test
    void testSynchronizedUserToOpa() {
        var userAlice = new User("Alice", "alice@email.com", RoleEnum.ADMIN);
        var userBob = new User("Bob", "bob@email.com", RoleEnum.COMMON);

        var users = List.of(userAlice, userBob);

        opaUserService.synchronizedUserToOpa(users);

        verify(webClient, times(1)).put();
    }

}
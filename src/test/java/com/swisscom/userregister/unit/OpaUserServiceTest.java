package com.swisscom.userregister.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swisscom.userregister.domain.entity.User;
import com.swisscom.userregister.domain.enums.RoleEnum;
import com.swisscom.userregister.domain.exceptions.BusinessException;
import com.swisscom.userregister.service.OpaUserService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class OpaUserServiceTest {

    private final ObjectMapper objectMapper;
    private final WebClient webClient;
    private final WebClient.RequestBodyUriSpec requestBodyUriSpec;
    private final WebClient.RequestBodySpec requestBodySpec;
    private final WebClient.RequestHeadersSpec requestHeadersSpec;
    private final WebClient.ResponseSpec responseSpec;
    private final Mono<String> monoResponse;
    private final OpaUserService opaUserService;

    public OpaUserServiceTest() {
        this.objectMapper = mock(ObjectMapper.class);
        this.webClient = mock(WebClient.class);
        this.opaUserService = new OpaUserService(objectMapper, webClient,
                "locahost:8080", "/v1/data/user_roles");

        this.requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        this.requestBodySpec = mock(WebClient.RequestBodySpec.class);
        this.requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        this.responseSpec = mock(WebClient.ResponseSpec.class);
        this.monoResponse = mock(Mono.class);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.accept(any(MediaType.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(monoResponse);
    }

    @Test
    void testSynchronizedUserToOpa() throws JsonProcessingException {
        var userAlice = new User("Alice", "alice@email.com", RoleEnum.ADMIN);
        var userBob = new User("Bob", "bob@email.com", RoleEnum.COMMON);

        var users = List.of(userAlice, userBob);

        opaUserService.synchronizedUserToOpa(users);

        verify(objectMapper, times(1)).writeValueAsString(any(List.class));
        verify(webClient, times(1)).post();
    }

    @Test
    void testSynchronizedUserToOpaWhenAnErrorOccur() throws JsonProcessingException {
        var userAlice = new User("Alice", "alice@email.com", RoleEnum.ADMIN);
        var userBob = new User("Bob", "bob@email.com", RoleEnum.COMMON);

        var users = List.of(userAlice, userBob);

        when(objectMapper.writeValueAsString(any(List.class))).thenThrow(JsonProcessingException.class);

        var exception = assertThrows(BusinessException.class,
                () -> opaUserService.synchronizedUserToOpa(users));

        assertNotNull(exception);
        assertNotNull(exception.getMessage());
        assertEquals("Error on synchronizing users to OPA server", exception.getMessage());

        verify(objectMapper, times(1)).writeValueAsString(any(List.class));
        verifyNoInteractions(webClient);
    }

}
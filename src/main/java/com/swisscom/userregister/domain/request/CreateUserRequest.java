package com.swisscom.userregister.domain.request;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank(message = "User name is mandatory!") String name,
        @NotBlank(message = "User email is mandatory!") String email,
        String role) {
}

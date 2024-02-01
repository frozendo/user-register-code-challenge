package com.swisscom.userregister.domain.request;

import com.swisscom.userregister.domain.entity.User;
import com.swisscom.userregister.domain.enums.RoleEnum;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public record CreateUserRequest(
        @NotBlank(message = "User name is mandatory!") String name,
        @NotBlank(message = "User email is mandatory!") String email,
        @NotBlank(message = "Password is mandatory") String password,
        RoleEnum role) {

    public User convertToEntity() {
        var userRole = Objects.requireNonNullElse(role, RoleEnum.COMMON);
        return new User(name, email, password, userRole);
    }

}

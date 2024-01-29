package com.swisscom.userregister.domain.response;

import com.swisscom.userregister.domain.entity.User;

public record UserResponse(String name, String email) {

    public UserResponse(User user) {
        this(user.getName(), user.getEmail());
    }

}

package com.swisscom.userregister.domain.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record OpaUserRequest(String email, String role) {

    @JsonAnyGetter
    public Map<String, String> convertToJson(List<OpaUserRequest> users) {
        return users.stream().collect(
                Collectors.toMap(OpaUserRequest::email, OpaUserRequest::role)
        );
    }

}

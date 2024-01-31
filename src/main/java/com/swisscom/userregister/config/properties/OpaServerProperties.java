package com.swisscom.userregister.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "opa")
public record OpaServerProperties(String server,
                                  String policyEndpoint,
                                  String rolesEndpoint,
                                  String usersEndpoint,
                                  String authorizeEndpoint) {

    public String getUserDataUri() {
        return server.concat(usersEndpoint);
    }

    public String getAuthorizeUri() {
        return server.concat(authorizeEndpoint);
    }

}

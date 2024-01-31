package com.swisscom.userregister.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "opa")
public record OpaServerProperties(String server, String usersEndpoint) {

    public String getUserDataUri() {
        return server.concat(usersEndpoint);
    }

}

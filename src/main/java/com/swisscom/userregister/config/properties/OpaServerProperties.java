package com.swisscom.userregister.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "opa")
public record OpaServerProperties(String server,
                                  String policyEndpoint,
                                  String dataEndpoint,
                                  String userRolesEndpoint,
                                  String authorizeEndpoint) {

    public String getUserRolesUri() {
        return server.concat(dataEndpoint).concat(userRolesEndpoint);
    }

    public String getAuthorizeUri() {
        return server.concat(authorizeEndpoint);
    }

}

package com.local.core.user.keycloak;

import static org.keycloak.OAuth2Constants.PASSWORD;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.stereotype.Service;

@Service
public class KeycloakFactoryService {
    final KeycloakBuilder builder;
    final KeycloakDefaultAdminConfig defaultAdminConfig;

    KeycloakFactoryService(KeycloakConfig config) {
        KeycloakBuilder builder = KeycloakBuilder.builder()
                .serverUrl(config.getHost())
                .realm(config.getDefaultRealm())
                .clientId(config.getClientId());

        String clientSecret = config.getClientSecret();
        if (clientSecret != null && !clientSecret.isBlank()) {
            builder = builder.clientSecret(clientSecret);
        }
        this.builder = builder;
        defaultAdminConfig = config.getDefaultAdmin();
    }

    public Keycloak getDefaultAdminInstance() {
        return getInstance(
                defaultAdminConfig.getUsername(),
                defaultAdminConfig.getPassword());
    }

    Keycloak getInstance(String username, String password) {
        return builder
                .grantType(PASSWORD)
                .username(username)
                .password(password)
                .resteasyClient(new ResteasyClientBuilderImpl()
                        .connectionPoolSize(10)
                        .build())
                .build();
    }
}

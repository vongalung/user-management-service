package com.local.core.user.keycloak;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.keycloak")
@Getter
@Setter
public class KeycloakConfig {
    @NotBlank
    private String host;
    @NotBlank
    private String clientId;
    private String clientSecret;
    @NotBlank
    private String defaultRealm;
    @NotBlank
    private String defaultUserRole;

    @NotNull
    @Valid
    private KeycloakDefaultAdminConfig defaultAdmin;

    @NotNull
    private String[] adminUserRoles;
}

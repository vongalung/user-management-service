package com.local.core.user.keycloak;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeycloakDefaultAdminConfig {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}

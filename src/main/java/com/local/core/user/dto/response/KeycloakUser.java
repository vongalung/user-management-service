package com.local.core.user.dto.response;

public record KeycloakUser(
        String userId,
        String userName,
        String firstName,
        String lastName,
        Boolean emailVerified,
        Boolean phoneVerified
) { }

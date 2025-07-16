package com.local.core.user.dto.kafka;

import java.util.UUID;

public record UserWipeDto(
        UUID userId,
        String keycloakUserId
) { }

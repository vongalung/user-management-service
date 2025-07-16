package com.local.core.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.ZonedDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserSearchResponseDto(
        UUID id,
        String userName,
        String fullName,
        String email,
        String phone,
        ZonedDateTime joinedSince,
        boolean isActive
) { }

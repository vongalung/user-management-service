package com.local.core.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserInfoResponseDto(
        UUID id,
        String userName,
        String fullName,
        String email,
        Boolean isEmailVerified,
        String phone,
        Boolean isPhoneVerified,
        Set<String> scopes,
        ZonedDateTime joinedSince,
        Boolean isActive
) { }

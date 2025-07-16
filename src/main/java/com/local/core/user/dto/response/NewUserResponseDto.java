package com.local.core.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NewUserResponseDto(
        String userName,
        String email,
        ZonedDateTime createdAt
) { }

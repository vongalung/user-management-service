package com.local.core.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserActivationResponseDto(
        boolean isActivated
) { }

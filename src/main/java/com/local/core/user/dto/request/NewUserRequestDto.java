package com.local.core.user.dto.request;

import static com.local.core.user.common.CommonPattern.PHONE_PATTERN;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record NewUserRequestDto(
        @NotBlank
        String userName,
        @NotBlank
        @Email
        String email,
        @NotBlank
        String password,
        @NotBlank
        String firstname,
        @NotBlank
        String lastName,
        @Pattern(regexp = PHONE_PATTERN)
        String phone
) { }

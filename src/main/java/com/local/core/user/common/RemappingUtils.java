package com.local.core.user.common;

import static java.util.stream.Collectors.toSet;

import com.local.core.user.dto.response.KeycloakUser;
import com.local.core.user.dto.response.UserInfoResponseDto;
import com.local.core.user.dto.response.UserSearchResponseDto;
import com.local.core.user.model.AccessScope;
import com.local.core.user.model.User;
import com.local.core.user.model.UserDetail;
import org.springframework.security.oauth2.jwt.Jwt;

public class RemappingUtils {
    public static UserSearchResponseDto remapSearchResponse(User user) {
        UserDetail detail = user.getDetails();
        return new UserSearchResponseDto(
                user.getId(),
                user.getUserName(),
                detail.getFullName(),
                detail.getEmail(),
                detail.getPhone(),
                user.getCreatedDate(),
                user.getInactiveSince() == null);
    }

    public static UserInfoResponseDto remapUserInfo(Jwt token, User user) {
        UserDetail detail = user.getDetails();
        Boolean emailVerified = token.getClaimAsBoolean("email_verified");
        Boolean phoneVerified = token.getClaimAsBoolean("phone_verified");
        return new UserInfoResponseDto(
                user.getId(),
                user.getUserName(),
                detail.getFullName(),
                detail.getEmail(),
                emailVerified,
                detail.getPhone(),
                phoneVerified,
                user.getScopes().stream().map(AccessScope::getName).collect(toSet()),
                user.getCreatedDate(),
                user.getInactiveSince() == null);
    }

    public static UserInfoResponseDto remapUserInfo(KeycloakUser keycloakUser, User user) {
        UserDetail detail = user.getDetails();
        return new UserInfoResponseDto(
                user.getId(),
                user.getUserName(),
                detail.getFullName(),
                detail.getEmail(),
                keycloakUser.emailVerified(),
                detail.getPhone(),
                keycloakUser.phoneVerified(),
                user.getScopes().stream().map(AccessScope::getName).collect(toSet()),
                user.getCreatedDate(),
                user.getInactiveSince() == null);
    }
}

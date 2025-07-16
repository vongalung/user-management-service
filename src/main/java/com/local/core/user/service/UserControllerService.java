package com.local.core.user.service;

import static com.local.core.user.common.RemappingUtils.remapUserInfo;

import com.local.core.user.dto.request.NewUserRequestDto;
import com.local.core.user.dto.response.KeycloakUser;
import com.local.core.user.dto.response.NewUserResponseDto;
import com.local.core.user.dto.response.UserInactivationResponseDto;
import com.local.core.user.dto.response.UserInfoResponseDto;
import com.local.core.user.exception.AuthUserNotFoundException;
import com.local.core.user.exception.BaseApplicationException;
import com.local.core.user.exception.UserAlreadyExistsException;
import com.local.core.user.keycloak.KeycloakService;
import com.local.core.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class UserControllerService {
    final KeycloakService keycloakService;
    final UserService userService;
    final AuthContextService authContextService;
    final UserActivationService userActivationService;

    @Transactional
    public NewUserResponseDto signup(NewUserRequestDto request)
            throws BaseApplicationException {
        userService.findByUniqueIdentifier(request).ifPresent(u -> {
            throw new UserAlreadyExistsException();
        });
        KeycloakUser keycloakUser = keycloakService.addUser(request);
        User user = userService.createNewUser(request, keycloakUser);
        return new NewUserResponseDto(
                user.getUserName(),
                user.getDetails().getEmail(),
                user.getCreatedDate());
    }

    public UserInfoResponseDto userInfo() throws BaseApplicationException {
        Jwt token = authContextService.getToken().orElseThrow();
        User user = authContextService.findUserFromToken(token)
                .orElseThrow(AuthUserNotFoundException::new);
        return remapUserInfo(token, user);
    }

    @Transactional
    public UserInactivationResponseDto inactivateUser(boolean permanently)
            throws BaseApplicationException {
        User user = authContextService.findUserFromToken()
                .orElseThrow(AuthUserNotFoundException::new);
        user = userActivationService.inactivateUser(user, permanently);
        return new UserInactivationResponseDto(
                user == null ? ZonedDateTime.now() : user.getInactiveSince(),
                user == null);
    }
}

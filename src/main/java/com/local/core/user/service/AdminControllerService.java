package com.local.core.user.service;

import com.local.core.user.common.PaginationFactory;
import com.local.core.user.common.RemappingUtils;
import com.local.core.user.dto.request.UserSearchRequestDto;
import com.local.core.user.dto.response.*;
import com.local.core.user.exception.BaseApplicationException;
import com.local.core.user.exception.ProvidedUserNotFoundException;
import com.local.core.user.exception.UserAlreadyActiveException;
import com.local.core.user.keycloak.KeycloakService;
import com.local.core.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AdminControllerService {
    final UserService userService;
    final UserActivationService userActivationService;
    final KeycloakService keycloakService;

    public Page<UserSearchResponseDto> search(UserSearchRequestDto request) {
        PaginationFactory<User> paginationFactory = userService.findByParameters(request);
        try (Stream<User> stream = paginationFactory.stream()) {
            return paginationFactory
                    .replacePagination(stream.map(RemappingUtils::remapSearchResponse))
                    .finalizePage();
        }
    }

    public UserInfoResponseDto findById(UUID userId) throws BaseApplicationException {
        User user = userService.findById(userId)
                .orElseThrow(ProvidedUserNotFoundException::new);
        KeycloakUser keycloakUser = keycloakService.findById(user.getKeycloakUserId());
        return RemappingUtils.remapUserInfo(keycloakUser, user);
    }

    @Transactional
    public UserActivationResponseDto activateUser(UUID userId) throws BaseApplicationException {
        User user = userService.findById(userId)
                .orElseThrow(ProvidedUserNotFoundException::new);
        if (user.getInactiveSince() == null) {
            throw new UserAlreadyActiveException();
        }
        user = userActivationService.activateUser(user);
        return new UserActivationResponseDto(user.getInactiveSince() == null);
    }

    @Transactional
    public UserInactivationResponseDto inactivateUser(UUID userId, boolean permanently)
            throws BaseApplicationException {
        User user = userService.findById(userId)
                .orElseThrow(ProvidedUserNotFoundException::new);
        user = userActivationService.inactivateUser(user, permanently);
        return new UserInactivationResponseDto(
                user == null ? ZonedDateTime.now() : user.getInactiveSince(),
                user == null);
    }
}

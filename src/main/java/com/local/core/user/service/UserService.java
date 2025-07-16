package com.local.core.user.service;

import com.local.core.user.common.PaginationFactory;
import com.local.core.user.dto.request.NewUserRequestDto;
import com.local.core.user.dto.request.UserSearchRequestDto;
import com.local.core.user.dto.response.KeycloakUser;
import com.local.core.user.model.AccessScope;
import com.local.core.user.model.User;
import com.local.core.user.model.UserDetail;
import com.local.core.user.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    final AccessScopeService accessScopeService;
    final UserRepo userRepo;

    public Optional<User> findById(UUID userId) {
        return userRepo.findById(userId);
    }

    public Optional<User> findByUniqueIdentifier(NewUserRequestDto request) {
        return userRepo.findByUniqueIdentifier(request);
    }

    public Optional<User> findByKeycloakUserId(String keycloakUserId) {
        return userRepo.findByKeycloakUserId(keycloakUserId);
    }

    public PaginationFactory<User> findByParameters(UserSearchRequestDto parameters) {
        return userRepo.findAllWithParameters(parameters);
    }

    @Transactional
    public User createNewUser(NewUserRequestDto newUser, KeycloakUser keycloakUser) {
        UserDetail detail = new UserDetail();
        detail.setEmail(newUser.email());
        detail.setPhone(newUser.phone());
        detail.setFullName(extractFullName(keycloakUser));

        User user = new User();
        user.setUserName(keycloakUser.userName());
        user.setKeycloakUserId(keycloakUser.userId());
        user.setDetails(detail);
        detail.setUser(user);

        Set<AccessScope> scopes = accessScopeService.getDefault();
        user.getScopes().addAll(scopes);
        return userRepo.saveAndFlush(user);
    }

    String extractFullName(KeycloakUser keycloakUser) {
        return String.join(" ",
                keycloakUser.firstName(), keycloakUser.lastName());
    }

    @Transactional
    public User markInactive(User user) {
        user.setInactiveSince(ZonedDateTime.now());
        return userRepo.saveAndFlush(user);
    }

    @Transactional
    public User activateUser(User user) {
        user.setInactiveSince(null);
        return userRepo.saveAndFlush(user);
    }

    @Transactional
    public void deleteUser(User user) {
        userRepo.delete(user);
    }
}

package com.local.core.user.service;

import com.local.core.user.config.BroadcastTopic;
import com.local.core.user.config.UserManagementConfig;
import com.local.core.user.dto.kafka.UserWipeDto;
import com.local.core.user.exception.BaseApplicationException;
import com.local.core.user.exception.InternalServerErrorException;
import com.local.core.user.keycloak.KeycloakService;
import com.local.core.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserActivationService {
    final KeycloakService keycloakService;
    final UserService userService;
    final KafkaProducerService kafkaProducerService;
    final UserManagementConfig userManagementConfig;

    @Transactional
    public User activateUser(User user) {
        keycloakService.setUserEnable(user.getKeycloakUserId(), true);
        return userService.activateUser(user);
    }

    @Transactional
    public User inactivateUser(User user, boolean permanently)
            throws BaseApplicationException {
        if (user.getInactiveSince() != null) {
            return null;
        }
        if (permanently) {
            wipeUser(user);
            return null;
        }
        return inactivateUser(user);
    }

    @Transactional
    User inactivateUser(User user) {
        keycloakService.setUserEnable(user.getKeycloakUserId(), false);
        return userService.markInactive(user);
    }

    @Transactional
    void wipeUser(User user) throws BaseApplicationException {
        UserWipeDto wipe = new UserWipeDto(user.getId(), user.getKeycloakUserId());
        try {
            userService.deleteUser(user);
            keycloakService.removeUser(wipe.keycloakUserId());
            broadcastUserWipe(wipe)
                    .thenAccept(v -> log.debug(
                            "wiping order sent for user[{}]", wipe))
                    .exceptionally(e -> {
                        log.error(
                                "failed sending wiping order for user[{}]",
                                wipe, e);
                        return null;
                    });
        } catch (Exception e) {
            log.error("failed wiping user[{}]", wipe, e);
            throw new InternalServerErrorException(e);
        }
    }

    CompletableFuture<Void> broadcastUserWipe(UserWipeDto wipe) {
        String userWipeTopic = getUserWipeTopic();
        if (userWipeTopic == null || userWipeTopic.isBlank()) {
            log.info("not sending wiping order for user[id={}, keycloak={}]",
                    wipe.userId(), wipe.keycloakUserId());
            return CompletableFuture.completedFuture(null);
        }
        log.info("sending wiping order for user[id={}, keycloak={}]",
                wipe.userId(), wipe.keycloakUserId());
        return kafkaProducerService.sendMessage(userWipeTopic, wipe)
                .thenApply(r -> null);
    }

    String getUserWipeTopic() {
        BroadcastTopic topics = userManagementConfig.getBroadcastTopic();
        if (topics == null) {
            return null;
        }
        return topics.getUserWipe();
    }
}

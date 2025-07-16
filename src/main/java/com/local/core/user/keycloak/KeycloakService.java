package com.local.core.user.keycloak;

import static java.util.Collections.singletonList;
import static org.keycloak.admin.client.CreatedResponseUtil.getCreatedId;

import com.local.core.user.dto.request.NewUserRequestDto;
import com.local.core.user.dto.response.KeycloakUser;
import com.local.core.user.exception.BaseApplicationException;
import com.local.core.user.exception.InternalServerErrorException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class KeycloakService {
    final KeycloakHelperService helperService;
    final KeycloakFactoryService keycloakFactoryService;

    public KeycloakUser addUser(NewUserRequestDto newUser)
            throws BaseApplicationException {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(newUser.userName());
        user.setFirstName(newUser.firstname());
        user.setLastName(newUser.lastName());
        user.setEmail(newUser.email());
        user.setCredentials(singletonList(helperService
                .createPasswordCredentials(newUser.password())));
        user.setEnabled(true);

        try (Keycloak keycloak = keycloakFactoryService.getDefaultAdminInstance();
             Response response = helperService.createUser(keycloak, user)) {
            log.debug("success creating new keycloak user with status={}",
                    response.getStatus());
            String userId = getCreatedId(response);
            ClientRepresentation client = helperService.getClient(keycloak);
            RoleRepresentation role = helperService.getDefaultUserRole(
                    keycloak, client);
            helperService.setRoleToUser(keycloak, client, userId, role);
            return new KeycloakUser(
                    userId,
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    null,
                    null);
        } catch (WebApplicationException cause) {
            throw new InternalServerErrorException(cause);
        }
    }

    public KeycloakUser findById(String userId) {
        try (Keycloak keycloak = keycloakFactoryService.getDefaultAdminInstance()) {
            UserRepresentation user = helperService.findById(keycloak, userId).toRepresentation();
            return new KeycloakUser(
                    user.getId(),
                    user.getUsername(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.isEmailVerified(),
                    null);
        }
    }

    public void setUserEnable(String userId, boolean enable) {
        try (Keycloak keycloak = keycloakFactoryService.getDefaultAdminInstance()) {
            UserResource userResource = helperService.findById(keycloak, userId);
            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(enable);
            userResource.update(user);
        }
    }

    public void removeUser(String userId) {
        try (Keycloak keycloak = keycloakFactoryService.getDefaultAdminInstance()) {
            helperService.findById(keycloak, userId).remove();
        }
    }
}

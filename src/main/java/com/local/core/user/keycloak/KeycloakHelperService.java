package com.local.core.user.keycloak;

import static java.util.Collections.singletonList;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeycloakHelperService {
    final KeycloakConfig config;

    CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    Response createUser(Keycloak keycloak, UserRepresentation user) {
        return keycloak.realm(config.getDefaultRealm()).users()
                .create(user);
    }

    ClientRepresentation getClient(Keycloak keycloak) {
        return keycloak.realm(config.getDefaultRealm()).clients()
                .findByClientId(config.getClientId())
                .getFirst();
    }

    RoleRepresentation getDefaultUserRole(Keycloak keycloak, ClientRepresentation client) {
        return keycloak.realm(config.getDefaultRealm()).clients()
                .get(client.getId()).roles()
                .get(config.getDefaultUserRole())
                .toRepresentation();
    }

    void setRoleToUser(Keycloak keycloak, ClientRepresentation client,
                       String userId, RoleRepresentation roleRepresentation) {
        keycloak.realm(config.getDefaultRealm()).users()
                .get(userId).roles()
                .clientLevel(client.getId())
                .add(singletonList(roleRepresentation));
    }

    UserResource findById(Keycloak keycloak, String id) {
        return keycloak.realm(config.getDefaultRealm()).users().get(id);
    }
}

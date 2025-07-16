package com.local.core.user.config;

import com.local.core.user.keycloak.KeycloakConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Log4j2
public class KeycloakJwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    final KeycloakConfig keycloakConfig;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractRoles(jwt);
        return new JwtAuthenticationToken(jwt, authorities);
    }

    private Collection<GrantedAuthority> extractRoles(Jwt jwt) {
        try (Stream<String> realmAccessRolesStream = extractRealmAccessRoles(jwt);
             Stream<String> realmResourcesRolesStream = extractRealmResourcesRoles(jwt)) {
            return Stream.concat(realmAccessRolesStream, realmResourcesRolesStream)
                    .map(this::adjustRoleName)
                    .map(SimpleGrantedAuthority::new)
                    .map(auth -> (GrantedAuthority) auth)
                    .toList();
        }
    }

    Stream<String> extractRealmAccessRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            @SuppressWarnings("unchecked")
            Collection<String> realmAccessRoles = (Collection<String>) realmAccess.get("roles");
            return realmAccessRoles.stream();
        }
        return Stream.empty();
    }

    Stream<String> extractRealmResourcesRoles(Jwt jwt) {
        Map<String, Object> realmClaims = jwt.getClaimAsMap("resource_access");
        @SuppressWarnings("unchecked")
        Map<String, Object> realmResources = realmClaims == null
                ? null : (Map<String, Object>) realmClaims.get(keycloakConfig.getDefaultRealm());
        if (realmResources != null && realmResources.containsKey("roles")) {
            @SuppressWarnings("unchecked")
            Collection<String> realmResourcesRoles = (Collection<String>) realmResources.get("roles");
            return realmResourcesRoles.stream();
        }
        return Stream.empty();
    }

    String adjustRoleName(String raw) {
        return "ROLE_" + raw;
    }
}

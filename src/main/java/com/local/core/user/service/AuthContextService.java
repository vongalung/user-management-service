package com.local.core.user.service;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import com.local.core.user.exception.BaseApplicationException;
import com.local.core.user.exception.InvalidTokenException;
import com.local.core.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthContextService {
    final UserService userService;

    public Optional<User> findUserFromToken() throws BaseApplicationException {
        return getToken().flatMap(this::findUserFromToken);
    }

    public Optional<User> findUserFromToken(Jwt token) throws BaseApplicationException {
        return userService.findByKeycloakUserId(token.getSubject());
    }

    public Optional<Jwt> getToken() {
        return extractJwt(getContext().getAuthentication());
    }

    Optional<Jwt> extractJwt(Authentication auth) throws InvalidTokenException {
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        if (auth instanceof JwtAuthenticationToken token) {
            return Optional.of(token.getToken());
        }
        log.debug("invalid auth type: {}", auth.getClass().getCanonicalName());
        throw new InvalidTokenException();
    }
}

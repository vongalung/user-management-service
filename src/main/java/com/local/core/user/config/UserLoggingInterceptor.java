package com.local.core.user.config;

import static org.slf4j.MDC.put;
import static org.slf4j.MDC.remove;

import com.local.core.user.model.User;
import com.local.core.user.service.AuthContextService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserLoggingInterceptor implements HandlerInterceptor {
    public final static String LOG_USER_ID_NAME = "userId";

    final AuthContextService authContextService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        onStart();
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        onCompletion();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    void onStart() {
        put(LOG_USER_ID_NAME, authContextService.findUserFromToken()
                .map(User::getId)
                .map(UUID::toString)
                .orElse(null));
    }

    void onCompletion() {
        remove(LOG_USER_ID_NAME);
    }
}

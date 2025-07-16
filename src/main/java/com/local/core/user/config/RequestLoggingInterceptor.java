package com.local.core.user.config;

import static org.slf4j.MDC.put;
import static org.slf4j.MDC.remove;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.UUID;

@Component
@Order(Integer.MIN_VALUE)
public class RequestLoggingInterceptor implements HandlerInterceptor {
    public final static String LOG_REQUEST_ID_NAME = "requestId";

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
        put(LOG_REQUEST_ID_NAME, UUID.randomUUID().toString());
    }

    void onCompletion() {
        remove(LOG_REQUEST_ID_NAME);
    }
}

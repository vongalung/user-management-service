package com.local.core.user.config;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.user-management")
@Getter
@Setter
public class UserManagementConfig {
    private String defaultScopeGroup;
    @Valid
    private BroadcastTopic broadcastTopic;
}

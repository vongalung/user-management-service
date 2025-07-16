package com.local.core.user.config;

import static org.slf4j.MDC.*;

import org.springframework.core.task.TaskDecorator;
import java.util.Map;

public class MdcTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, String> contextMap = getCopyOfContextMap();
        return () -> {
            try {
                setContextMap(contextMap);
                runnable.run();
            } finally {
                clear();
            }
        };
    }
}

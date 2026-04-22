package com.upeu.config;

import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class AssignmentFeignConfig {

    @Bean
    public RequestInterceptor correlationIdRequestInterceptor() {
        return template -> {
            String traceId = MDC.get("traceId");
            if (traceId == null || traceId.isBlank()) {
                traceId = UUID.randomUUID().toString();
            }
            template.header("X-Trace-ID", traceId);
        };
    }
}

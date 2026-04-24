package com.serviya.msservicerequest.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignAuthConfig {

    @Bean
    public RequestInterceptor relayBearerToken() {
        return template -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getCredentials() instanceof String token && !token.isBlank()) {
                template.header("Authorization", "Bearer " + token);
            }
        };
    }
}

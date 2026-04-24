package com.serviya.msservicerequest.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Hidden
@RequestMapping("/api/v1/service-request")
public class GatewayInstanciasController {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${eureka.instance.hostname:unknown}")
    private String hostname;

    @Value("${eureka.instance.instance-id:unknown}")
    private String instanceId;

    private final Environment environment;

    public GatewayInstanciasController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/instancia")
    public Map<String, String> instancia() {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("service", appName);
        response.put("hostname", hostname);
        response.put("instanceId", instanceId);
        response.put("port", environment.getProperty("local.server.port", environment.getProperty("server.port")));
        response.put("profile", String.join(",", environment.getActiveProfiles()));
        return response;
    }
}

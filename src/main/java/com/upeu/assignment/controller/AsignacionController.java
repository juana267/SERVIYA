package com.upeu.assignment.controller;

import com.upeu.assignment.dto.AsignacionResponse;
import com.upeu.assignment.service.AsignacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/asignaciones")
public class AsignacionController {

    private final AsignacionService asignacionService;
    private final Environment environment;

    @PostMapping("/auto/{solicitudId}")
    public ResponseEntity<AsignacionResponse> autoAsignar(@PathVariable Long solicitudId) {
        return ResponseEntity.ok(asignacionService.autoAsignar(solicitudId));
    }

    @GetMapping("/instancia")
    public Map<String, String> instancia() {
        String service = Objects.toString(environment.getProperty("spring.application.name"), "unknown");
        String port = Objects.toString(environment.getProperty("local.server.port"),
                Objects.toString(environment.getProperty("server.port"), "unknown"));
        String instanceId = Objects.toString(environment.getProperty("eureka.instance.instance-id"), "unknown");
        return Map.of("service", service, "port", port, "instanceId", instanceId);
    }
}

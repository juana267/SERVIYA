package com.serviya.msservicerequest.controller;

import com.serviya.msservicerequest.dto.service.CreateServiceRequest;
import com.serviya.msservicerequest.dto.service.ServiceRequestResponse;
import com.serviya.msservicerequest.security.JwtPrincipal;
import com.serviya.msservicerequest.service.ServiceRequestService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/servicios")
@RequiredArgsConstructor
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;

    @PostMapping
    public ResponseEntity<ServiceRequestResponse> create(@Valid @RequestBody CreateServiceRequest request,
                                                         @AuthenticationPrincipal JwtPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceRequestService.create(request, principal.userId()));
    }

    @GetMapping("/{id}")
    @Hidden
    public ResponseEntity<ServiceRequestResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceRequestService.findById(id));
    }

    @GetMapping
    @Hidden
    public ResponseEntity<List<ServiceRequestResponse>> findAll(@RequestParam(required = false) Long categoriaId,
                                                                @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(serviceRequestService.findAll(categoriaId, estado));
    }

    @GetMapping("/mis-solicitudes")
    @Hidden
    public ResponseEntity<List<ServiceRequestResponse>> findMyRequests(@AuthenticationPrincipal JwtPrincipal principal) {
        return ResponseEntity.ok(serviceRequestService.findByClienteId(principal.userId()));
    }

    @GetMapping("/mis-trabajos")
    @Hidden
    public ResponseEntity<List<ServiceRequestResponse>> findMyAssignedJobs(@AuthenticationPrincipal JwtPrincipal principal) {
        return ResponseEntity.ok(serviceRequestService.findByTrabajadorId(principal.userId()));
    }

    @PatchMapping("/{id}/aceptar")
    public ResponseEntity<ServiceRequestResponse> accept(@PathVariable Long id,
                                                         @AuthenticationPrincipal JwtPrincipal principal) {
        return ResponseEntity.ok(serviceRequestService.accept(id, principal.userId()));
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<ServiceRequestResponse> finish(@PathVariable Long id,
                                                         @AuthenticationPrincipal JwtPrincipal principal) {
        return ResponseEntity.ok(serviceRequestService.finish(id, principal.userId()));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ServiceRequestResponse> cancel(@PathVariable Long id,
                                                         @AuthenticationPrincipal JwtPrincipal principal) {
        return ResponseEntity.ok(serviceRequestService.cancel(id, principal.userId()));
    }
}

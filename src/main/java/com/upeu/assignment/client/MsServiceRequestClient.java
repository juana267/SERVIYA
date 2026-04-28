package com.upeu.assignment.client;

import com.upeu.assignment.dto.ActualizarEstadoSolicitudRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-service-request", path = "/api/v1")
public interface MsServiceRequestClient {

    @PutMapping("/solicitudes/{solicitudId}/estado")
    void actualizarEstado(@PathVariable("solicitudId") Long solicitudId,
                          @RequestBody ActualizarEstadoSolicitudRequest request);
}

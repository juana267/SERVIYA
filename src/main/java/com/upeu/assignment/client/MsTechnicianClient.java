package com.upeu.assignment.client;

import com.upeu.assignment.dto.TecnicoCercanoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "ms-technician", path = "/api/v1")
public interface MsTechnicianClient {

    @GetMapping("/tecnicos/cercanos")
    List<TecnicoCercanoDto> obtenerTecnicosCercanos(@RequestParam("solicitudId") Long solicitudId,
                                                    @RequestParam(value = "radioKm", required = false) Double radioKm);
}

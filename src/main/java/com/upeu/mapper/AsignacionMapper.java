package com.upeu.mapper;

import com.upeu.dto.AsignacionResponse;
import com.upeu.entity.Asignacion;
import org.springframework.stereotype.Component;

@Component
public class AsignacionMapper {

    public AsignacionResponse toResponse(Asignacion asignacion) {
        if (asignacion == null) {
            return null;
        }
        return AsignacionResponse.builder()
                .id(asignacion.getId())
                .solicitudId(asignacion.getSolicitudId())
                .tecnicoId(asignacion.getTecnicoId())
                .estado(asignacion.getEstado())
                .rankingTecnico(asignacion.getRankingTecnico())
                .distanciaKm(asignacion.getDistanciaKm())
                .fechaAsignacion(asignacion.getFechaAsignacion())
                .build();
    }
}

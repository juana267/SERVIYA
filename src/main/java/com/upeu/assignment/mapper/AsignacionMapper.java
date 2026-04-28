package com.upeu.assignment.mapper;

import com.upeu.assignment.dto.AsignacionResponse;
import com.upeu.assignment.entity.Asignacion;
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

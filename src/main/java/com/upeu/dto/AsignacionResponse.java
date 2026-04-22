package com.upeu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionResponse {

    private Long id;
    private Long solicitudId;
    private Long tecnicoId;
    private String estado;
    private Double rankingTecnico;
    private Double distanciaKm;
    private LocalDateTime fechaAsignacion;
}

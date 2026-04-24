package com.serviya.msservicerequest.dto.service;

import com.serviya.msservicerequest.dto.category.CategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequestResponse {

    private Long id;
    private Long clienteId;
    private Long trabajadorId;
    private CategoryResponse categoria;
    private String descripcion;
    private String estado;
    private BigDecimal precioAcordado;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String direccionServicio;
    private Double lat;
    private Double lng;
}

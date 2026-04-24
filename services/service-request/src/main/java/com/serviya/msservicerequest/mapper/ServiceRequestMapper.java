package com.serviya.msservicerequest.mapper;

import com.serviya.msservicerequest.dto.service.CreateServiceRequest;
import com.serviya.msservicerequest.dto.service.ServiceRequestResponse;
import com.serviya.msservicerequest.entity.Categoria;
import com.serviya.msservicerequest.entity.Servicio;
import org.springframework.stereotype.Component;

@Component
public class ServiceRequestMapper {

    private final CategoryMapper categoryMapper;

    public ServiceRequestMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public Servicio toEntity(CreateServiceRequest request, Long clienteId, Categoria categoria) {
        return Servicio.builder()
                .clienteId(clienteId)
                .categoria(categoria)
                .descripcion(request.getDescripcion())
                .precioAcordado(request.getPrecioAcordado())
                .direccionServicio(request.getDireccionServicio())
                .lat(request.getLat())
                .lng(request.getLng())
                .build();
    }

    public ServiceRequestResponse toResponse(Servicio servicio) {
        return ServiceRequestResponse.builder()
                .id(servicio.getId())
                .clienteId(servicio.getClienteId())
                .trabajadorId(servicio.getTrabajadorId())
                .categoria(categoryMapper.toResponse(servicio.getCategoria()))
                .descripcion(servicio.getDescripcion())
                .estado(servicio.getEstado().name())
                .precioAcordado(servicio.getPrecioAcordado())
                .fechaSolicitud(servicio.getFechaSolicitud())
                .fechaInicio(servicio.getFechaInicio())
                .fechaFin(servicio.getFechaFin())
                .direccionServicio(servicio.getDireccionServicio())
                .lat(servicio.getLat())
                .lng(servicio.getLng())
                .build();
    }
}

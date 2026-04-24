package com.serviya.msservicerequest.service.impl;

import com.serviya.msservicerequest.dto.service.CreateServiceRequest;
import com.serviya.msservicerequest.dto.service.ServiceRequestResponse;
import com.serviya.msservicerequest.entity.Categoria;
import com.serviya.msservicerequest.entity.EstadoServicio;
import com.serviya.msservicerequest.entity.Servicio;
import com.serviya.msservicerequest.exception.BadRequestException;
import com.serviya.msservicerequest.exception.ResourceNotFoundException;
import com.serviya.msservicerequest.mapper.ServiceRequestMapper;
import com.serviya.msservicerequest.repository.CategoriaRepository;
import com.serviya.msservicerequest.repository.ServicioRepository;
import com.serviya.msservicerequest.service.ServiceRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceRequestServiceImpl implements ServiceRequestService {

    private final ServicioRepository servicioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ServiceRequestMapper serviceRequestMapper;

    @Override
    @Transactional
    public ServiceRequestResponse create(CreateServiceRequest request, Long clienteId) {
        validateActorId(clienteId, "cliente");
        Categoria categoria = getCategoria(request.getCategoriaId());
        Servicio servicio = serviceRequestMapper.toEntity(request, clienteId, categoria);
        Servicio saved = servicioRepository.save(servicio);
        return serviceRequestMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceRequestResponse findById(Long id) {
        return serviceRequestMapper.toResponse(getServicio(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceRequestResponse> findAll(Long categoriaId, String estado) {
        List<Servicio> servicios;
        if (categoriaId != null && estado != null && !estado.isBlank()) {
            servicios = servicioRepository.findByCategoriaIdAndEstadoOrderByFechaSolicitudDesc(categoriaId, parseEstado(estado));
        } else if (categoriaId != null) {
            servicios = servicioRepository.findByCategoriaIdOrderByFechaSolicitudDesc(categoriaId);
        } else if (estado != null && !estado.isBlank()) {
            servicios = servicioRepository.findByEstadoOrderByFechaSolicitudDesc(parseEstado(estado));
        } else {
            servicios = servicioRepository.findAllByOrderByFechaSolicitudDesc();
        }
        return servicios.stream().map(serviceRequestMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceRequestResponse> findByClienteId(Long clienteId) {
        return servicioRepository.findByClienteIdOrderByFechaSolicitudDesc(clienteId)
                .stream()
                .map(serviceRequestMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceRequestResponse> findByTrabajadorId(Long trabajadorId) {
        return servicioRepository.findByTrabajadorIdOrderByFechaSolicitudDesc(trabajadorId)
                .stream()
                .map(serviceRequestMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ServiceRequestResponse accept(Long serviceId, Long trabajadorId) {
        validateActorId(trabajadorId, "trabajador");
        Servicio servicio = getServicio(serviceId);
        if (servicio.getEstado() != EstadoServicio.SOLICITADO) {
            throw new BadRequestException("Solo se puede aceptar un servicio en estado SOLICITADO");
        }
        if (servicio.getClienteId().equals(trabajadorId)) {
            throw new BadRequestException("El cliente no puede aceptar su propia solicitud");
        }
        servicio.setTrabajadorId(trabajadorId);
        servicio.setEstado(EstadoServicio.EN_PROCESO);
        servicio.setFechaInicio(LocalDateTime.now());
        return serviceRequestMapper.toResponse(servicioRepository.save(servicio));
    }

    @Override
    @Transactional
    public ServiceRequestResponse finish(Long serviceId, Long trabajadorId) {
        Servicio servicio = getServicio(serviceId);
        if (servicio.getEstado() != EstadoServicio.EN_PROCESO) {
            throw new BadRequestException("Solo se puede finalizar un servicio en estado EN_PROCESO");
        }
        if (servicio.getTrabajadorId() == null || !servicio.getTrabajadorId().equals(trabajadorId)) {
            throw new BadRequestException("Solo el trabajador asignado puede finalizar el servicio");
        }
        servicio.setEstado(EstadoServicio.FINALIZADO);
        servicio.setFechaFin(LocalDateTime.now());
        return serviceRequestMapper.toResponse(servicioRepository.save(servicio));
    }

    @Override
    @Transactional
    public ServiceRequestResponse cancel(Long serviceId, Long clienteId) {
        Servicio servicio = getServicio(serviceId);
        if (!servicio.getClienteId().equals(clienteId)) {
            throw new BadRequestException("Solo el cliente que creo la solicitud puede cancelarla");
        }
        if (servicio.getEstado() != EstadoServicio.SOLICITADO) {
            throw new BadRequestException("Solo se puede cancelar un servicio antes de su inicio");
        }
        servicio.setEstado(EstadoServicio.CANCELADO);
        return serviceRequestMapper.toResponse(servicioRepository.save(servicio));
    }

    private Servicio getServicio(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio con id " + id + " no encontrado"));
    }

    private Categoria getCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria con id " + id + " no encontrada"));
    }

    private void validateActorId(Long actorId, String actorName) {
        if (actorId == null || actorId <= 0) {
            throw new BadRequestException("El id del " + actorName + " debe ser mayor a cero");
        }
    }

    private EstadoServicio parseEstado(String estado) {
        try {
            return EstadoServicio.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Estado no valido: " + estado);
        }
    }
}

package com.serviya.msservicerequest.service;

import com.serviya.msservicerequest.dto.service.CreateServiceRequest;
import com.serviya.msservicerequest.dto.service.ServiceRequestResponse;

import java.util.List;

public interface ServiceRequestService {

    ServiceRequestResponse create(CreateServiceRequest request, Long clienteId);

    ServiceRequestResponse findById(Long id);

    List<ServiceRequestResponse> findAll(Long categoriaId, String estado);

    List<ServiceRequestResponse> findByClienteId(Long clienteId);

    List<ServiceRequestResponse> findByTrabajadorId(Long trabajadorId);

    ServiceRequestResponse accept(Long serviceId, Long trabajadorId);

    ServiceRequestResponse finish(Long serviceId, Long trabajadorId);

    ServiceRequestResponse cancel(Long serviceId, Long clienteId);
}

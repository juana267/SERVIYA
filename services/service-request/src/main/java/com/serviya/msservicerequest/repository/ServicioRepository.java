package com.serviya.msservicerequest.repository;

import com.serviya.msservicerequest.entity.EstadoServicio;
import com.serviya.msservicerequest.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    List<Servicio> findByClienteIdOrderByFechaSolicitudDesc(Long clienteId);

    List<Servicio> findByTrabajadorIdOrderByFechaSolicitudDesc(Long trabajadorId);

    List<Servicio> findByCategoriaIdOrderByFechaSolicitudDesc(Long categoriaId);

    List<Servicio> findByEstadoOrderByFechaSolicitudDesc(EstadoServicio estado);

    List<Servicio> findByCategoriaIdAndEstadoOrderByFechaSolicitudDesc(Long categoriaId, EstadoServicio estado);

    List<Servicio> findAllByOrderByFechaSolicitudDesc();
}

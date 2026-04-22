package com.upeu.repository;

import com.upeu.entity.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    boolean existsBySolicitudId(Long solicitudId);

    Optional<Asignacion> findBySolicitudId(Long solicitudId);
}

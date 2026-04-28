package com.upeu.assignment.repository;

import com.upeu.assignment.entity.ConfigRegla;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfigReglaRepository extends JpaRepository<ConfigRegla, Long> {

    Optional<ConfigRegla> findFirstByActivoTrue();
}

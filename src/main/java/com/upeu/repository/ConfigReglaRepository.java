package com.upeu.repository;

import com.upeu.entity.ConfigRegla;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfigReglaRepository extends JpaRepository<ConfigRegla, Long> {

    Optional<ConfigRegla> findFirstByActivoTrue();
}

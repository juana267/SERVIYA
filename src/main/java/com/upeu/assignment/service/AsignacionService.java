package com.upeu.assignment.service;

import com.upeu.assignment.client.MsServiceRequestClient;
import com.upeu.assignment.client.MsTechnicianClient;
import com.upeu.assignment.dto.ActualizarEstadoSolicitudRequest;
import com.upeu.assignment.dto.AsignacionResponse;
import com.upeu.assignment.dto.TecnicoCercanoDto;
import com.upeu.assignment.entity.Asignacion;
import com.upeu.assignment.entity.ConfigRegla;
import com.upeu.assignment.exception.AsignacionNoDisponibleException;
import com.upeu.assignment.mapper.AsignacionMapper;
import com.upeu.assignment.repository.AsignacionRepository;
import com.upeu.assignment.repository.ConfigReglaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsignacionService {

    private final AsignacionRepository asignacionRepository;
    private final ConfigReglaRepository configReglaRepository;
    private final MsTechnicianClient msTechnicianClient;
    private final MsServiceRequestClient msServiceRequestClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
    private final AsignacionMapper asignacionMapper;

    @Transactional
    public AsignacionResponse autoAsignar(Long solicitudId) {
        return asignacionRepository.findBySolicitudId(solicitudId)
                .map(asignacionMapper::toResponse)
                .orElseGet(() -> crearNuevaAsignacion(solicitudId));
    }

    private AsignacionResponse crearNuevaAsignacion(Long solicitudId) {
        ConfigRegla regla = configReglaRepository.findFirstByActivoTrue()
                .orElseGet(this::reglaPorDefecto);

        List<TecnicoCercanoDto> tecnicos = obtenerTecnicosCercanos(solicitudId, regla.getRadioPermitidoKm());
        TecnicoCercanoDto seleccionado = seleccionarMejorTecnico(tecnicos, regla.getRadioPermitidoKm());

        Asignacion asignacion = Asignacion.builder()
                .solicitudId(solicitudId)
                .tecnicoId(seleccionado.getTecnicoId())
                .estado("ASIGNADA")
                .rankingTecnico(seleccionado.getRanking())
                .distanciaKm(seleccionado.getDistanciaKm())
                .fechaAsignacion(LocalDateTime.now())
                .build();

        Asignacion guardada = asignacionRepository.save(asignacion);

        actualizarEstadoSolicitud(solicitudId, regla.getEstadoAsignado(), seleccionado.getTecnicoId());

        return asignacionMapper.toResponse(guardada);
    }

    private List<TecnicoCercanoDto> obtenerTecnicosCercanos(Long solicitudId, Double radioKm) {
        CircuitBreaker cb = circuitBreakerFactory.create("msTechnician");
        return cb.run(
                () -> msTechnicianClient.obtenerTecnicosCercanos(solicitudId, radioKm),
                ex -> {
                    log.warn("Fallo consultando técnicos cercanos para solicitudId={}: {}", solicitudId, ex.toString());
                    return List.of();
                }
        );
    }

    private void actualizarEstadoSolicitud(Long solicitudId, String estado, Long tecnicoId) {
        CircuitBreaker cb = circuitBreakerFactory.create("msServiceRequest");
        cb.run(
                () -> {
                    msServiceRequestClient.actualizarEstado(solicitudId,
                            ActualizarEstadoSolicitudRequest.builder()
                                    .estado(estado)
                                    .tecnicoId(tecnicoId)
                                    .build());
                    return null;
                },
                ex -> {
                    log.warn("Fallo actualizando estado en MS-SERVICE-REQUEST para solicitudId={}: {}", solicitudId, ex.toString());
                    return null;
                }
        );
    }

    private TecnicoCercanoDto seleccionarMejorTecnico(List<TecnicoCercanoDto> tecnicos, Double radioKm) {
        TecnicoCercanoDto mejor = tecnicos.stream()
                .filter(Objects::nonNull)
                .filter(t -> t.getTecnicoId() != null)
                .filter(t -> t.getRanking() != null)
                .filter(t -> t.getDistanciaKm() == null || radioKm == null || t.getDistanciaKm() <= radioKm)
                .max(Comparator
                        .comparing(TecnicoCercanoDto::getRanking, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(TecnicoCercanoDto::getDistanciaKm, Comparator.nullsFirst(Comparator.reverseOrder())))
                .orElse(null);

        if (mejor == null) {
            throw new AsignacionNoDisponibleException("No hay técnicos disponibles dentro del radio permitido");
        }

        return mejor;
    }

    private ConfigRegla reglaPorDefecto() {
        return ConfigRegla.builder()
                .nombre("DEFAULT")
                .radioPermitidoKm(10.0)
                .estadoAsignado("ASIGNADA")
                .activo(true)
                .build();
    }
}

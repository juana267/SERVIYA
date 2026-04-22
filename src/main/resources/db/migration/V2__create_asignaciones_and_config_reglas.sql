CREATE TABLE IF NOT EXISTS config_reglas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(80) NOT NULL,
    radio_permitido_km DOUBLE NOT NULL,
    estado_asignado VARCHAR(40) NOT NULL,
    activo BOOLEAN NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_config_reglas_nombre UNIQUE (nombre)
);

CREATE TABLE IF NOT EXISTS asignaciones (
    id BIGINT NOT NULL AUTO_INCREMENT,
    solicitud_id BIGINT NOT NULL,
    tecnico_id BIGINT NOT NULL,
    estado VARCHAR(40) NOT NULL,
    ranking_tecnico DOUBLE NOT NULL,
    distancia_km DOUBLE,
    fecha_asignacion DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_asignaciones_solicitud_id UNIQUE (solicitud_id)
);

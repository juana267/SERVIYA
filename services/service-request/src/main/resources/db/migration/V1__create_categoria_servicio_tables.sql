CREATE TABLE IF NOT EXISTS categoria (
    id_categoria BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(120) NOT NULL,
    icono_url VARCHAR(255),
    CONSTRAINT pk_categoria PRIMARY KEY (id_categoria),
    CONSTRAINT uk_categoria_nombre UNIQUE (nombre)
);

CREATE TABLE IF NOT EXISTS servicio (
    id_servicio BIGINT NOT NULL AUTO_INCREMENT,
    id_cliente BIGINT NOT NULL,
    id_trabajador BIGINT NULL,
    id_categoria BIGINT NOT NULL,
    descripcion VARCHAR(400) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    precio_acordado DECIMAL(10, 2) NOT NULL,
    fecha_solicitud TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_inicio TIMESTAMP NULL,
    fecha_fin TIMESTAMP NULL,
    direccion_servicio VARCHAR(255) NOT NULL,
    lat DOUBLE NULL,
    lng DOUBLE NULL,
    CONSTRAINT pk_servicio PRIMARY KEY (id_servicio),
    CONSTRAINT fk_servicio_categoria FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
);

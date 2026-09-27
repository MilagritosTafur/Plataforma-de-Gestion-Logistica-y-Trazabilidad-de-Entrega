CREATE TABLE clientes (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT UNSIGNED NULL,
    documento VARCHAR(20) NULL,
    nombres VARCHAR(120) NOT NULL,
    apellidos VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL,
    telefono VARCHAR(20) NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_clientes_usuario UNIQUE (usuario_id),
    CONSTRAINT uk_clientes_email UNIQUE (email),
    CONSTRAINT uk_clientes_documento UNIQUE (documento),
    CONSTRAINT fk_clientes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

CREATE TABLE direcciones (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT UNSIGNED NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    distrito VARCHAR(100) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    referencia VARCHAR(255) NULL,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_direcciones_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

CREATE TABLE repartidores (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT UNSIGNED NOT NULL,
    documento VARCHAR(20) NULL,
    disponible BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_repartidores_usuario UNIQUE (usuario_id),
    CONSTRAINT uk_repartidores_documento UNIQUE (documento),
    CONSTRAINT fk_repartidores_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE vehiculos (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(12) NOT NULL,
    marca VARCHAR(80) NOT NULL,
    modelo VARCHAR(80) NULL,
    capacidad_kg DECIMAL(10,2) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_vehiculos_placa UNIQUE (placa)
);

CREATE TABLE rutas (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    fecha_programada DATE NOT NULL,
    repartidor_id BIGINT UNSIGNED NOT NULL,
    vehiculo_id BIGINT UNSIGNED NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PROGRAMADA',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rutas_repartidor FOREIGN KEY (repartidor_id) REFERENCES repartidores(id),
    CONSTRAINT fk_rutas_vehiculo FOREIGN KEY (vehiculo_id) REFERENCES vehiculos(id)
);

CREATE TABLE envios (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    codigo_seguimiento VARCHAR(30) NOT NULL,
    cliente_id BIGINT UNSIGNED NOT NULL,
    direccion_destino_id BIGINT UNSIGNED NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'REGISTRADO',
    fecha_estimada_entrega DATE NULL,
    fecha_entrega DATETIME NULL,
    receptor_nombre VARCHAR(150) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_envios_codigo UNIQUE (codigo_seguimiento),
    CONSTRAINT fk_envios_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    CONSTRAINT fk_envios_direccion FOREIGN KEY (direccion_destino_id) REFERENCES direcciones(id)
);

CREATE TABLE paquetes (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    envio_id BIGINT UNSIGNED NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    peso_kg DECIMAL(10,2) NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_paquetes_envio FOREIGN KEY (envio_id) REFERENCES envios(id)
);

CREATE TABLE asignaciones (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    envio_id BIGINT UNSIGNED NOT NULL,
    ruta_id BIGINT UNSIGNED NOT NULL,
    repartidor_id BIGINT UNSIGNED NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finalizada_at DATETIME NULL,
    CONSTRAINT fk_asignaciones_envio FOREIGN KEY (envio_id) REFERENCES envios(id),
    CONSTRAINT fk_asignaciones_ruta FOREIGN KEY (ruta_id) REFERENCES rutas(id),
    CONSTRAINT fk_asignaciones_repartidor FOREIGN KEY (repartidor_id) REFERENCES repartidores(id)
);

CREATE TABLE eventos_seguimiento (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    envio_id BIGINT UNSIGNED NOT NULL,
    tipo VARCHAR(25) NOT NULL,
    ubicacion VARCHAR(150) NULL,
    observacion VARCHAR(255) NULL,
    visible_cliente BOOLEAN NOT NULL DEFAULT TRUE,
    registrado_por_id BIGINT UNSIGNED NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_eventos_envio FOREIGN KEY (envio_id) REFERENCES envios(id),
    CONSTRAINT fk_eventos_usuario FOREIGN KEY (registrado_por_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

CREATE TABLE incidencias (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    envio_id BIGINT UNSIGNED NOT NULL,
    repartidor_id BIGINT UNSIGNED NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    evidencia_url VARCHAR(255) NULL,
    resuelta BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_incidencias_envio FOREIGN KEY (envio_id) REFERENCES envios(id),
    CONSTRAINT fk_incidencias_repartidor FOREIGN KEY (repartidor_id) REFERENCES repartidores(id)
);

CREATE INDEX idx_envios_cliente ON envios(cliente_id);
CREATE INDEX idx_eventos_envio ON eventos_seguimiento(envio_id, created_at);
CREATE INDEX idx_asignaciones_repartidor_activa ON asignaciones(repartidor_id, activa);

-- El objeto "item" pertenecia a la plantilla inicial y no participa en el dominio logistico.
DROP TABLE IF EXISTS item;

-- Una direccion utilizada por un envio debe pertenecer al mismo cliente del envio.
ALTER TABLE direcciones
    ADD CONSTRAINT uk_direcciones_id_cliente UNIQUE (id, cliente_id);

ALTER TABLE envios
    ADD CONSTRAINT fk_envios_direccion_cliente
        FOREIGN KEY (direccion_destino_id, cliente_id) REFERENCES direcciones (id, cliente_id);

-- Evita que una asignacion indique un repartidor diferente del repartidor de su ruta.
ALTER TABLE rutas
    ADD CONSTRAINT uk_rutas_id_repartidor UNIQUE (id, repartidor_id);

ALTER TABLE asignaciones
    ADD CONSTRAINT fk_asignaciones_ruta_repartidor
        FOREIGN KEY (ruta_id, repartidor_id) REFERENCES rutas (id, repartidor_id),
    ADD COLUMN envio_activo_id BIGINT UNSIGNED
        GENERATED ALWAYS AS (CASE WHEN activa = TRUE THEN envio_id ELSE NULL END) STORED,
    ADD CONSTRAINT uk_asignaciones_envio_activo UNIQUE (envio_activo_id);

-- Validaciones que tambien protegen la integridad si se consulta la base directamente.
ALTER TABLE vehiculos
    ADD CONSTRAINT ck_vehiculos_capacidad_positiva CHECK (capacidad_kg > 0);

ALTER TABLE paquetes
    ADD CONSTRAINT ck_paquetes_peso_positivo CHECK (peso_kg > 0),
    ADD CONSTRAINT ck_paquetes_cantidad_positiva CHECK (cantidad > 0);

CREATE INDEX idx_envios_estado ON envios (estado);
CREATE INDEX idx_rutas_estado ON rutas (estado);
CREATE INDEX idx_asignaciones_ruta_activa ON asignaciones (ruta_id, activa);
CREATE INDEX idx_incidencias_envio_resuelta ON incidencias (envio_id, resuelta);

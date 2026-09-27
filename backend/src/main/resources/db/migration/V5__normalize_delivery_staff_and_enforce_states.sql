-- clientes representa al destinatario/comprador del envio. Puede existir sin
-- usuario web porque el operador tambien registra clientes presenciales.
-- Sus datos de contacto son una fotografia operativa del destinatario; no es
-- una tabla de autenticacion (usuarios es la unica tabla de login).

-- Un repartidor necesita identificacion y licencia para ser asignable a una ruta.
-- Se conserva nullable para no invalidar historicos; el backend no permite usar
-- repartidores sin licencia y los deja no disponibles hasta regularizarlos.
ALTER TABLE repartidores
    ADD COLUMN licencia_conducir VARCHAR(30) NULL AFTER documento,
    ADD CONSTRAINT uk_repartidores_licencia UNIQUE (licencia_conducir);

UPDATE repartidores
SET disponible = FALSE
WHERE licencia_conducir IS NULL OR TRIM(licencia_conducir) = '';

-- La ruta ya determina al repartidor. Eliminar la duplicacion de asignaciones
-- evita que dos columnas puedan apuntar a repartidores diferentes.
ALTER TABLE asignaciones
    DROP FOREIGN KEY fk_asignaciones_ruta_repartidor,
    DROP FOREIGN KEY fk_asignaciones_repartidor,
    DROP COLUMN repartidor_id;

ALTER TABLE rutas
    DROP INDEX uk_rutas_id_repartidor;

-- Estados se mantienen como VARCHAR para facilitar la evolucion, pero quedan
-- restringidos en la base y vuelven a validarse en ServicioLogistico.
ALTER TABLE envios
    ADD CONSTRAINT ck_envios_estado_valido
        CHECK (estado IN ('REGISTRADO', 'ASIGNADO', 'EN_RUTA', 'ENTREGADO', 'INCIDENCIA', 'DEVUELTO'));

ALTER TABLE rutas
    ADD CONSTRAINT ck_rutas_estado_valido
        CHECK (estado IN ('PROGRAMADA', 'EN_CURSO', 'FINALIZADA', 'CANCELADA'));

ALTER TABLE eventos_seguimiento
    ADD CONSTRAINT ck_eventos_tipo_valido
        CHECK (tipo IN ('REGISTRADO', 'ASIGNADO', 'EN_RUTA', 'ENTREGADO', 'INCIDENCIA', 'DEVUELTO'));

CREATE INDEX idx_repartidores_disponibles ON repartidores (disponible, licencia_conducir);

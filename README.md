# Core Logistics

Plataforma de gestión logística y trazabilidad de entregas de última milla. El proyecto está separado en dos aplicaciones:

- `backend`: API REST con Spring Boot, Spring Security, JWT, JPA y Flyway.
- `front`: páginas responsivas en Bootstrap 5, servidas por Nginx y conectadas a la API mediante `/api`.

### Vista con Thymeleaf

Thymeleaf se utiliza donde aporta valor real: en el **seguimiento público**. El formulario de la portada lleva a `GET /seguimiento?codigo=...`; `VistaSeguimientoController` consulta el módulo `tracking` y Spring Boot renderiza `backend/src/main/resources/templates/seguimiento.html` con el estado, destino y eventos autorizados que existen en la base de datos. Esto permite que la consulta funcione sin JavaScript para obtener los datos y mantiene la API JSON en `GET /api/tracking/{codigo}` para el frontend y futuras integraciones.

No se usa Thymeleaf en los paneles de administrador, operador, repartidor y cliente: esos módulos permanecen como frontend Bootstrap separado y consumen sus controladores REST por JWT. Así se respeta la separación frontend/backend sin duplicar la lógica de permisos.

La portada que está en `front/templates/public/index.html` es la interfaz principal del proyecto. Puede abrirse con Docker/Nginx o con **Live Server**: cuando se sirve desde `127.0.0.1:5500`, el JavaScript detecta ese entorno y consulta de forma segura a `http://127.0.0.1:8081/api`. Así no hay una segunda portada estática ni enlaces de demostración sin función.

## Experiencia de usuario

No hay datos decorativos ni tarjetas estáticas en los paneles. Cada pantalla consulta la API y muestra únicamente información persistida:

- Página pública: consulta de código, explicación del ciclo y accesos por perfil.
- Seguimiento público: estado real y línea de tiempo de eventos autorizados.
- Administrador: crea cuentas y consulta el equipo operativo y el resumen actual.
- Operador: proceso guiado `registrar envío → planificar ruta → asignar y despachar`.
- Repartidor: inicia ruta, confirma el receptor mediante un formulario o reporta una incidencia.
- Cliente: consulta solo sus envíos y accede a su trazabilidad.

La portada no almacena ni completa automáticamente datos de búsqueda; el código de seguimiento se limpia al abrir o volver a la página. El formulario de acceso también se limpia para evitar que queden credenciales visibles en una sesión anterior.

### Registro y acceso por correo

- Cualquier correo nuevo puede crear una cuenta de **cliente** desde `Registro` e ingresar inmediatamente con la contraseña elegida.
- Las cuentas de **operador** y **repartidor** las crea el administrador desde su panel, porque son perfiles con permisos operativos. Después pueden iniciar sesión con el correo registrado y el sistema los redirige automáticamente a su panel correspondiente.
- Los correos se normalizan (espacios y mayúsculas/minúsculas), por lo que `Correo@Ejemplo.com` y `correo@ejemplo.com` identifican la misma cuenta.

## Flujo que se puede demostrar

1. El administrador inicia sesión y crea una cuenta de **OPERADOR** y otra de **REPARTIDOR**.
2. El operador registra un cliente, su dirección y un envío. El sistema crea un código único como `CL-XXXXXXXXXX`, guarda el paquete y registra el evento **REGISTRADO**.
3. El operador registra un vehículo, crea una ruta para un repartidor disponible y asigna el envío. El estado cambia a **ASIGNADO**.
4. El repartidor entra a su panel, ve únicamente sus asignaciones y registra **EN_RUTA**, **ENTREGADO** o una **INCIDENCIA**.
5. Para entregar debe indicar quién recibió el paquete. La asignación se cierra y no se puede volver a modificar ese envío.
6. El cliente ve solo los envíos ligados a su cuenta. Cualquier persona puede buscar el código en la página pública y consultar los eventos visibles.

## Reglas de negocio aplicadas

- Solo se permiten estas transiciones: `REGISTRADO → ASIGNADO → EN_RUTA → ENTREGADO`; desde `EN_RUTA` también se puede pasar a `INCIDENCIA`; y desde una incidencia a `EN_RUTA` o `DEVUELTO`.
- Un envío solo puede tener una asignación activa y no puede entrar dos veces a una ruta activa.
- Un repartidor con una ruta programada o en curso no recibe otra ruta.
- Solo el repartidor asignado puede cambiar el estado o reportar una incidencia.
- Los eventos no se eliminan. El endpoint público devuelve únicamente información autorizada para el seguimiento.
- Las contraseñas usan BCrypt y las rutas privadas usan JWT y roles.

## Base de datos

Flyway crea el esquema desde `backend/src/main/resources/db/migration`.

| Grupo | Tablas |
| --- | --- |
| Seguridad | `roles`, `usuarios` |
| Personas | `clientes`, `direcciones`, `repartidores` |
| Operación | `vehiculos`, `rutas`, `envios`, `paquetes`, `asignaciones` |
| Trazabilidad | `eventos_seguimiento`, `incidencias` |

Relaciones principales: un cliente tiene direcciones y envíos; un envío contiene paquetes y una dirección destino del mismo cliente; una asignación une un envío con una ruta (y el repartidor se obtiene exclusivamente desde esa ruta); cada evento e incidencia pertenece a un envío.

### Decisiones del modelo

- `usuarios` es la única tabla de autenticación. `clientes.usuario_id` puede ser `NULL`: un operador puede registrar al destinatario de un envío aunque este no tenga cuenta web.
- Los campos de contacto de `clientes` son una **fotografía operativa** del destinatario usada en la entrega. Se duplican de forma deliberada cuando el cliente sí crea una cuenta para permitir clientes presenciales y conservar el dato con el que se creó el envío; no se usan para validar contraseñas ni permisos.
- `repartidores` ahora incluye `documento` y `licencia_conducir`. Documento y licencia son únicos; el backend exige ambos al crear una cuenta de repartidor y no permite asignarlo a rutas sin licencia válida.
- `asignaciones.repartidor_id` fue eliminado en `V5`: conservarlo junto a `rutas.repartidor_id` podía producir asignaciones inconsistentes. La ruta es la única fuente del repartidor asignado.
- Los estados siguen siendo `VARCHAR` para no rigidizar futuras extensiones, pero `V5` añade restricciones `CHECK` en MySQL y `ServicioLogistico` valida también cada transición autorizada. De ese modo hay protección tanto desde la API como ante cambios directos en la base.

Las tablas técnicas `flyway_schema_history` y la antigua tabla de plantilla `item` no forman parte del modelo funcional ni del diagrama. `item` se elimina en la migración `V4`.

Al aplicar `V5`, los repartidores históricos que no tenían licencia quedan temporalmente como no disponibles. Es una medida deliberada para no inventar datos ni permitir que se les asigne una nueva ruta; crea una cuenta nueva de repartidor desde Administración con documento y licencia para habilitarlo.

Las migraciones `V4__strengthen_relations_and_remove_legacy.sql` y `V5__normalize_delivery_staff_and_enforce_states.sql` añaden protección en la propia base de datos:

- Una dirección de destino no puede pertenecer a otro cliente.
- La asignación obtiene al repartidor únicamente desde su ruta; ya no puede existir una asignación con dos repartidores distintos.
- Un envío tiene como máximo una asignación activa.
- Capacidad de vehículo, peso y cantidad de paquete deben ser positivos.
- Índices para consultas frecuentes de estados, rutas, asignaciones e incidencias.
- Documento y licencia únicos para repartidores, más un índice para encontrar repartidores habilitados.
- Estados de envío, ruta y evento limitados a los valores del dominio.

## API principal

| Módulo | Rutas |
| --- | --- |
| Autenticación | `POST /api/auth/login`, `POST /api/auth/registro` |
| Usuarios | `GET/POST /api/usuarios` (administrador) |
| Clientes | `GET/POST /api/clientes`, `POST /api/clientes/{id}/direcciones` |
| Vehículos | `GET/POST /api/vehiculos` |
| Rutas | `GET/POST /api/rutas` |
| Envíos | `GET/POST /api/envios`, `GET /api/envios/mis-envios`, `POST /api/envios/{id}/eventos` |
| Asignaciones | `POST /api/asignaciones`, `GET /api/asignaciones/mias` |
| Incidencias | `POST /api/incidencias/envio/{envioId}` |
| Seguimiento público | `GET /api/tracking/{codigo}`, `GET /seguimiento?codigo=...` (Thymeleaf) |

Cada módulo posee su propio controlador dentro de `backend/src/main/java/com/example/demo/feature`.

## Cómo ejecutar

1. Copia `.env.example` como `.env` y cambia las contraseñas y el secreto JWT.
2. Instala Docker Desktop y ejecútalo.
3. Desde la carpeta raíz ejecuta `docker compose up --build -d`.
4. Abre `http://localhost:3000`.
5. Ingresa con las credenciales configuradas en `ADMIN_EMAIL` y `ADMIN_PASSWORD` para crear las cuentas del operador y repartidor.

El modificador `-d` deja los contenedores ejecutándose en segundo plano. Si ejecutas `docker compose up --build` sin `-d`, la terminal muestra los registros y al pulsar `Ctrl+C` Docker detiene los servicios de forma controlada; el mensaje `Gracefully Stopping` no representa un error. Después de reconstruir, espera aproximadamente 45 segundos: Spring Boot necesita ese tiempo para iniciar JPA y comprobar las migraciones de MySQL. Durante ese intervalo el frontend puede mostrar `502 Bad Gateway` porque el backend aún está iniciando.

Para la configuración incluida en este proyecto, el acceso inicial es `admin@demo.com` con contraseña `Admin123!`. Estas son credenciales de demostración local; cámbialas antes de publicar el sistema.

Si ya tenías los contenedores levantados antes de una modificación del frontend, Docker conserva la imagen anterior. Reconstrúyelos desde la carpeta raíz con `docker compose up --build` y luego vuelve a abrir `http://localhost:3000`.

### Usar la misma portada con Live Server

1. Inicia MySQL y el backend en el puerto `8081` (con Docker puedes ejecutar `docker compose up mysql app`).
2. Abre la carpeta `front` con Live Server y navega a `front/templates/public/index.html` (normalmente `http://127.0.0.1:5500/front/templates/public/index.html`).
3. La portada, el seguimiento, registro y acceso consultarán el backend real; no necesitan rutas manuales adicionales. Si Spring Boot está apagado, la interfaz muestra un mensaje que indica cómo resolverlo.

Los orígenes de desarrollo permitidos se definen en `CORS_ALLOWED_ORIGINS`; la configuración incluida admite `localhost` y `127.0.0.1` en los puertos 3000 y 5500. No se habilita cualquier origen.

## Conexión desde DBeaver

La conexión mostrada en DBeaver es correcta. Usa estos valores:

| Campo | Valor |
| --- | --- |
| Motor | MySQL 8 |
| Host | `localhost` |
| Puerto | `3306` |
| Base de datos | `demo_db` |
| Usuario | `demo` |
| Contraseña | `demo` |

En DBeaver se usa `localhost` porque se conecta desde tu computadora. El backend usa el host `mysql`, ya que se conecta desde otro contenedor dentro de Docker. No se deben intercambiar esos dos valores.

Para revisar el estado de los servicios usa `docker compose ps`. Debes ver `mysql`, `app` y `front` en estado `running`; MySQL debe indicar `healthy` antes de que el backend se conecte.

## Datos de demostración creados en tu base local

Durante la verificación se crearon estas cuentas y un envío de prueba. Puedes usarlas después de reconstruir el frontend:

| Rol | Correo | Contraseña | Qué debe verse |
| --- | --- | --- | --- |
| Administrador | `admin@demo.com` | `Admin123!` | Panel para crear operadores y repartidores. |
| Operador | `operador.demo@corelocal.test` | `Demo123!` | Ruta, vehículo y envío registrados. |
| Repartidor | `repartidor.demo@corelocal.test` | `Demo123!` | Una asignación en estado `ASIGNADO`. |
| Cliente | `cliente.demo@corelocal.test` | `Cliente123!` | Un envío en su panel. |

El código público de seguimiento es `CL-8DAAF4A34D`. Inicia sesión como repartidor y pulsa **Iniciar ruta** para continuar la demostración; después podrás marcarlo como entregado o registrar una incidencia.

## Si el inicio de sesión indica credenciales inválidas

El correo debe estar registrado antes de ingresar. En la captura de revisión se usó `maria@gmail.com`; ese correo no existe actualmente en la tabla `usuarios`, por lo que el acceso se rechaza correctamente. Regístralo primero desde **Regístrate aquí** o usa una de las cuentas de demostración de esta guía.

El sistema devuelve ahora un mensaje entendible para credenciales inválidas y evita que el navegador use una copia anterior de JavaScript.

La documentación interactiva de la API queda disponible en `http://localhost:8081/swagger-ui/index.html` cuando el backend está activo.

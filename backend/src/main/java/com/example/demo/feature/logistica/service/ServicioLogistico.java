package com.example.demo.feature.logistica.service;

import com.example.demo.domain.model.*;
import com.example.demo.domain.repository.*;
import com.example.demo.feature.logistica.dto.*;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;

/** Reune las reglas de negocio de la operacion. Los controladores solo reciben y devuelven HTTP. */
@Service
@RequiredArgsConstructor
@Transactional
public class ServicioLogistico {
    private final ClienteRepository clienteRepository;
    private final DireccionRepository direccionRepository;
    private final RepartidorRepository repartidorRepository;
    private final VehiculoRepository vehiculoRepository;
    private final RutaRepository rutaRepository;
    private final EnvioRepository envioRepository;
    private final PaqueteRepository paqueteRepository;
    private final AsignacionRepository asignacionRepository;
    private final EventoSeguimientoRepository eventoRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final UsuarioRepository usuarioRepository;

    public Map<String, Object> crearCliente(CrearClienteRequest request) {
        Optional<Cliente> existente = clienteRepository.findByEmailIgnoreCase(request.email());
        if (existente.isPresent()) {
            Cliente cliente = existente.get();
            if (cliente.getDocumento() == null || cliente.getDocumento().isBlank()) cliente.setDocumento(request.documento());
            if (cliente.getTelefono() == null || cliente.getTelefono().isBlank()) cliente.setTelefono(request.telefono());
            return clienteDatos(clienteRepository.save(cliente));
        }
        Cliente cliente = new Cliente();
        cliente.setDocumento(request.documento());
        cliente.setNombres(request.nombres());
        cliente.setApellidos(request.apellidos());
        cliente.setEmail(request.email());
        cliente.setTelefono(request.telefono());
        return clienteDatos(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarClientes() {
        return clienteRepository.findAll().stream().map(this::clienteDatos).toList();
    }

    public Map<String, Object> crearDireccion(Long clienteId, CrearDireccionRequest request) {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(() -> noEncontrado("Cliente"));
        if (!cliente.isActivo()) {
            throw conflicto("El cliente no esta activo");
        }
        if (request.principal()) {
            direccionRepository.findByClienteId(clienteId).forEach(direccion -> direccion.setPrincipal(false));
        }
        Direccion direccion = new Direccion();
        direccion.setCliente(cliente);
        direccion.setDireccion(request.direccion());
        direccion.setDistrito(request.distrito());
        direccion.setCiudad(request.ciudad());
        direccion.setReferencia(request.referencia());
        direccion.setPrincipal(request.principal());
        return direccionDatos(direccionRepository.save(direccion));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarDirecciones(Long clienteId) {
        return direccionRepository.findByClienteId(clienteId).stream().map(this::direccionDatos).toList();
    }

    public Map<String, Object> crearVehiculo(CrearVehiculoRequest request) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca(request.placa().trim().toUpperCase());
        vehiculo.setMarca(request.marca());
        vehiculo.setModelo(request.modelo());
        vehiculo.setCapacidadKg(request.capacidadKg());
        vehiculo.setActivo(true);
        return vehiculoDatos(vehiculoRepository.save(vehiculo));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarVehiculos() {
        return vehiculoRepository.findAll().stream().map(this::vehiculoDatos).toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarRepartidores() {
        return repartidorRepository.findAll().stream().map(r -> datos(
                "id", r.getId(), "nombre", r.getUsuario().getNombre() + " " + r.getUsuario().getApellido(),
                "email", r.getUsuario().getEmail(), "documento", r.getDocumento(),
                "licenciaVerificada", r.getLicenciaConducir() != null && !r.getLicenciaConducir().isBlank(),
                "disponible", r.isDisponible())).toList();
    }

    public Map<String, Object> crearRuta(CrearRutaRequest request) {
        Repartidor repartidor = repartidorRepository.findById(request.repartidorId())
                .orElseThrow(() -> noEncontrado("Repartidor"));
        Vehiculo vehiculo = vehiculoRepository.findById(request.vehiculoId())
                .orElseThrow(() -> noEncontrado("Vehiculo"));
        if (!repartidor.isDisponible()) {
            throw conflicto("El repartidor no esta disponible");
        }
        if (repartidor.getLicenciaConducir() == null || repartidor.getLicenciaConducir().isBlank()) {
            throw conflicto("El repartidor no tiene una licencia de conducir registrada");
        }
        if (!vehiculo.isActivo()) {
            throw conflicto("El vehiculo no esta activo");
        }
        if (rutaRepository.existsByRepartidorIdAndEstadoIn(repartidor.getId(),
                List.of(EstadoRuta.PROGRAMADA, EstadoRuta.EN_CURSO))) {
            throw conflicto("El repartidor ya tiene una ruta activa");
        }
        if (rutaRepository.existsByVehiculoIdAndEstadoIn(vehiculo.getId(),
                List.of(EstadoRuta.PROGRAMADA, EstadoRuta.EN_CURSO))) {
            throw conflicto("El vehiculo ya esta asignado a una ruta activa");
        }
        Ruta ruta = new Ruta();
        ruta.setNombre(request.nombre());
        ruta.setFechaProgramada(request.fechaProgramada());
        ruta.setRepartidor(repartidor);
        ruta.setVehiculo(vehiculo);
        ruta.setEstado(EstadoRuta.PROGRAMADA);
        return rutaDatos(rutaRepository.save(ruta));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarRutas() {
        return rutaRepository.findAll().stream().map(this::rutaDatos).toList();
    }

    public Map<String, Object> crearEnvio(CrearEnvioRequest request, String emailOperador) {
        Cliente cliente = clienteRepository.findById(request.clienteId()).orElseThrow(() -> noEncontrado("Cliente"));
        Direccion direccion = direccionRepository.findById(request.direccionDestinoId()).orElseThrow(() -> noEncontrado("Direccion"));
        if (!cliente.isActivo()) {
            throw conflicto("El cliente no esta activo");
        }
        if (!direccion.getCliente().getId().equals(cliente.getId())) {
            throw conflicto("La direccion seleccionada no pertenece al cliente");
        }
        Envio envio = new Envio();
        envio.setCodigoSeguimiento(generarCodigo());
        envio.setCliente(cliente);
        envio.setDireccionDestino(direccion);
        envio.setDescripcion(request.descripcion());
        envio.setFechaEstimadaEntrega(request.fechaEstimadaEntrega());
        envio.setEstado(EstadoEnvio.REGISTRADO);
        envioRepository.save(envio);

        Paquete paquete = new Paquete();
        paquete.setEnvio(envio);
        paquete.setDescripcion(request.descripcionPaquete());
        paquete.setPesoKg(request.pesoKg());
        paquete.setCantidad(request.cantidad());
        paqueteRepository.save(paquete);
        registrarEventoInterno(envio, TipoEventoSeguimiento.REGISTRADO, "Centro logistico", "Envio registrado", true, emailOperador);
        return envioDatos(envio);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarEnvios() {
        return envioRepository.findAll().stream().map(this::envioDatos).toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> misEnvios(String emailCliente) {
        return envioRepository.findByClienteUsuarioEmailIgnoreCaseOrderByIdDesc(emailCliente).stream().map(this::envioDatos).toList();
    }

    public Map<String, Object> asignarEnvio(CrearAsignacionRequest request, String emailOperador) {
        Envio envio = envioRepository.findById(request.envioId()).orElseThrow(() -> noEncontrado("Envio"));
        Ruta ruta = rutaRepository.findById(request.rutaId()).orElseThrow(() -> noEncontrado("Ruta"));
        if (envio.getEstado() != EstadoEnvio.REGISTRADO) {
            throw conflicto("Solo se pueden asignar envios en estado REGISTRADO");
        }
        if (asignacionRepository.existsByEnvioIdAndActivaTrue(envio.getId())) {
            throw conflicto("El envio ya pertenece a una ruta activa");
        }
        if (ruta.getEstado() != EstadoRuta.PROGRAMADA) {
            throw conflicto("La ruta ya no acepta asignaciones");
        }
        Asignacion asignacion = new Asignacion();
        asignacion.setEnvio(envio);
        asignacion.setRuta(ruta);
        asignacionRepository.save(asignacion);
        envio.setEstado(EstadoEnvio.ASIGNADO);
        registrarEventoInterno(envio, TipoEventoSeguimiento.ASIGNADO, ruta.getNombre(), "Envio asignado a una ruta", true, emailOperador);
        return asignacionDatos(asignacion);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> misAsignaciones(String emailRepartidor) {
        return asignacionRepository.findByRutaRepartidorUsuarioEmailIgnoreCaseAndActivaTrueOrderByIdDesc(emailRepartidor)
                .stream().map(this::asignacionDatos).toList();
    }

    public Map<String, Object> registrarEvento(Long envioId, RegistrarEventoRequest request, String emailRepartidor) {
        Envio envio = envioRepository.findById(envioId).orElseThrow(() -> noEncontrado("Envio"));
        Asignacion asignacion = asignacionActivaDelRepartidor(envioId, emailRepartidor);
        if (request.nuevoEstado() == EstadoEnvio.INCIDENCIA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registre la incidencia en /api/incidencias para conservar su detalle");
        }
        verificarTransicion(envio.getEstado(), request.nuevoEstado());
        envio.setEstado(request.nuevoEstado());
        if (request.nuevoEstado() == EstadoEnvio.EN_RUTA) {
            asignacion.getRuta().setEstado(EstadoRuta.EN_CURSO);
        }
        if (request.nuevoEstado() == EstadoEnvio.ENTREGADO) {
            if (request.receptorNombre() == null || request.receptorNombre().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar quien recibio el paquete");
            }
            envio.setReceptorNombre(request.receptorNombre());
            envio.setFechaEntrega(LocalDateTime.now());
            cerrarAsignacion(asignacion);
        }
        if (request.nuevoEstado() == EstadoEnvio.DEVUELTO) {
            cerrarAsignacion(asignacion);
        }
        registrarEventoInterno(envio, TipoEventoSeguimiento.valueOf(request.nuevoEstado().name()), request.ubicacion(),
                request.observacion(), true, emailRepartidor);
        return envioDatos(envio);
    }

    public Map<String, Object> reportarIncidencia(Long envioId, CrearIncidenciaRequest request, String emailRepartidor) {
        Envio envio = envioRepository.findById(envioId).orElseThrow(() -> noEncontrado("Envio"));
        Asignacion asignacion = asignacionActivaDelRepartidor(envioId, emailRepartidor);
        verificarTransicion(envio.getEstado(), EstadoEnvio.INCIDENCIA);
        Incidencia incidencia = new Incidencia();
        incidencia.setEnvio(envio);
        incidencia.setRepartidor(asignacion.getRuta().getRepartidor());
        incidencia.setDescripcion(request.descripcion());
        incidencia.setEvidenciaUrl(request.evidenciaUrl());
        incidencia.setResuelta(false);
        incidenciaRepository.save(incidencia);
        envio.setEstado(EstadoEnvio.INCIDENCIA);
        registrarEventoInterno(envio, TipoEventoSeguimiento.INCIDENCIA, request.ubicacion(),
                "Se registro una incidencia de entrega", true, emailRepartidor);
        return datos("id", incidencia.getId(), "envioId", envioId, "estadoEnvio", envio.getEstado(), "descripcion", incidencia.getDescripcion());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> trackingPublico(String codigo) {
        String codigoNormalizado = codigo == null ? "" : codigo.trim().toUpperCase(Locale.ROOT);
        Envio envio = envioRepository.findByCodigoSeguimiento(codigoNormalizado).orElseThrow(() -> noEncontrado("Codigo de seguimiento"));
        List<Map<String, Object>> eventos = eventoRepository
                .findByEnvioCodigoSeguimientoAndVisibleClienteTrueOrderByIdAsc(codigoNormalizado).stream()
                .map(e -> datos("tipo", e.getTipo(), "ubicacion", e.getUbicacion(), "observacion", e.getObservacion(), "fecha", e.getCreatedAt()))
                .toList();
        return datos("codigoSeguimiento", envio.getCodigoSeguimiento(), "estado", envio.getEstado(),
                "fechaEstimadaEntrega", envio.getFechaEstimadaEntrega(),
                "destino", envio.getDireccionDestino().getDistrito() + ", " + envio.getDireccionDestino().getCiudad(),
                "eventos", eventos);
    }

    private Asignacion asignacionActivaDelRepartidor(Long envioId, String email) {
        Asignacion asignacion = asignacionRepository.findByEnvioIdAndActivaTrue(envioId)
                .orElseThrow(() -> conflicto("El envio no tiene una asignacion activa"));
        if (!asignacion.getRuta().getRepartidor().getUsuario().getEmail().equalsIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el repartidor asignado puede registrar este evento");
        }
        return asignacion;
    }

    private void cerrarAsignacion(Asignacion asignacion) {
        asignacion.setActiva(false);
        asignacion.setFinalizadaAt(LocalDateTime.now());
        asignacionRepository.save(asignacion);
        Ruta ruta = asignacion.getRuta();
        if (asignacionRepository.countByRutaIdAndActivaTrue(ruta.getId()) == 0) {
            ruta.setEstado(EstadoRuta.FINALIZADA);
        }
    }

    private void verificarTransicion(EstadoEnvio actual, EstadoEnvio siguiente) {
        boolean valida = switch (actual) {
            case REGISTRADO -> siguiente == EstadoEnvio.ASIGNADO;
            case ASIGNADO -> siguiente == EstadoEnvio.EN_RUTA;
            case EN_RUTA -> siguiente == EstadoEnvio.ENTREGADO || siguiente == EstadoEnvio.INCIDENCIA;
            case INCIDENCIA -> siguiente == EstadoEnvio.EN_RUTA || siguiente == EstadoEnvio.DEVUELTO;
            case ENTREGADO, DEVUELTO -> false;
        };
        if (!valida) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Transicion no permitida: " + actual + " -> " + siguiente);
        }
    }

    private void registrarEventoInterno(Envio envio, TipoEventoSeguimiento tipo, String ubicacion, String observacion,
                                        boolean visibleCliente, String emailUsuario) {
        EventoSeguimiento evento = new EventoSeguimiento();
        evento.setEnvio(envio);
        evento.setTipo(tipo);
        evento.setUbicacion(ubicacion);
        evento.setObservacion(observacion);
        evento.setVisibleCliente(visibleCliente);
        if (emailUsuario != null) {
            usuarioRepository.findByEmailIgnoreCase(emailUsuario).ifPresent(evento::setRegistradoPor);
        }
        eventoRepository.save(evento);
    }

    private String generarCodigo() {
        String codigo;
        do {
            codigo = "CL-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        } while (envioRepository.findByCodigoSeguimiento(codigo).isPresent());
        return codigo;
    }

    private Map<String, Object> clienteDatos(Cliente c) {
        return datos("id", c.getId(), "documento", c.getDocumento(), "nombres", c.getNombres(), "apellidos", c.getApellidos(),
                "email", c.getEmail(), "telefono", c.getTelefono(), "activo", c.isActivo());
    }
    private Map<String, Object> direccionDatos(Direccion d) {
        return datos("id", d.getId(), "clienteId", d.getCliente().getId(), "direccion", d.getDireccion(), "distrito", d.getDistrito(),
                "ciudad", d.getCiudad(), "referencia", d.getReferencia(), "principal", d.isPrincipal());
    }
    private Map<String, Object> vehiculoDatos(Vehiculo v) {
        return datos("id", v.getId(), "placa", v.getPlaca(), "marca", v.getMarca(), "modelo", v.getModelo(),
                "capacidadKg", v.getCapacidadKg(), "activo", v.isActivo());
    }
    private Map<String, Object> rutaDatos(Ruta r) {
        return datos("id", r.getId(), "nombre", r.getNombre(), "fechaProgramada", r.getFechaProgramada(), "estado", r.getEstado(),
                "repartidorId", r.getRepartidor().getId(), "repartidor", r.getRepartidor().getUsuario().getNombre() + " " + r.getRepartidor().getUsuario().getApellido(),
                "vehiculoId", r.getVehiculo().getId(), "placa", r.getVehiculo().getPlaca());
    }
    private Map<String, Object> envioDatos(Envio e) {
        return datos("id", e.getId(), "codigoSeguimiento", e.getCodigoSeguimiento(), "estado", e.getEstado(), "descripcion", e.getDescripcion(),
                "clienteId", e.getCliente().getId(), "cliente", e.getCliente().getNombres() + " " + e.getCliente().getApellidos(),
                "destino", e.getDireccionDestino().getDireccion() + ", " + e.getDireccionDestino().getDistrito(),
                "fechaEstimadaEntrega", e.getFechaEstimadaEntrega(), "fechaEntrega", e.getFechaEntrega());
    }
    private Map<String, Object> asignacionDatos(Asignacion a) {
        return datos("id", a.getId(), "activa", a.isActiva(), "envio", envioDatos(a.getEnvio()), "ruta", rutaDatos(a.getRuta()));
    }
    private Map<String, Object> datos(Object... pares) {
        Map<String, Object> resultado = new LinkedHashMap<>();
        for (int i = 0; i < pares.length; i += 2) resultado.put((String) pares[i], pares[i + 1]);
        return resultado;
    }
    private ResponseStatusException noEncontrado(String recurso) { return new ResponseStatusException(HttpStatus.NOT_FOUND, recurso + " no encontrado"); }
    private ResponseStatusException conflicto(String mensaje) { return new ResponseStatusException(HttpStatus.CONFLICT, mensaje); }
}

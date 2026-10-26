package com.example.demo.feature.logistica.service;

import com.example.demo.domain.model.*;
import com.example.demo.domain.repository.*;
import com.example.demo.feature.logistica.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioLogisticoTest {

    @Mock private ClienteRepository clienteRepository;
    @Mock private DireccionRepository direccionRepository;
    @Mock private RepartidorRepository repartidorRepository;
    @Mock private VehiculoRepository vehiculoRepository;
    @Mock private RutaRepository rutaRepository;
    @Mock private EnvioRepository envioRepository;
    @Mock private PaqueteRepository paqueteRepository;
    @Mock private AsignacionRepository asignacionRepository;
    @Mock private EventoSeguimientoRepository eventoRepository;
    @Mock private IncidenciaRepository incidenciaRepository;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ServicioLogistico servicioLogistico;

    private Repartidor repartidorValido;
    private Vehiculo vehiculoValido;
    private Usuario usuarioRepartidor;

    @BeforeEach
    void setUp() {
        usuarioRepartidor = new Usuario();
        usuarioRepartidor.setId(10L);
        usuarioRepartidor.setEmail("repartidor@test.com");
        usuarioRepartidor.setNombre("Carlos");
        usuarioRepartidor.setApellido("Perez");

        repartidorValido = new Repartidor();
        repartidorValido.setId(1L);
        repartidorValido.setUsuario(usuarioRepartidor);
        repartidorValido.setDocumento("45678901");
        repartidorValido.setLicenciaConducir("Q12345678");
        repartidorValido.setDisponible(true);

        vehiculoValido = new Vehiculo();
        vehiculoValido.setId(1L);
        vehiculoValido.setPlaca("ABC-123");
        vehiculoValido.setMarca("Toyota");
        vehiculoValido.setCapacidadKg(new java.math.BigDecimal("500.0"));
        vehiculoValido.setActivo(true);
    }

    @Test
    @DisplayName("crearRuta: Rechaza si el repartidor no tiene licencia de conducir")
    void crearRuta_sinLicencia_lanzaConflicto() {
        repartidorValido.setLicenciaConducir(null);
        when(repartidorRepository.findById(1L)).thenReturn(Optional.of(repartidorValido));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculoValido));

        CrearRutaRequest request = new CrearRutaRequest("Ruta Centro", LocalDate.now(), 1L, 1L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                servicioLogistico.crearRuta(request));

        assertEquals(HttpStatus.CONFLICT, HttpStatus.valueOf(ex.getStatusCode().value()));
        assertTrue(ex.getReason().contains("licencia de conducir"));
    }

    @Test
    @DisplayName("crearRuta: Rechaza si el repartidor no está disponible")
    void crearRuta_repartidorNoDisponible_lanzaConflicto() {
        repartidorValido.setDisponible(false);
        when(repartidorRepository.findById(1L)).thenReturn(Optional.of(repartidorValido));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculoValido));

        CrearRutaRequest request = new CrearRutaRequest("Ruta Centro", LocalDate.now(), 1L, 1L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                servicioLogistico.crearRuta(request));

        assertEquals(HttpStatus.CONFLICT, HttpStatus.valueOf(ex.getStatusCode().value()));
        assertTrue(ex.getReason().contains("no esta disponible"));
    }

    @Test
    @DisplayName("crearRuta: Rechaza si el repartidor ya tiene una ruta activa")
    void crearRuta_repartidorConRutaActiva_lanzaConflicto() {
        when(repartidorRepository.findById(1L)).thenReturn(Optional.of(repartidorValido));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculoValido));
        when(rutaRepository.existsByRepartidorIdAndEstadoIn(eq(1L), any())).thenReturn(true);

        CrearRutaRequest request = new CrearRutaRequest("Ruta Centro", LocalDate.now(), 1L, 1L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                servicioLogistico.crearRuta(request));

        assertEquals(HttpStatus.CONFLICT, HttpStatus.valueOf(ex.getStatusCode().value()));
        assertTrue(ex.getReason().contains("ya tiene una ruta activa"));
    }

    @Test
    @DisplayName("asignarEnvio: Rechaza asignar un envío que no está en estado REGISTRADO")
    void asignarEnvio_estadoInvalido_lanzaConflicto() {
        Envio envio = new Envio();
        envio.setId(100L);
        envio.setEstado(EstadoEnvio.EN_RUTA);

        Ruta ruta = new Ruta();
        ruta.setId(5L);
        ruta.setEstado(EstadoRuta.PROGRAMADA);

        when(envioRepository.findById(100L)).thenReturn(Optional.of(envio));
        when(rutaRepository.findById(5L)).thenReturn(Optional.of(ruta));

        CrearAsignacionRequest request = new CrearAsignacionRequest(100L, 5L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                servicioLogistico.asignarEnvio(request, "operador@demo.com"));

        assertEquals(HttpStatus.CONFLICT, HttpStatus.valueOf(ex.getStatusCode().value()));
        assertTrue(ex.getReason().contains("REGISTRADO"));
    }

    @Test
    @DisplayName("asignarEnvio: Rechaza si el envío ya pertenece a una asignación activa")
    void asignarEnvio_yaAsignado_lanzaConflicto() {
        Envio envio = new Envio();
        envio.setId(100L);
        envio.setEstado(EstadoEnvio.REGISTRADO);

        Ruta ruta = new Ruta();
        ruta.setId(5L);
        ruta.setEstado(EstadoRuta.PROGRAMADA);

        when(envioRepository.findById(100L)).thenReturn(Optional.of(envio));
        when(rutaRepository.findById(5L)).thenReturn(Optional.of(ruta));
        when(asignacionRepository.existsByEnvioIdAndActivaTrue(100L)).thenReturn(true);

        CrearAsignacionRequest request = new CrearAsignacionRequest(100L, 5L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                servicioLogistico.asignarEnvio(request, "operador@demo.com"));

        assertEquals(HttpStatus.CONFLICT, HttpStatus.valueOf(ex.getStatusCode().value()));
        assertTrue(ex.getReason().contains("ruta activa"));
    }

    @Test
    @DisplayName("registrarEvento: Rechaza transición ilegal de REGISTRADO directamente a ENTREGADO")
    void registrarEvento_transicionIlegal_lanzaBadRequest() {
        Envio envio = new Envio();
        envio.setId(50L);
        envio.setEstado(EstadoEnvio.REGISTRADO);

        Ruta ruta = new Ruta();
        ruta.setId(1L);
        ruta.setRepartidor(repartidorValido);

        Asignacion asignacion = new Asignacion();
        asignacion.setId(1L);
        asignacion.setEnvio(envio);
        asignacion.setRuta(ruta);
        asignacion.setActiva(true);

        when(envioRepository.findById(50L)).thenReturn(Optional.of(envio));
        when(asignacionRepository.findByEnvioIdAndActivaTrue(50L)).thenReturn(Optional.of(asignacion));

        RegistrarEventoRequest request = new RegistrarEventoRequest(EstadoEnvio.ENTREGADO, "Almacen", "Intento ilegal", "Juan Perez");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                servicioLogistico.registrarEvento(50L, request, "repartidor@test.com"));

        assertEquals(HttpStatus.BAD_REQUEST, HttpStatus.valueOf(ex.getStatusCode().value()));
        assertTrue(ex.getReason().contains("Transicion no permitida"));
    }

    @Test
    @DisplayName("registrarEvento: Rechaza si otro repartidor intenta actualizar el envío")
    void registrarEvento_repartidorAjeno_lanzaForbidden() {
        Envio envio = new Envio();
        envio.setId(50L);
        envio.setEstado(EstadoEnvio.ASIGNADO);

        Ruta ruta = new Ruta();
        ruta.setId(1L);
        ruta.setRepartidor(repartidorValido);

        Asignacion asignacion = new Asignacion();
        asignacion.setId(1L);
        asignacion.setEnvio(envio);
        asignacion.setRuta(ruta);
        asignacion.setActiva(true);

        when(envioRepository.findById(50L)).thenReturn(Optional.of(envio));
        when(asignacionRepository.findByEnvioIdAndActivaTrue(50L)).thenReturn(Optional.of(asignacion));

        RegistrarEventoRequest request = new RegistrarEventoRequest(EstadoEnvio.EN_RUTA, "En camino", "Iniciando", null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                servicioLogistico.registrarEvento(50L, request, "otro.repartidor@test.com"));

        assertEquals(HttpStatus.FORBIDDEN, HttpStatus.valueOf(ex.getStatusCode().value()));
    }

    @Test
    @DisplayName("registrarEvento: Exige receptorNombre al marcar ENTREGADO")
    void registrarEvento_entregadoSinReceptor_lanzaBadRequest() {
        Envio envio = new Envio();
        envio.setId(50L);
        envio.setEstado(EstadoEnvio.EN_RUTA);
        envio.setCodigoSeguimiento("CL-TEST12345");
        envio.setDescripcion("Paquete de prueba");

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombres("Maria");
        cliente.setApellidos("Lopez");
        envio.setCliente(cliente);

        Direccion direccion = new Direccion();
        direccion.setDireccion("Av. Principal 123");
        direccion.setDistrito("Miraflores");
        envio.setDireccionDestino(direccion);

        Ruta ruta = new Ruta();
        ruta.setId(1L);
        ruta.setRepartidor(repartidorValido);

        Asignacion asignacion = new Asignacion();
        asignacion.setId(1L);
        asignacion.setEnvio(envio);
        asignacion.setRuta(ruta);
        asignacion.setActiva(true);

        when(envioRepository.findById(50L)).thenReturn(Optional.of(envio));
        when(asignacionRepository.findByEnvioIdAndActivaTrue(50L)).thenReturn(Optional.of(asignacion));

        RegistrarEventoRequest request = new RegistrarEventoRequest(EstadoEnvio.ENTREGADO, "Destino", "Sin receptor", "   ");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                servicioLogistico.registrarEvento(50L, request, "repartidor@test.com"));

        assertEquals(HttpStatus.BAD_REQUEST, HttpStatus.valueOf(ex.getStatusCode().value()));
        assertTrue(ex.getReason().contains("Debe indicar quien recibio"));
    }

    @Test
    @DisplayName("registrarEvento: Cierra la ruta automáticamente al entregar el último envío de la ruta")
    void registrarEvento_entregaUltimoEnvio_finalizaRuta() {
        Envio envio = new Envio();
        envio.setId(50L);
        envio.setEstado(EstadoEnvio.EN_RUTA);
        envio.setCodigoSeguimiento("CL-TEST12345");
        envio.setDescripcion("Paquete de prueba");

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombres("Maria");
        cliente.setApellidos("Lopez");
        envio.setCliente(cliente);

        Direccion direccion = new Direccion();
        direccion.setDireccion("Av. Principal 123");
        direccion.setDistrito("Miraflores");
        envio.setDireccionDestino(direccion);

        Ruta ruta = new Ruta();
        ruta.setId(1L);
        ruta.setEstado(EstadoRuta.EN_CURSO);
        ruta.setRepartidor(repartidorValido);

        Asignacion asignacion = new Asignacion();
        asignacion.setId(1L);
        asignacion.setEnvio(envio);
        asignacion.setRuta(ruta);
        asignacion.setActiva(true);

        when(envioRepository.findById(50L)).thenReturn(Optional.of(envio));
        when(asignacionRepository.findByEnvioIdAndActivaTrue(50L)).thenReturn(Optional.of(asignacion));
        when(asignacionRepository.countByRutaIdAndActivaTrue(1L)).thenReturn(0L);

        RegistrarEventoRequest request = new RegistrarEventoRequest(EstadoEnvio.ENTREGADO, "Destino", "Entregado conforme", "Juan Destinatario");

        servicioLogistico.registrarEvento(50L, request, "repartidor@test.com");

        assertEquals(EstadoEnvio.ENTREGADO, envio.getEstado());
        assertEquals("Juan Destinatario", envio.getReceptorNombre());
        assertFalse(asignacion.isActiva());
        assertNotNull(asignacion.getFinalizadaAt());
        assertEquals(EstadoRuta.FINALIZADA, ruta.getEstado());
        verify(eventoRepository, times(1)).save(any());
    }
}

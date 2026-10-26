package red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;
import model.Especie;
import model.ModeloJuego;
import org.junit.jupiter.api.Test;

class RedClientTest {
    @Test
    void protocolRoundTripsEveryMessageType() {
        CodificadorJson codificador = CodificadorJson.getInstancia();
        for (TipoMensaje tipo : TipoMensaje.values()) {
            Mensaje original = FabricaMensajes.crear(tipo, null);
            Mensaje recibido = codificador.decodificar(codificador.codificar(original));
            assertEquals(tipo, recibido.getTipo());
        }
    }

    @Test
    void dispatcherRunsHandlersOnSwingThreadAndUpdatesModel() throws Exception {
        ModeloJuego modelo = new ModeloJuego();
        CountDownLatch actualizado = new CountDownLatch(1);
        AtomicBoolean enHiloSwing = new AtomicBoolean();
        modelo.addObserver((source, observable) -> {
            enHiloSwing.set(SwingUtilities.isEventDispatchThread());
            actualizado.countDown();
        });
        DespachadorMensajes despachador = new DespachadorMensajes(modelo);

        DatosPartidaCreada datos = new DatosPartidaCreada();
        datos.codigo = "A7K2";
        despachador.despachar(FabricaMensajes.crear(TipoMensaje.MATCH_CREATED, datos));

        assertTrue(actualizado.await(2, TimeUnit.SECONDS));
        assertEquals("A7K2", modelo.getCodigoPartida());
        assertTrue(enHiloSwing.get());
    }

    @Test
    void listenerReportsMalformedJsonAndContinuesReading() throws Exception {
        ModeloJuego modelo = new ModeloJuego();
        CountDownLatch procesados = new CountDownLatch(2);
        modelo.addObserver((source, observable) -> procesados.countDown());
        DespachadorMensajes despachador = new DespachadorMensajes(modelo);

        BufferedReader lector = new BufferedReader(new java.io.StringReader(
                "esto no es JSON\n"
                        + "{\"tipo\":\"MATCH_CREATED\",\"payload\":{\"codigo\":\"B2C3\"}}\n"));
        new HiloEscucha(lector, despachador, razon -> { }).run();

        assertTrue(procesados.await(2, TimeUnit.SECONDS));
        assertEquals("B2C3", modelo.getCodigoPartida());
        assertTrue(modelo.getUltimoError().contains("mensaje inválido"));
    }

    @Test
    void connectionSendsAndReceivesLineDelimitedJson() throws Exception {
        ModeloJuego modelo = new ModeloJuego();
        CountDownLatch respuestaRecibida = new CountDownLatch(1);
        modelo.addObserver((source, observable) -> {
            if ("A7K2".equals(modelo.getCodigoPartida())) {
                respuestaRecibida.countDown();
            }
        });
        DespachadorMensajes despachador = new DespachadorMensajes(modelo);

        try (ServerSocket servidor = new ServerSocket(0)) {
            ExecutorService servidorExecutor = Executors.newSingleThreadExecutor();
            try {
                Future<String> mensajeRecibido = servidorExecutor.submit(() -> {
                    try (var cliente = servidor.accept();
                            BufferedReader lector = new BufferedReader(new InputStreamReader(
                                        cliente.getInputStream(), StandardCharsets.UTF_8));
                            PrintWriter escritor = new PrintWriter(new OutputStreamWriter(
                                        cliente.getOutputStream(), StandardCharsets.UTF_8), true)) {
                        String linea = lector.readLine();
                        escritor.println("{\"tipo\":\"MATCH_CREATED\",\"payload\":{\"codigo\":\"A7K2\"}}");
                        return linea;
                    }
                });

                ConexionServidor conexion = new ConexionServidor(despachador);
                conexion.conectar("127.0.0.1", servidor.getLocalPort());
                assertTrue(conexion.estaConectado());
                DatosCrearPartida datosCrear = new DatosCrearPartida();
                datosCrear.nickname = "Ana";
                conexion.enviar(FabricaMensajes.crear(TipoMensaje.CREATE_MATCH, datosCrear));

                assertTrue(respuestaRecibida.await(2, TimeUnit.SECONDS));
                Mensaje recibido = CodificadorJson.getInstancia().decodificar(
                        mensajeRecibido.get(2, TimeUnit.SECONDS));
                assertEquals(TipoMensaje.CREATE_MATCH, recibido.getTipo());
                conexion.desconectar();
                assertFalse(conexion.estaConectado());
                assertThrows(IllegalStateException.class, () -> conexion.enviar(
                        FabricaMensajes.crear(TipoMensaje.CREATE_MATCH, datosCrear)));
            } finally {
                servidorExecutor.shutdownNow();
            }
        }
    }
    @Test
    void animalSelectionHandlerCopiesAvailableSpecies() {
        ModeloJuego modelo = new ModeloJuego();
        DatosSeleccion datos = new DatosSeleccion();
        datos.disponibles = new java.util.Vector<>();
        datos.disponibles.add(Especie.JAGUAR);
        datos.disponibles.add(Especie.TUCAN);
        datos.eleccionRival = Especie.DANTA;

        new ManejadorSeleccion(modelo).manejar(
                FabricaMensajes.crear(TipoMensaje.ANIMAL_SELECTION, datos));

        assertEquals(java.util.List.of(Especie.JAGUAR, Especie.TUCAN), modelo.getEspeciesDisponibles());
        assertEquals(Especie.DANTA, modelo.getEspecieRival());
    }

    @Test
    void matchJoinStartStateAndEndHandlersUpdateTheModel() {
        ModeloJuego modelo = new ModeloJuego();
        DatosPartidaCreada confirmacionUnion = new DatosPartidaCreada();
        confirmacionUnion.codigo = "C4D5";
        new ManejadorUnido(modelo).manejar(
                FabricaMensajes.crear(TipoMensaje.MATCH_JOINED, confirmacionUnion));
        assertEquals("C4D5", modelo.getCodigoPartida());

        VistaAnimal animal = new VistaAnimal();
        animal.id = 21;
        animal.especie = Especie.JAGUAR;
        animal.esMio = true;
        animal.energia = 20;
        animal.x = 3.0;
        animal.y = 4.0;
        DatosInicio inicio = new DatosInicio();
        inicio.mapa = new VistaMapa();
        inicio.zonaPropia = 6;
        inicio.manada = new java.util.Vector<>();
        inicio.manada.add(animal);
        new ManejadorInicio(modelo).manejar(FabricaMensajes.crear(TipoMensaje.GAME_STARTED, inicio));
        assertEquals(6, modelo.getZonaActual());
        assertEquals(Especie.JAGUAR, modelo.getMiEspecie());
        assertEquals(1, modelo.getMisAnimales().size());

        EstadoVisible estado = new EstadoVisible();
        estado.idZona = 7;
        estado.animales = new java.util.Vector<>();
        animal.x = 8.0;
        estado.animales.add(animal);
        new ManejadorEstado(modelo).manejar(FabricaMensajes.crear(TipoMensaje.STATE_UPDATE, estado));
        assertEquals(7, modelo.getZonaActual());
        assertEquals(8.0, modelo.getAnimales().get(21).getPosActual().getX());

        ResumenPartida resumen = new ResumenPartida();
        resumen.ganador = "Ana";
        new ManejadorFinPartida(modelo).manejar(
                FabricaMensajes.crear(TipoMensaje.GAME_OVER, resumen));
        assertEquals("Ana", modelo.getResumen().ganador);
    }

    @Test
    void dispatcherUsesErrorHandlerForUnregisteredMessage() throws Exception {
        ModeloJuego modelo = new ModeloJuego();
        CountDownLatch errorRecibido = new CountDownLatch(1);
        modelo.addObserver((source, observable) -> errorRecibido.countDown());
        DespachadorMensajes despachador = new DespachadorMensajes(modelo);

        despachador.despachar(FabricaMensajes.crear(
                TipoMensaje.CREATE_MATCH, new DatosCrearPartida()));

        assertTrue(errorRecibido.await(2, TimeUnit.SECONDS));
        assertTrue(modelo.getUltimoError().contains("CREATE_MATCH"));
    }

}

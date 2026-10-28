package iu;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Container;
import java.awt.Component;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JPanel;
import control.ControladorJuego;
import control.ControladorLobby;
import model.Especie;
import model.ModeloJuego;
import model.Punto;
import model.Seleccion;
import red.ConexionServidor;
import red.DatosVerZona;
import red.DatosInicio;
import red.EstadoVisible;
import red.Mensaje;
import red.TipoMensaje;
import red.VistaMapa;
import red.VistaZona;
import red.VistaAnimal;
import org.junit.jupiter.api.Test;

class UiRenderingTest {
    @Test
    void svgAssetsLoadAndAreCached() {
        CacheSvg cache = CacheSvg.getInstancia();

        for (Especie especie : Especie.values()) {
            BufferedImage imagen = cache.getImagen(especie.getRutaSvg());
            assertNotNull(imagen, especie.getNombre());
            assertSame(imagen, cache.getImagen(especie.getRutaSvg()));
        }
        assertNotNull(cache.getImagen("svg/taza_cafe.svg"));
        assertNotNull(cache.getImagen("svg/bandera_azul.svg"));
        assertNotNull(cache.getImagen("svg/atacando.svg"));
    }

    @Test
    void mapAndScreensCanRenderWithoutAWindow() {
        ModeloJuego modelo = new ModeloJuego();
        Seleccion seleccion = new Seleccion();
        PanelMapa mapa = new PanelMapa(modelo, seleccion, null);
        mapa.setSize(800, 500);
        BufferedImage canvas = new BufferedImage(800, 500, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = canvas.createGraphics();
        mapa.paint(graphics);
        graphics.dispose();
        mapa.detenerAnimacion();

        assertTrue(canvas.getRGB(400, 250) != 0);
        render(new PantallaLobby(modelo, null, null));
        render(new PantallaEspera(modelo, () -> { }));
        render(new PantallaSeleccion(modelo, null, () -> { }));
        PantallaJuego juego = new PantallaJuego(modelo, null, seleccion, () -> { });
        render(juego);
        juego.detenerAnimacion();
        render(new PantallaResumen(modelo, () -> { }));
    }

    @Test
    void regionViewMapsMouseCoordinatesToFocusedZone() {
        ModeloJuego modelo = new ModeloJuego();
        VistaMapa mapa = new VistaMapa();
        VistaZona zona = new VistaZona();
        zona.id = 5;
        zona.x = 320;
        zona.y = 240;
        zona.ancho = 320;
        zona.alto = 240;
        mapa.zonas = new java.util.Vector<>();
        mapa.zonas.add(zona);
        modelo.setMapa(mapa);
        modelo.setZonaActual(5);

        PanelMapa panel = new PanelMapa(modelo, new Seleccion(), null);
        panel.setSize(800, 500);
        assertEquals(new Punto(480, 360), panel.pantallaAMundo(new java.awt.Point(400, 250)));
        assertEquals(new java.awt.Point(400, 250), panel.mundoAPantalla(new Punto(480, 360)));
        panel.detenerAnimacion();
    }

    @Test
    void regionArrowsNavigateEveryZoneFromTheMap() {
        ModeloJuego modelo = new ModeloJuego();
        VistaMapa mapa = new VistaMapa();
        mapa.zonas = new Vector<>();
        for (int id = 1; id <= 3; id++) {
            VistaZona zona = new VistaZona();
            zona.id = id;
            zona.x = (id - 1) * 320;
            zona.ancho = 320;
            zona.alto = 240;
            mapa.zonas.add(zona);
        }
        modelo.setMapa(mapa);
        modelo.setZonaActual(1);
        modelo.setZonasVisibles(new Vector<>(java.util.List.of(1)));
        FakeConexion conexion = new FakeConexion();
        Seleccion seleccion = new Seleccion();
        PantallaJuego pantalla = new PantallaJuego(modelo,
                new ControladorJuego(conexion, seleccion), seleccion, () -> { });

        ((javax.swing.JButton) findNamed(pantalla, "regionSiguiente")).doClick();

        assertEquals(TipoMensaje.VIEW_ZONE, conexion.ultimo.getTipo());
        assertEquals(2, ((DatosVerZona) conexion.ultimo.getPayload(DatosVerZona.class)).idZona);
        pantalla.detenerAnimacion();
    }

    @Test
    void unoccupiedRegionRendersInGrayscaleWithCenteredFog() {
        ModeloJuego modelo = new ModeloJuego();
        VistaMapa mapa = new VistaMapa();
        VistaZona zona = new VistaZona();
        zona.id = 1;
        zona.ancho = 320;
        zona.alto = 240;
        mapa.zonas = new Vector<>(java.util.List.of(zona));
        mapa.caminos = new Vector<>();
        modelo.setMapa(mapa);
        modelo.setZonaActual(1);
        modelo.setZonasVisibles(new Vector<>());

        PanelMapa panel = new PanelMapa(modelo, new Seleccion(), null);
        panel.setSize(800, 500);
        BufferedImage canvas = new BufferedImage(800, 500, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = canvas.createGraphics();
        panel.paint(graphics);
        graphics.dispose();

        Color pixelGris = new Color(canvas.getRGB(60, 50), true);
        assertEquals(pixelGris.getRed(), pixelGris.getGreen(), 1);
        assertEquals(pixelGris.getGreen(), pixelGris.getBlue(), 1);
        assertTrue((canvas.getRGB(400, 250) >>> 24) > 0);
        assertNotNull(CacheSvg.getInstancia().getImagen("svg/niebla.svg"));
        panel.detenerAnimacion();
    }

    @Test
    void hoveredAnimalShowsBlueRing() {
        ModeloJuego modelo = new ModeloJuego();
        EstadoVisible estado = new EstadoVisible();
        estado.animales = new Vector<>();
        estado.tazas = new Vector<>();
        VistaAnimal animal = new VistaAnimal();
        animal.id = 1;
        animal.especie = Especie.JAGUAR;
        animal.x = 640;
        animal.y = 360;
        animal.energia = 20;
        estado.animales.add(animal);
        modelo.aplicarEstado(estado, 1_000L);

        PanelMapa panel = new PanelMapa(modelo, new Seleccion(), null);
        panel.setSize(1280, 720);
        panel.actualizarAnimalBajoCursor(new java.awt.Point(640, 360));
        BufferedImage canvas = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = canvas.createGraphics();
        panel.paint(graphics);
        graphics.dispose();
        panel.detenerAnimacion();

        Color anillo = new Color(canvas.getRGB(667, 360), true);
        assertTrue(anillo.getBlue() > anillo.getRed());
    }

    @Test
    void lobbyValidationDoesNotMoveCenteredControls() {
        PantallaLobby lobby = new PantallaLobby(new ModeloJuego(), null, null);
        lobby.setSize(900, 600);
        layoutRecursivamente(lobby);

        Component create = findNamed(lobby, "crearPartida");
        Component join = findNamed(lobby, "codigoPartida");
        Component message = findNamed(lobby, "mensajeLobby");
        Rectangle createBounds = create.getBounds();
        Rectangle joinBounds = join.getBounds();
        Rectangle messageBounds = message.getBounds();

        layoutRecursivamente(lobby);
        assertEquals(createBounds, create.getBounds());
        assertEquals(joinBounds, join.getBounds());
        assertEquals(messageBounds, message.getBounds());
    }

    @Test
    void waitingScreenExitButtonFitsInsideItsCard() {
        PantallaEspera espera = new PantallaEspera(new ModeloJuego(), () -> { });
        espera.setSize(900, 600);
        layoutRecursivamente(espera);

        Component tarjeta = findNamed(espera, "tarjetaEspera");
        Component salir = findNamed(espera, "salirPartida");
        Rectangle boundsEnTarjeta = javax.swing.SwingUtilities.convertRectangle(
                salir.getParent(), salir.getBounds(), tarjeta);

        assertTrue(new Rectangle(0, 0, tarjeta.getWidth(), tarjeta.getHeight())
                .contains(boundsEnTarjeta));
    }

    @Test
    void lobbyDoesNotNavigateWhenJoinIsRejected() {
        ModeloJuego modelo = new ModeloJuego();
        List<Boolean> destinos = new ArrayList<>();
        PantallaLobby lobby = new PantallaLobby(modelo,
                new ControladorLobby(new FakeConexion()), destinos::add);
        lobby.setSize(900, 600);
        layoutRecursivamente(lobby);
        ((javax.swing.JTextField) findNamed(lobby, "nickname")).setText("Ana");
        ((javax.swing.JTextField) findNamed(lobby, "codigoPartida")).setText("A1B2");
        ((javax.swing.JButton) findNamed(lobby, "unirsePartida")).doClick();

        assertTrue(destinos.isEmpty());
        modelo.setUltimoError("La partida está llena");
        lobby.refrescar();

        assertTrue(destinos.isEmpty());
    }

    @Test
    void lobbyNavigatesToWaitingScreenOnlyAfterCreationIsAccepted() {
        ModeloJuego modelo = new ModeloJuego();
        List<Boolean> destinos = new ArrayList<>();
        PantallaLobby lobby = new PantallaLobby(modelo,
                new ControladorLobby(new FakeConexion()), destinos::add);
        lobby.setSize(900, 600);
        layoutRecursivamente(lobby);
        ((javax.swing.JTextField) findNamed(lobby, "nickname")).setText("Ana");
        ((javax.swing.JButton) findNamed(lobby, "crearPartida")).doClick();

        assertTrue(destinos.isEmpty());
        modelo.setCodigoPartida("A1B2");
        lobby.refrescar();

        assertEquals(List.of(Boolean.TRUE), destinos);
    }

    @Test
    void rejectedSpeciesChoiceDoesNotOpenGameScreen() {
        ModeloJuego modelo = new ModeloJuego();
        List<Boolean> destinos = new ArrayList<>();
        PantallaSeleccion seleccion = new PantallaSeleccion(modelo,
                new ControladorLobby(new FakeConexion()), () -> destinos.add(Boolean.TRUE));
        seleccion.setSize(1000, 700);
        layoutRecursivamente(seleccion);
        JLabel ayuda = (JLabel) findNamed(seleccion, "ayudaSeleccionAnimal");
        String textoAyuda = ayuda.getText();
        Color colorAyuda = ayuda.getForeground();
        ((javax.swing.JButton) findNamed(seleccion, "especie-JAGUAR")).doClick();
        ((javax.swing.JButton) findNamed(seleccion, "entrarAlBosque")).doClick();

        assertTrue(destinos.isEmpty());
        modelo.setUltimoError("La especie ya fue elegida por el otro jugador");
        seleccion.refrescar();

        assertTrue(destinos.isEmpty());
        assertEquals(null, modelo.getMiEspecie());
        assertTrue(!modelo.isSeleccionConfirmada());
        assertEquals(textoAyuda, ayuda.getText());
        assertEquals(colorAyuda, ayuda.getForeground());
        assertTrue(((javax.swing.JButton) findNamed(seleccion, "entrarAlBosque")).isEnabled());
        assertTrue(((javax.swing.JButton) findNamed(seleccion, "especie-JAGUAR")).isEnabled());
        JLabel estadoSeleccion = (JLabel) findNamed(seleccion, "estadoSeleccion");
        assertEquals("Selecciona una especie para continuar", estadoSeleccion.getText());
        assertEquals(TemaUI.CREMA, estadoSeleccion.getForeground());
    }

    @Test
    void acceptedSpeciesChoiceOpensGameOnlyWhenServerStartsMatch() {
        ModeloJuego modelo = new ModeloJuego();
        List<Boolean> destinos = new ArrayList<>();
        PantallaSeleccion seleccion = new PantallaSeleccion(modelo,
                new ControladorLobby(new FakeConexion()), () -> destinos.add(Boolean.TRUE));
        seleccion.setSize(1000, 700);
        layoutRecursivamente(seleccion);
        ((javax.swing.JButton) findNamed(seleccion, "especie-JAGUAR")).doClick();
        ((javax.swing.JButton) findNamed(seleccion, "entrarAlBosque")).doClick();

        modelo.setSeleccionConfirmada(true);
        seleccion.refrescar();
        assertTrue(destinos.isEmpty());

        DatosInicio inicio = new DatosInicio();
        inicio.mapa = new VistaMapa();
        modelo.aplicarInicio(inicio);
        seleccion.refrescar();

        assertEquals(List.of(Boolean.TRUE), destinos);
    }

    @Test
    void speciesRowsHaveThreeCenteredAboveFour() {
        PantallaSeleccion seleccion = new PantallaSeleccion(new ModeloJuego(), null, () -> { });
        seleccion.setSize(1000, 700);
        layoutRecursivamente(seleccion);

        Container filaSuperior = (Container) findNamed(seleccion, "filaSuperiorEspecies");
        Container filaInferior = (Container) findNamed(seleccion, "filaInferiorEspecies");
        assertEquals(3, filaSuperior.getComponentCount());
        assertEquals(4, filaInferior.getComponentCount());

        Rectangle first = filaSuperior.getComponent(0).getBounds();
        Rectangle last = filaSuperior.getComponent(2).getBounds();
        double centerItems = (first.getMinX() + last.getMaxX()) / 2.0;
        assertEquals(filaSuperior.getWidth() / 2.0, centerItems, 1.0);
    }

    @Test
    void opponentSpeciesIsDisabledAndAnyConflictingLocalChoiceIsCleared() {
        ModeloJuego modelo = new ModeloJuego();
        modelo.setEspeciesDisponibles(new Vector<>(Arrays.asList(Especie.values())));
        PantallaSeleccion pantalla = new PantallaSeleccion(modelo, null, () -> { });

        ((javax.swing.JButton) findNamed(pantalla, "especie-JAGUAR")).doClick();
        modelo.setEspecieRival(Especie.JAGUAR);
        pantalla.refrescar();

        assertFalse(((javax.swing.JButton) findNamed(pantalla, "especie-JAGUAR")).isEnabled());
        assertEquals("El rival eligio Jaguar. Selecciona otra especie.",
                ((JLabel) findNamed(pantalla, "estadoSeleccion")).getText());
        assertTrue(((javax.swing.JButton) findNamed(pantalla, "especie-DANTA")).isEnabled());
    }

    @Test
    void selectionWaitsForServerConfirmationBeforeEnteringGame() {
        ModeloJuego modelo = new ModeloJuego();
        modelo.setEspeciesDisponibles(new Vector<>(Arrays.asList(Especie.values())));
        boolean[] continued = {false};
        PantallaSeleccion pantalla = new PantallaSeleccion(modelo, null, () -> continued[0] = true);

        ((javax.swing.JButton) findNamed(pantalla, "especie-JAGUAR")).doClick();
        ((javax.swing.JButton) findNamed(pantalla, "entrarAlBosque")).doClick();

        assertFalse(continued[0]);
        assertEquals("Confirmando especie...", ((JLabel) findNamed(pantalla, "estadoSeleccion")).getText());

        Vector<Especie> restantes = new Vector<>(Arrays.asList(Especie.values()));
        restantes.remove(Especie.JAGUAR);
        modelo.setEspeciesDisponibles(restantes);
        pantalla.refrescar();

        assertTrue(continued[0]);
    }

    @Test
    void rejectedSelectionKeepsPlayerOnSelectionScreenAndShowsServerError() {
        ModeloJuego modelo = new ModeloJuego();
        modelo.setEspeciesDisponibles(new Vector<>(Arrays.asList(Especie.values())));
        boolean[] continued = {false};
        PantallaSeleccion pantalla = new PantallaSeleccion(modelo, null, () -> continued[0] = true);

        ((javax.swing.JButton) findNamed(pantalla, "especie-JAGUAR")).doClick();
        ((javax.swing.JButton) findNamed(pantalla, "entrarAlBosque")).doClick();
        modelo.setUltimoError("La especie no está disponible para este jugador");
        pantalla.refrescar();

        assertFalse(continued[0]);
        assertEquals("La especie no está disponible para este jugador",
                ((JLabel) findNamed(pantalla, "estadoSeleccion")).getText());
        assertEquals(null, modelo.getMiEspecie());
    }

    private void render(JPanel panel) {
        panel.setSize(900, 600);
        layoutRecursivamente(panel);
        BufferedImage canvas = new BufferedImage(900, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = canvas.createGraphics();
        panel.paint(graphics);
        graphics.dispose();
        boolean pintado = false;
        for (int y = 0; y < canvas.getHeight() && !pintado; y++) {
            for (int x = 0; x < canvas.getWidth(); x++) {
                if ((canvas.getRGB(x, y) >>> 24) != 0) {
                    pintado = true;
                    break;
                }
            }
        }
        assertTrue(pintado, panel.getClass().getSimpleName() + " no dibujo contenido");
    }

    private void layoutRecursivamente(Container contenedor) {
        contenedor.doLayout();
        for (java.awt.Component componente : contenedor.getComponents()) {
            if (componente instanceof Container) {
                layoutRecursivamente((Container) componente);
            }
        }
    }

    private Component findNamed(Container parent, String name) {
        for (Component component : parent.getComponents()) {
            if (name.equals(component.getName())) {
                return component;
            }
            if (component instanceof Container) {
                Component nested = findNamed((Container) component, name);
                if (nested != null) {
                    return nested;
                }
            }
        }
        return null;
    }

    private static final class FakeConexion extends ConexionServidor {
        private Mensaje ultimo;

        @Override
        public void enviar(Mensaje mensaje) {
            ultimo = mensaje;
        }
    }
}

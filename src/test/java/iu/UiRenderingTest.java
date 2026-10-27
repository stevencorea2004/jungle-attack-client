package iu;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Container;
import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.JPanel;
import model.Especie;
import model.ModeloJuego;
import model.Seleccion;
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

        ((javax.swing.JButton) create).doClick();
        layoutRecursivamente(lobby);
        assertEquals(createBounds, create.getBounds());
        assertEquals(joinBounds, join.getBounds());
        assertEquals(messageBounds, message.getBounds());

        ((javax.swing.JButton) findNamed(lobby, "unirsePartida")).doClick();
        layoutRecursivamente(lobby);
        assertEquals(createBounds, create.getBounds());
        assertEquals(joinBounds, join.getBounds());
        assertEquals(messageBounds, message.getBounds());
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
}

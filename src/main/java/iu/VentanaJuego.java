package iu;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import control.ControladorJuego;
import control.ControladorLobby;
import model.ModeloJuego;
import model.Seleccion;
import util.IObserver;
import util.Observable;

public class VentanaJuego extends JFrame implements IObserver {
    private CardLayout tarjetas;
    private final Map<String, Pantalla> pantallas = new LinkedHashMap<>();
    private ModeloJuego modelo;
    private PantallaJuego pantallaJuego;
    private final Seleccion seleccion;
    private final JPanel contenedor;
    private final ControladorJuego controladorJuego;

    public VentanaJuego(ModeloJuego modelo, ControladorLobby controladorLobby,
            ControladorJuego controladorJuego, Seleccion seleccion) {
        super("Jungle Attack");
        this.modelo = modelo;
        this.seleccion = seleccion;
        this.controladorJuego = controladorJuego;
        this.tarjetas = new CardLayout();
        this.contenedor = new JPanel(tarjetas);
        this.contenedor.setPreferredSize(new Dimension(1280, 800));
        setContentPane(contenedor);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(960, 650));
        setSize(1280, 800);
        setLocationRelativeTo(null);

        agregarPantalla("lobby", new PantallaLobby(modelo, controladorLobby,
                crear -> mostrarPantalla(crear ? "espera" : "seleccion")));
        agregarPantalla("espera", new PantallaEspera(modelo,
                () -> mostrarPantalla("seleccion"), this::salirPartida));
        agregarPantalla("seleccion", new PantallaSeleccion(modelo, controladorLobby,
                () -> mostrarPantalla("juego"), this::salirPartida));
        pantallaJuego = new PantallaJuego(modelo, controladorJuego, seleccion, () -> {
            salirPartida();
        });
        agregarPantalla("juego", pantallaJuego);
        agregarPantalla("resumen", new PantallaResumen(modelo, () -> {
            if (this.modelo != null) {
                this.modelo.setResumen(null);
            }
            mostrarPantalla("lobby");
        }));

        if (modelo != null) {
            modelo.addObserver(this);
        }
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (VentanaJuego.this.modelo != null) {
                    VentanaJuego.this.modelo.removeObserver(VentanaJuego.this);
                }
                if (pantallaJuego != null) {
                    pantallaJuego.detenerAnimacion();
                }
            }
        });
        mostrarPantalla("lobby");
    }

    private void agregarPantalla(String nombre, Pantalla pantalla) {
        pantallas.put(nombre, pantalla);
        contenedor.add(pantalla, nombre);
        if (modelo != null) {
            modelo.addObserver(pantalla);
        }
    }

    public void mostrarPantalla(String nombre) {
        if (!pantallas.containsKey(nombre)) {
            throw new IllegalArgumentException("Pantalla no registrada: " + nombre);
        }
        tarjetas.show(contenedor, nombre);
        Pantalla pantalla = pantallas.get(nombre);
        pantalla.refrescar();
        pantalla.revalidate();
        pantalla.repaint();
    }

    private void salirPartida() {
        if (controladorJuego != null) {
            controladorJuego.salirPartida();
        }
        if (seleccion != null) {
            seleccion.limpiar();
        }
        if (modelo != null) {
            modelo.reiniciarPartida();
        }
        mostrarPantalla("lobby");
    }

    @Override
    public void update(Object source, Observable ob) {
        if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
            javax.swing.SwingUtilities.invokeLater(() -> update(source, ob));
            return;
        }
        if (modelo != null && modelo.getResumen() != null) {
            mostrarPantalla("resumen");
            return;
        }
        if (modelo != null && modelo.getUltimoError() != null
                && !modelo.getUltimoError().isBlank()) {
            String error = modelo.getUltimoError();
            modelo.limpiarUltimoError();
            DialogoError.mostrar(this, error);
        }
        for (Pantalla pantalla : pantallas.values()) {
            pantalla.refrescar();
        }
    }
}

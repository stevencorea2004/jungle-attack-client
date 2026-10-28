package iu;

import control.ControladorJuego;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.ModeloJuego;
import model.Seleccion;

public class PantallaJuego extends Pantalla {
    private final ControladorJuego controlador;
    private final Seleccion seleccion;
    private final Runnable salir;
    private PanelMapa panelMapa;
    private PanelMarcador marcador;
    private JButton zonaAnterior;
    private JButton zonaSiguiente;
    private JLabel zonaLabel;
    private JLabel estado;

    public PantallaJuego(ModeloJuego modelo, ControladorJuego controlador, Seleccion seleccion, Runnable salir) {
        super(modelo);
        this.controlador = controlador;
        this.seleccion = seleccion;
        this.salir = salir;
        construir();
    }

    @Override
    public void construir() {
        setLayout(new BorderLayout(10, 10));
        setOpaque(true);
        setBackground(TemaUI.VERDE_NOCHE);
        setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        JLabel titulo = TemaUI.etiqueta("JUNGLE ATTACK", Font.BOLD, 22f, TemaUI.CREMA);
        titulo.setHorizontalAlignment(JLabel.LEFT);
        barra.add(titulo, BorderLayout.WEST);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acciones.setOpaque(false);
        zonaAnterior = TemaUI.boton("← Región");
        zonaAnterior.setName("regionAnterior");
        zonaSiguiente = TemaUI.boton("Región →");
        zonaSiguiente.setName("regionSiguiente");
        zonaLabel = TemaUI.etiqueta("Región", Font.BOLD, 13f, TemaUI.CREMA);
        zonaAnterior.addActionListener(e -> cambiarRegion(-1));
        zonaSiguiente.addActionListener(e -> cambiarRegion(1));
        JButton menu = TemaUI.boton("Salir de partida");
        menu.addActionListener(e -> salir.run());
        acciones.add(zonaAnterior);
        acciones.add(zonaLabel);
        acciones.add(zonaSiguiente);
        acciones.add(menu);
        barra.add(acciones, BorderLayout.EAST);
        add(barra, BorderLayout.NORTH);

        panelMapa = new PanelMapa(modelo, seleccion, controlador);
        marcador = new PanelMarcador(modelo, seleccion);
        JPanel centro = new JPanel(new BorderLayout(12, 0));
        centro.setOpaque(false);
        centro.add(panelMapa, BorderLayout.CENTER);
        centro.add(marcador, BorderLayout.EAST);
        add(centro, BorderLayout.CENTER);

        estado = TemaUI.etiqueta("Arrastra para seleccionar animales | Clic en rival para atacar | Clic derecho para moverte",
                Font.PLAIN, 12f, TemaUI.CREMA);
        add(estado, BorderLayout.SOUTH);
    }

    @Override
    public void refrescar() {
        actualizarZonas();
        if (panelMapa != null) {
            panelMapa.repaint();
        }
        if (marcador != null) {
            marcador.repaint();
        }
    }

    public void detenerAnimacion() {
        if (panelMapa != null) {
            panelMapa.detenerAnimacion();
        }
    }

    private void actualizarZonas() {
        if (zonaLabel == null || modelo == null) {
            return;
        }
        java.util.Vector<Integer> zonas = zonasDelMapa();
        int indice = zonas.indexOf(modelo.getZonaActual());
        zonaLabel.setText(indice < 0 ? "Sin región"
                : "Región " + modelo.getZonaActual() + " (" + (indice + 1) + "/" + zonas.size() + ")");
        zonaAnterior.setEnabled(controlador != null && indice > 0);
        zonaSiguiente.setEnabled(controlador != null && indice >= 0 && indice + 1 < zonas.size());
        if (modelo.getUltimoError() != null && !modelo.getUltimoError().isBlank()) {
            estado.setText(modelo.getUltimoError());
            estado.setForeground(new java.awt.Color(255, 180, 160));
        } else {
            estado.setText("Arrastra para seleccionar animales | Clic en rival para atacar | Clic derecho para moverte");
            estado.setForeground(TemaUI.CREMA);
        }
    }

    private void cambiarRegion(int desplazamiento) {
        if (controlador == null || modelo == null) {
            return;
        }
        java.util.Vector<Integer> zonas = zonasDelMapa();
        int destino = zonas.indexOf(modelo.getZonaActual()) + desplazamiento;
        if (destino >= 0 && destino < zonas.size()) {
            controlador.verZona(zonas.get(destino));
        }
    }

    private java.util.Vector<Integer> zonasDelMapa() {
        java.util.Vector<Integer> zonas = new java.util.Vector<>();
        if (modelo == null || modelo.getMapa() == null || modelo.getMapa().zonas == null) {
            return zonas;
        }
        for (red.VistaZona zona : modelo.getMapa().zonas) {
            if (zona != null && !zonas.contains(zona.id)) {
                zonas.add(zona.id);
            }
        }
        java.util.Collections.sort(zonas);
        return zonas;
    }
}

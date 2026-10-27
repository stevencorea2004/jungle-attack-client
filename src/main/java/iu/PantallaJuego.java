package iu;

import control.ControladorJuego;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
    private JComboBox<Integer> selectorZona;
    private boolean actualizandoZonas;

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
        JLabel zonaLabel = TemaUI.etiqueta("Explorar zona:", Font.BOLD, 13f, TemaUI.CREMA);
        selectorZona = new JComboBox<>();
        selectorZona.addActionListener(e -> {
            if (!actualizandoZonas && controlador != null && selectorZona.getSelectedItem() != null) {
                controlador.verZona((Integer) selectorZona.getSelectedItem());
            }
        });
        JButton menu = TemaUI.boton("Menu");
        menu.addActionListener(e -> salir.run());
        acciones.add(zonaLabel);
        acciones.add(selectorZona);
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

        JLabel ayuda = TemaUI.etiqueta("Arrastra para seleccionar varios animales  |  Clic en rival para atacar  |  Clic derecho para moverte",
                Font.PLAIN, 12f, TemaUI.CREMA);
        add(ayuda, BorderLayout.SOUTH);
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
        if (selectorZona == null || modelo == null) {
            return;
        }
        actualizandoZonas = true;
        selectorZona.removeAllItems();
        if (modelo.getZonasVisibles() != null) {
            for (Integer idZona : modelo.getZonasVisibles()) {
                if (idZona != null) {
                    selectorZona.addItem(idZona);
                }
            }
        }
        if (modelo.getZonasVisibles() != null
                && modelo.getZonasVisibles().contains(modelo.getZonaActual())) {
            selectorZona.setSelectedItem(modelo.getZonaActual());
        }
        selectorZona.setEnabled(selectorZona.getItemCount() > 0);
        actualizandoZonas = false;
    }
}

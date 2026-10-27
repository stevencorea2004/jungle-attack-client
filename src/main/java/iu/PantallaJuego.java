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
        JComboBox<Integer> zona = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            zona.addItem(i);
        }
        zona.addActionListener(e -> {
            if (controlador != null && zona.getSelectedItem() != null) {
                controlador.verZona((Integer) zona.getSelectedItem());
            }
        });
        JButton menu = TemaUI.boton("Menu");
        menu.addActionListener(e -> salir.run());
        acciones.add(zonaLabel);
        acciones.add(zona);
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
}

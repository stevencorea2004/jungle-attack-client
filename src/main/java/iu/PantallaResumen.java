package iu;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.ModeloJuego;
import red.ResumenPartida;

public class PantallaResumen extends Pantalla {
    private final Runnable volver;
    private JLabel ganador;
    private JLabel duracion;
    private JPanel datos;

    public PantallaResumen(ModeloJuego modelo, Runnable volver) {
        super(modelo);
        this.volver = volver;
        construir();
    }

    @Override
    public void construir() {
        setLayout(new BorderLayout());
        setBackground(TemaUI.VERDE_NOCHE);
        setOpaque(true);
        ganador = TemaUI.etiqueta("Partida finalizada", Font.BOLD, 30f, TemaUI.VERDE_NOCHE);
        JPanel tarjeta = TemaUI.tarjeta();
        tarjeta.setLayout(new BorderLayout(0, 20));
        tarjeta.setPreferredSize(new java.awt.Dimension(700, 510));
        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 8));
        encabezado.setOpaque(false);
        encabezado.add(ganador);
        duracion = TemaUI.etiqueta("", Font.PLAIN, 15f, TemaUI.TEXTO);
        encabezado.add(duracion);
        tarjeta.add(encabezado, BorderLayout.NORTH);

        datos = new JPanel(new GridLayout(1, 3, 14, 0));
        datos.setOpaque(false);
        tarjeta.add(datos, BorderLayout.CENTER);

        JButton inicio = TemaUI.boton("Volver al menu");
        inicio.addActionListener(e -> volver.run());
        JPanel pie = new JPanel();
        pie.setOpaque(false);
        pie.add(inicio);
        tarjeta.add(pie, BorderLayout.SOUTH);

        JPanel wrapper = new JPanel(new java.awt.GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(tarjeta);
        add(wrapper, BorderLayout.CENTER);
    }

    @Override
    public void refrescar() {
        if (modelo == null || modelo.getResumen() == null || datos == null) {
            return;
        }
        ResumenPartida resumen = modelo.getResumen();
        ganador.setText("Ganador: " + (resumen.ganador == null ? "Sin definir" : resumen.ganador));
        duracion.setText("Duracion: " + resumen.duracionSeg + " segundos");
        datos.removeAll();
        datos.add(columna("Bots eliminados", resumen.botsEliminados));
        datos.add(columna("Animales perdidos", resumen.animalesPerdidos));
        datos.add(columna("Cafes consumidos", resumen.tazas));
        datos.revalidate();
        datos.repaint();
    }

    private JPanel columna(String titulo, Map<String, Integer> valores) {
        JPanel panel = new JPanel();
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        panel.setBackground(new java.awt.Color(239, 235, 216));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 12, 14, 12));
        JLabel encabezado = TemaUI.etiqueta(titulo, Font.BOLD, 14f, TemaUI.VERDE_NOCHE);
        encabezado.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(encabezado);
        panel.add(javax.swing.Box.createVerticalStrut(15));
        if (valores == null || valores.isEmpty()) {
            JLabel vacio = TemaUI.etiqueta("Sin datos", Font.PLAIN, 13f, TemaUI.TEXTO);
            vacio.setAlignmentX(CENTER_ALIGNMENT);
            panel.add(vacio);
        } else {
            for (Map.Entry<String, Integer> entrada : valores.entrySet()) {
                JLabel linea = TemaUI.etiqueta(entrada.getKey() + ": " + entrada.getValue(),
                        Font.PLAIN, 13f, TemaUI.TEXTO);
                linea.setAlignmentX(CENTER_ALIGNMENT);
                panel.add(linea);
                panel.add(javax.swing.Box.createVerticalStrut(8));
            }
        }
        return panel;
    }
}

package iu;

import control.ControladorLobby;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.Especie;
import model.ModeloJuego;

public class PantallaSeleccion extends Pantalla {
    private final ControladorLobby controlador;
    private final Runnable continuar;
    private Especie seleccionada;
    private JLabel estado;
    private JPanel opciones;
    private JPanel filaSuperior;
    private JPanel filaInferior;
    private final CacheSvg cache = CacheSvg.getInstancia();
    private boolean seleccionPendiente;

    public PantallaSeleccion(ModeloJuego modelo, ControladorLobby controlador, Runnable continuar) {
        super(modelo);
        this.controlador = controlador;
        this.continuar = continuar;
        construir();
    }

    @Override
    public void construir() {
        setLayout(new BorderLayout(0, 24));
        setOpaque(true);
        setBackground(TemaUI.VERDE_NOCHE);
        JLabel titulo = TemaUI.etiqueta("Elige tu animal", Font.BOLD, 32f, TemaUI.CREMA);
        JLabel texto = TemaUI.etiqueta("Cada especie solo puede pertenecer a un jugador", Font.PLAIN, 15f, TemaUI.CREMA);
        JPanel         encabezado = new JPanel(new java.awt.GridLayout(2, 1, 0, 5));
        encabezado.setOpaque(false);
        encabezado.add(titulo);
        encabezado.add(texto);
        add(encabezado, BorderLayout.NORTH);

        opciones = new JPanel(new java.awt.GridLayout(2, 1, 0, 16));
        opciones.setOpaque(false);
        filaSuperior = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 16, 0));
        filaSuperior.setOpaque(false);
        filaSuperior.setName("filaSuperiorEspecies");
        filaInferior = new JPanel(new java.awt.GridLayout(1, 4, 16, 0));
        filaInferior.setOpaque(false);
        filaInferior.setName("filaInferiorEspecies");
        opciones.add(filaSuperior);
        opciones.add(filaInferior);
        add(opciones, BorderLayout.CENTER);
        estado = TemaUI.etiqueta("Selecciona una especie para continuar", Font.PLAIN, 14f, TemaUI.CREMA);
        estado.setName("estadoSeleccion");
        JButton empezar = TemaUI.boton("Entrar al bosque");
        empezar.setName("entrarAlBosque");
        empezar.addActionListener(e -> {
            if (seleccionPendiente) {
                return;
            }
            if (seleccionada == null) {
                estado.setText("Primero elige una especie");
                return;
            }
            if (modelo != null) {
                modelo.setUltimoError(null);
                modelo.setMiEspecie(seleccionada);
            }
            seleccionPendiente = true;
            estado.setText("Confirmando especie...");
            if (controlador != null) {
                controlador.elegirEspecie(seleccionada);
            }
            repaint();
        });
        JPanel pie = new JPanel(new BorderLayout(14, 0));
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(0, 25, 20, 25));
        pie.add(estado, BorderLayout.CENTER);
        pie.add(empezar, BorderLayout.EAST);
        add(pie, BorderLayout.SOUTH);
        cargarEspecies();
    }

    @Override
    public void refrescar() {
        if (modelo != null && estado != null) {
            if (seleccionPendiente && modelo.getUltimoError() != null) {
                seleccionPendiente = false;
                seleccionada = null;
                modelo.setMiEspecie(null);
                estado.setText(modelo.getUltimoError());
                estado.setForeground(new java.awt.Color(166, 55, 44));
                cargarEspecies();
                return;
            }

            Especie especieRival = modelo.getEspecieRival();
            Especie miEspecie = modelo.getMiEspecie();
            Vector<Especie> disponibles = modelo.getEspeciesDisponibles();
            if (seleccionPendiente && miEspecie != null && disponibles != null
                    && !disponibles.contains(miEspecie) && miEspecie != especieRival) {
                seleccionPendiente = false;
                if (continuar != null) {
                    continuar.run();
                }
                return;
            }
            if (especieRival != null) {
                if (seleccionada == especieRival) {
                    seleccionada = null;
                }
                estado.setText("El rival eligio " + especieRival.getNombre()
                        + ". Selecciona otra especie.");
                estado.setForeground(new java.awt.Color(166, 55, 44));
            } else if (modelo.getUltimoError() != null) {
                estado.setText(modelo.getUltimoError());
                estado.setForeground(new java.awt.Color(166, 55, 44));
            }
        }
        cargarEspecies();
    }

    private void cargarEspecies() {
        if (opciones == null) {
            return;
        }
        filaSuperior.removeAll();
        filaInferior.removeAll();
        Vector<Especie> disponibles = modelo == null ? null : modelo.getEspeciesDisponibles();
        Especie especieRival = modelo == null ? null : modelo.getEspecieRival();
        int indice = 0;
        for (Especie especie : Especie.values()) {
            boolean libre = !seleccionPendiente && especie != especieRival
                    && (disponibles == null || disponibles.contains(especie) || especie == seleccionada);
            JButton opcion = new JButton("<html><center>" + especie.getNombre() + "</center></html>");
            opcion.setName("especie-" + especie.name());
            opcion.setVerticalTextPosition(JButton.BOTTOM);
            opcion.setHorizontalTextPosition(JButton.CENTER);
            opcion.setFont(opcion.getFont().deriveFont(Font.BOLD, 15f));
            opcion.setForeground(libre ? TemaUI.TEXTO : new java.awt.Color(130, 130, 130));
            opcion.setBackground(libre ? TemaUI.PANEL : new java.awt.Color(190, 190, 182));
            opcion.setEnabled(libre);
            opcion.setFocusPainted(false);
            opcion.setPreferredSize(new java.awt.Dimension(170, 160));
            opcion.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(especie == seleccionada ? TemaUI.ORO : new java.awt.Color(213, 199, 158),
                            especie == seleccionada ? 3 : 1),
                    BorderFactory.createEmptyBorder(12, 8, 12, 8)));
            try {
                java.awt.image.BufferedImage imagen = cache.getImagen(especie.getRutaSvg());
                if (imagen != null) {
                    opcion.setIcon(new javax.swing.ImageIcon(imagen.getScaledInstance(72, 72, Image.SCALE_SMOOTH)));
                }
            } catch (IllegalArgumentException | IllegalStateException ex) {
                opcion.setText("<html><center>" + especie.getNombre() + "<br>" + (libre ? "" : "Ocupado") + "</center></html>");
                opcion.setToolTipText(ex.getMessage());
            }
            opcion.addActionListener(e -> {
                seleccionada = especie;
                estado.setText(especieRival == null
                        ? especie.getNombre() + " seleccionado"
                        : "Rival: " + especieRival.getNombre() + " | " + especie.getNombre() + " seleccionado");
                estado.setForeground(TemaUI.CREMA);
                cargarEspecies();
            });
            if (indice < 3) {
                filaSuperior.add(opcion);
            } else {
                filaInferior.add(opcion);
            }
            indice++;
        }
        filaSuperior.revalidate();
        filaSuperior.repaint();
        filaInferior.revalidate();
        filaInferior.repaint();
        opciones.revalidate();
        opciones.repaint();
    }
}

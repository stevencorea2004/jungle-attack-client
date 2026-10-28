package iu;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import control.ControladorLobby;
import model.Especie;
import model.ModeloJuego;

public class PantallaSeleccion extends Pantalla {
    private final ControladorLobby controlador;
    private final Runnable continuar;
    private final Runnable salir;
    private Especie seleccionada;
    private JLabel estado;
    private JLabel ayuda;
    private JPanel opciones;
    private JPanel filaSuperior;
    private JPanel filaInferior;
    private JButton botonEmpezar;
    private final CacheSvg cache = CacheSvg.getInstancia();
    private boolean solicitudPendiente;
    private boolean deseleccionPendiente;
    private boolean juegoNotificado;
    private JButton botonDeseleccionar;

    public PantallaSeleccion(ModeloJuego modelo, ControladorLobby controlador, Runnable continuar) {
        this(modelo, controlador, continuar, () -> { });
    }

    public PantallaSeleccion(ModeloJuego modelo, ControladorLobby controlador,
            Runnable continuar, Runnable salir) {
        super(modelo);
        this.controlador = controlador;
        this.continuar = continuar;
        this.salir = salir;
        construir();
    }

    @Override
    public void construir() {
        setLayout(new BorderLayout(0, 24));
        setOpaque(true);
        setBackground(TemaUI.VERDE_NOCHE);
        JLabel titulo = TemaUI.etiqueta("Elige tu animal", Font.BOLD, 32f, TemaUI.CREMA);
        JLabel texto = TemaUI.etiqueta("Cada especie solo puede pertenecer a un jugador", Font.PLAIN, 15f, TemaUI.CREMA);
        estado = TemaUI.etiqueta("Selecciona una especie para continuar", Font.PLAIN, 14f, TemaUI.CREMA);
        estado.setName("estadoSeleccion");
        JPanel encabezado = new JPanel(new java.awt.GridLayout(3, 1, 0, 5));
        encabezado.setOpaque(false);
        encabezado.add(titulo);
        encabezado.add(texto);
        encabezado.add(estado);
        add(encabezado, BorderLayout.NORTH);

        opciones = new JPanel(new java.awt.GridLayout(2, 1, 0, 6));
        opciones.setOpaque(false);
        filaSuperior = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 16, 0));
        filaSuperior.setOpaque(false);
        filaSuperior.setName("filaSuperiorEspecies");
        filaInferior = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 16, 0));
        filaInferior.setOpaque(false);
        filaInferior.setName("filaInferiorEspecies");
        opciones.add(filaSuperior);
        opciones.add(filaInferior);
        add(opciones, BorderLayout.CENTER);
        ayuda = TemaUI.etiqueta("Elige una especie disponible y pulsa Entrar al bosque",
                Font.PLAIN, 13f, TemaUI.CREMA);
        ayuda.setName("ayudaSeleccionAnimal");
        botonEmpezar = TemaUI.boton("Entrar al bosque");
        botonEmpezar.setName("entrarAlBosque");
        botonEmpezar.addActionListener(e -> {
            if (solicitudPendiente || deseleccionPendiente
                    || (modelo != null && modelo.isSeleccionConfirmada())) {
                return;
            }
            if (seleccionada == null) {
                DialogoError.mostrar(this, "Primero elige una especie.");
                return;
            }
            if (controlador == null || modelo == null) {
                DialogoError.mostrar(this, "No hay una conexion disponible con el servidor.");
                return;
            }
            solicitudPendiente = true;
            modelo.setMiEspecie(seleccionada);
            modelo.limpiarUltimoError();
            estado.setText("Enviando seleccion...");
            estado.setForeground(TemaUI.CREMA);
            cargarEspecies();
            actualizarAcciones();
            controlador.elegirEspecie(seleccionada);
        });
        botonDeseleccionar = TemaUI.boton("Deseleccionar animal");
        botonDeseleccionar.setName("deseleccionarAnimal");
        botonDeseleccionar.setVisible(false);
        botonDeseleccionar.addActionListener(e -> deseleccionar());
        JPanel pie = new JPanel(new BorderLayout(14, 0));
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(0, 25, 20, 25));
        pie.add(ayuda, BorderLayout.CENTER);
        JButton salir = TemaUI.boton("Salir de partida");
        salir.addActionListener(e -> this.salir.run());
        pie.add(salir, BorderLayout.WEST);
        JPanel acciones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(botonDeseleccionar);
        acciones.add(botonEmpezar);
        pie.add(acciones, BorderLayout.EAST);
        add(pie, BorderLayout.SOUTH);
        cargarEspecies();
    }

    @Override
    public void refrescar() {
        if (modelo == null || estado == null) {
            return;
        }
        if (modelo.getMapa() != null) {
            if (!juegoNotificado && continuar != null) {
                juegoNotificado = true;
                continuar.run();
            }
            return;
        }
        juegoNotificado = false;
        if (modelo.getUltimoError() != null && !modelo.getUltimoError().isBlank()) {
            if (solicitudPendiente) {
                solicitudPendiente = false;
                seleccionada = null;
                modelo.setMiEspecie(null);
                modelo.setSeleccionConfirmada(false);
            }
            if (deseleccionPendiente) {
                deseleccionPendiente = false;
            }
            estado.setText(modelo.getUltimoError());
            estado.setForeground(new java.awt.Color(166, 55, 44));
        } else if (deseleccionPendiente && !modelo.isSeleccionConfirmada()) {
            deseleccionPendiente = false;
            seleccionada = null;
            modelo.setMiEspecie(null);
            estado.setText("Animal deseleccionado. Elige otra especie.");
            estado.setForeground(TemaUI.CREMA);
        } else if (solicitudPendiente && modelo.isSeleccionConfirmada()) {
            solicitudPendiente = false;
            estado.setText("Especie confirmada. Esperando al otro jugador...");
            estado.setForeground(TemaUI.CREMA);
        } else if (modelo.isSeleccionConfirmada()) {
            solicitudPendiente = false;
            estado.setText("Especie confirmada. Esperando al otro jugador...");
            estado.setForeground(TemaUI.CREMA);
        } else if (solicitudPendiente) {
            estado.setText("Enviando seleccion...");
        } else if (modelo.getEspecieRival() != null) {
            if (seleccionada == modelo.getEspecieRival()) {
                seleccionada = null;
            }
            estado.setText("El rival eligio " + modelo.getEspecieRival().getNombre()
                    + ". Selecciona otra especie.");
            estado.setForeground(new java.awt.Color(166, 55, 44));
        } else if (seleccionada != null) {
            estado.setText(seleccionada.getNombre() + " seleccionado");
            estado.setForeground(TemaUI.CREMA);
        } else {
            estado.setText("Selecciona una especie para continuar");
            estado.setForeground(TemaUI.CREMA);
        }
        cargarEspecies();
        actualizarAcciones();
    }

    public void reiniciarSeleccion() {
        seleccionada = null;
        solicitudPendiente = false;
        deseleccionPendiente = false;
        juegoNotificado = false;
        if (modelo != null) {
            modelo.setMiEspecie(null);
            modelo.setEspecieRival(null);
            modelo.setSeleccionConfirmada(false);
        }
        if (estado != null) {
            estado.setText("Selecciona una especie para continuar");
            estado.setForeground(TemaUI.CREMA);
        }
        cargarEspecies();
        actualizarAcciones();
    }

    private void deseleccionar() {
        if (seleccionada == null || solicitudPendiente || deseleccionPendiente
                || modelo == null || modelo.getMapa() != null
                || modelo.getEspecieRival() != null) {
            return;
        }
        if (modelo.isSeleccionConfirmada()) {
            if (controlador == null) {
                DialogoError.mostrar(this, "No hay una conexion disponible con el servidor.");
                return;
            }
            deseleccionPendiente = true;
            estado.setText("Cancelando seleccion...");
            botonDeseleccionar.setEnabled(false);
            botonEmpezar.setEnabled(false);
            controlador.deseleccionarEspecie();
            return;
        }
        seleccionada = null;
        modelo.setMiEspecie(null);
        estado.setText("Selecciona una especie para continuar");
        actualizarAcciones();
        cargarEspecies();
    }

    private void actualizarAcciones() {
        if (modelo == null || modelo.getMapa() != null) {
            botonDeseleccionar.setVisible(false);
            botonEmpezar.setEnabled(false);
            return;
        }
        boolean rivalYaEligio = modelo.getEspecieRival() != null;
        boolean tieneSeleccion = seleccionada != null || modelo.isSeleccionConfirmada();
        botonDeseleccionar.setVisible(!rivalYaEligio && tieneSeleccion);
        botonDeseleccionar.setEnabled(!solicitudPendiente && !deseleccionPendiente);
        botonEmpezar.setEnabled(seleccionada != null && !solicitudPendiente
                && !deseleccionPendiente && !modelo.isSeleccionConfirmada());
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
            boolean libre = especie != especieRival
                    && (disponibles == null || disponibles.contains(especie) || especie == seleccionada);
            JButton opcion = new JButton("<html><center>" + especie.getNombre() + "</center></html>");
            opcion.setName("especie-" + especie.name());
            opcion.setVerticalTextPosition(JButton.BOTTOM);
            opcion.setHorizontalTextPosition(JButton.CENTER);
            opcion.setFont(opcion.getFont().deriveFont(Font.BOLD, 15f));
            opcion.setForeground(libre ? TemaUI.TEXTO : new java.awt.Color(130, 130, 130));
            opcion.setBackground(libre ? TemaUI.PANEL : new java.awt.Color(190, 190, 182));
            opcion.setEnabled(libre && !solicitudPendiente
                    && !deseleccionPendiente
                    && (modelo == null || !modelo.isSeleccionConfirmada()));
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
                actualizarAcciones();
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

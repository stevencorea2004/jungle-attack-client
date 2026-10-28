package iu;

import control.ControladorJuego;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.AnimalCliente;
import model.Seleccion;

public class ControladorMouse extends MouseAdapter {
    private final ControladorJuego controlador;
    private final PanelMapa panel;
    private Point inicioArrastre;

    public ControladorMouse(ControladorJuego controlador, PanelMapa panel) {
        this.controlador = controlador;
        this.panel = panel;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        inicioArrastre = e.getPoint();
        if (e.getButton() == MouseEvent.BUTTON1) {
            panel.setSeleccionVisual(inicioArrastre, inicioArrastre);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        panel.actualizarAnimalBajoCursor(e.getPoint());
        if (inicioArrastre != null && (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
            panel.setSeleccionVisual(inicioArrastre, e.getPoint());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        panel.actualizarAnimalBajoCursor(e.getPoint());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (inicioArrastre == null) {
            return;
        }
        Point fin = e.getPoint();
        double desplazamiento = inicioArrastre.distance(fin);
        if (e.getButton() == MouseEvent.BUTTON3) {
            limpiarSeleccionInvalida();
            if (controlador != null && panel.getSeleccion() != null
                    && !panel.getSeleccion().getSeleccionados().isEmpty()) {
                controlador.moverSeleccionados(panel.pantallaAMundo(fin));
            }
        } else if (e.getButton() == MouseEvent.BUTTON1) {
            if (desplazamiento >= 8.0) {
                seleccionarRectangulo(inicioArrastre, fin);
            } else {
                clickEnMapa(fin, e.isShiftDown());
            }
        }
        inicioArrastre = null;
        panel.setSeleccionVisual(null, null);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    private void seleccionarRectangulo(Point inicio, Point fin) {
        Seleccion seleccion = panel.getSeleccion();
        if (seleccion == null) {
            return;
        }
        java.util.List<AnimalCliente> propias = panel.getModelo() == null
                ? java.util.List.of() : panel.getModelo().getMisAnimales().stream()
                        .filter(this::estaVivo).toList();
        seleccion.seleccionarEnRectangulo(propias, panel.rectanguloMundo(inicio, fin));
    }

    private void clickEnMapa(Point posicionPantalla, boolean alternar) {
        AnimalCliente animal = panel.animalCercano(posicionPantalla);
        Seleccion seleccion = panel.getSeleccion();
        if (animal == null || !estaVivo(animal)) {
            return;
        }
        if (animal.isEsMio()) {
            if (seleccion != null) {
                limpiarSeleccionInvalida();
                if (alternar) {
                    seleccion.alternar(animal.getId());
                } else {
                    seleccion.limpiar();
                    seleccion.seleccionar(animal.getId());
                }
                panel.repaint();
            }
        } else {
            limpiarSeleccionInvalida();
            if (controlador != null && seleccion != null
                    && !seleccion.getSeleccionados().isEmpty()) {
                controlador.atacar(animal.getId());
            }
        }
    }

    private void limpiarSeleccionInvalida() {
        Seleccion seleccion = panel.getSeleccion();
        if (seleccion == null || panel.getModelo() == null) {
            return;
        }
        for (Integer id : seleccion.getSeleccionados()) {
            AnimalCliente animal = panel.getModelo().getAnimales().get(id);
            if (animal == null || !animal.isEsMio() || !estaVivo(animal)) {
                seleccion.deseleccionar(id);
            }
        }
    }

    private boolean estaVivo(AnimalCliente animal) {
        return animal != null && animal.getEnergia() > 0;
    }
}

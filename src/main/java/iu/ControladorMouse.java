package iu;

import control.ControladorJuego;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.AnimalCliente;
import model.Punto;
import model.Seleccion;

public class ControladorMouse extends MouseAdapter {
    private ControladorJuego controlador;
    private PanelMapa panel;
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
        if (inicioArrastre != null && (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
            panel.setSeleccionVisual(inicioArrastre, e.getPoint());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (inicioArrastre == null) {
            return;
        }
        Point fin = e.getPoint();
        double desplazamiento = inicioArrastre.distance(fin);
        if (e.getButton() == MouseEvent.BUTTON3) {
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
        java.util.Vector<AnimalCliente> propias = panel.getModelo() == null
                ? new java.util.Vector<>() : panel.getModelo().getMisAnimales();
        seleccion.seleccionarEnRectangulo(propias, panel.rectanguloMundo(inicio, fin));
    }

    private void clickEnMapa(Point posicionPantalla, boolean alternar) {
        AnimalCliente animal = panel.animalCercano(posicionPantalla);
        Seleccion seleccion = panel.getSeleccion();
        if (animal == null) {
            return;
        }
        if (animal.isEsMio()) {
            if (seleccion != null) {
                if (alternar) {
                    seleccion.alternar(animal.getId());
                } else {
                    seleccion.limpiar();
                    seleccion.seleccionar(animal.getId());
                }
                panel.repaint();
            }
        } else if (controlador != null && seleccion != null
                && !seleccion.getSeleccionados().isEmpty()) {
            controlador.atacar(animal.getId());
        }
    }
}

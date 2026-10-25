package iu;

import control.ControladorJuego;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ControladorMouse extends MouseAdapter {
    private ControladorJuego controlador;
    private PanelMapa panel;
    private Point inicioArrastre;

    @Override
    public void mousePressed(MouseEvent e) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}

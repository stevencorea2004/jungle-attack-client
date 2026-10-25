package iu;

import java.awt.Graphics;
import java.awt.Point;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.Timer;
import model.ModeloJuego;
import model.Punto;
import model.Seleccion;

public class PanelMapa extends JPanel {
    private ModeloJuego modelo;
    private Seleccion seleccion;
    private Vector<IDibujable> dibujables;
    private Timer refresco;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public Punto pantallaAMundo(Point p) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}

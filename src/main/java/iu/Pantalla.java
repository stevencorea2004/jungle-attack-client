package iu;

import javax.swing.JPanel;
import model.ModeloJuego;
import util.IObserver;
import util.Observable;

public abstract class Pantalla extends JPanel implements IObserver {
    protected ModeloJuego modelo;

    public abstract void construir();

    public abstract void refrescar();

    @Override
    public void update(Object source, Observable ob) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}

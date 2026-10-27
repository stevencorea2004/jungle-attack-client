package iu;

import javax.swing.JPanel;
import model.ModeloJuego;
import util.IObserver;
import util.Observable;

public abstract class Pantalla extends JPanel implements IObserver {
    protected ModeloJuego modelo;

    protected Pantalla(ModeloJuego modelo) {
        this.modelo = modelo;
        setOpaque(false);
    }

    public abstract void construir();

    public abstract void refrescar();

    @Override
    public void update(Object source, Observable ob) {
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            refrescar();
            revalidate();
            repaint();
        } else {
            javax.swing.SwingUtilities.invokeLater(() -> {
                refrescar();
                revalidate();
                repaint();
            });
        }
    }
}

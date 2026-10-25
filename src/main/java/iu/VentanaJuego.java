package iu;

import java.awt.CardLayout;
import java.util.Map;
import javax.swing.JFrame;
import model.ModeloJuego;
import util.IObserver;
import util.Observable;

public class VentanaJuego extends JFrame implements IObserver {
    private CardLayout tarjetas;
    private Map<String, Pantalla> pantallas;
    private ModeloJuego modelo;

    public void mostrarPantalla(String nombre) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    @Override
    public void update(Object source, Observable ob) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}

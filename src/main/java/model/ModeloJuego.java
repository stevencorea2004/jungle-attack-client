package model;

import java.util.Map;
import java.util.Vector;
import red.EstadoVisible;
import red.ResumenPartida;
import red.VistaMapa;
import util.Observable;

public class ModeloJuego extends Observable {
    private String nickname;
    private String codigoPartida;
    private Especie miEspecie;
    private Vector<Especie> especiesDisponibles;
    private VistaMapa mapa;
    private int zonaActual;
    private Vector<Integer> zonasVisibles;
    private Map<Integer, AnimalCliente> animales;
    private Vector<Object> tazas;
    private Punto bandera;
    private int danio;
    private int tazasConsumidas;
    private int botsEliminados;
    private String ultimoError;
    private ResumenPartida resumen;

    public void aplicarEstado(EstadoVisible estado, long recibido) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public Vector<AnimalCliente> getMisAnimales() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public AnimalCliente getAnimalEn(Punto p) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public void setResumen(ResumenPartida resumen) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}

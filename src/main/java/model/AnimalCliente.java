package model;

public class AnimalCliente {
    private int id;
    private Especie especie;
    private boolean esBot;
    private boolean esMio;
    private int energia;
    private Punto posAnterior;
    private Punto posActual;
    private long tiempoAnterior;
    private long tiempoActual;

    public void actualizarPos(Punto p, long tiempo) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public Punto posInterpolada(long ahora) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}

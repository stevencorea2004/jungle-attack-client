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

    public AnimalCliente() {
        this.posAnterior = new Punto();
        this.posActual = new Punto();
    }

    public void actualizarPos(Punto p, long tiempo) {
        this.posAnterior = this.posActual;
        this.posActual = p == null ? new Punto() : p;
        this.tiempoAnterior = this.tiempoActual;
        this.tiempoActual = tiempo;
    }

    public Punto posInterpolada(long ahora) {
        if (posActual == null) {
            return new Punto();
        }
        if (tiempoActual <= tiempoAnterior || ahora <= tiempoAnterior) {
            return posActual;
        }
        double delta = (double) (ahora - tiempoAnterior) / (double) (tiempoActual - tiempoAnterior);
        delta = Math.max(0.0, Math.min(1.0, delta));
        return posAnterior.interpolar(posActual, delta);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Especie getEspecie() { return especie; }
    public void setEspecie(Especie especie) { this.especie = especie; }
    public boolean isEsBot() { return esBot; }
    public void setEsBot(boolean esBot) { this.esBot = esBot; }
    public boolean isEsMio() { return esMio; }
    public void setEsMio(boolean esMio) { this.esMio = esMio; }
    public int getEnergia() { return energia; }
    public void setEnergia(int energia) { this.energia = energia; }
    public Punto getPosActual() { return posActual; }
    public void setPosActual(Punto posActual) { this.posActual = posActual; }
}

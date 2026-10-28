package model;

import java.util.ArrayList;
import java.util.List;
import util.Constantes;

public class AnimalCliente {
    private int id;
    private Especie especie;
    private boolean esBot;
    private boolean esMio;
    private int energia;
    private Integer idObjetivo;
    private Double objetivoX;
    private Double objetivoY;
    private boolean atacando;
    private Punto posAnterior;
    private Punto posActual;
    private final List<MuestraPosicion> historialPosiciones;

    public AnimalCliente() {
        this.posActual = new Punto();
        this.historialPosiciones = new ArrayList<>();
    }

    public void actualizarPos(Punto p, long tiempo) {
        Punto nuevaPosicion = copiar(p == null ? new Punto() : p);
        if (!historialPosiciones.isEmpty()
                && tiempo < historialPosiciones.get(historialPosiciones.size() - 1).tiempo) {
            return;
        }
        if (!historialPosiciones.isEmpty()
                && tiempo == historialPosiciones.get(historialPosiciones.size() - 1).tiempo) {
            historialPosiciones.set(historialPosiciones.size() - 1,
                    new MuestraPosicion(nuevaPosicion, tiempo));
        } else {
            historialPosiciones.add(new MuestraPosicion(nuevaPosicion, tiempo));
        }
        posActual = nuevaPosicion;
        long limite = tiempo - Constantes.RETRASO_INTERPOLACION_MS;
        while (historialPosiciones.size() > 2
                && historialPosiciones.get(1).tiempo <= limite) {
            historialPosiciones.remove(0);
        }
    }

    public Punto posInterpolada(long ahora) {
        if (posActual == null) {
            return new Punto();
        }
        if (historialPosiciones.isEmpty()) {
            return copiar(posActual);
        }
        long tiempoRenderizado = ahora - Constantes.RETRASO_INTERPOLACION_MS;
        MuestraPosicion anterior = historialPosiciones.get(0);
        if (tiempoRenderizado <= anterior.tiempo) {
            return copiar(anterior.posicion);
        }
        for (int i = 1; i < historialPosiciones.size(); i++) {
            MuestraPosicion actual = historialPosiciones.get(i);
            if (tiempoRenderizado <= actual.tiempo) {
                double delta = (double) (tiempoRenderizado - anterior.tiempo)
                        / (double) (actual.tiempo - anterior.tiempo);
                return anterior.posicion.interpolar(actual.posicion, delta);
            }
            anterior = actual;
        }
        return copiar(anterior.posicion);
    }

    private static Punto copiar(Punto punto) {
        return new Punto(punto.getX(), punto.getY());
    }

    private static final class MuestraPosicion {
        private final Punto posicion;
        private final long tiempo;

        private MuestraPosicion(Punto posicion, long tiempo) {
            this.posicion = posicion;
            this.tiempo = tiempo;
        }
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
    public Integer getIdObjetivo() { return idObjetivo; }
    public void setIdObjetivo(Integer idObjetivo) { this.idObjetivo = idObjetivo; }
    public Double getObjetivoX() { return objetivoX; }
    public void setObjetivoX(Double objetivoX) { this.objetivoX = objetivoX; }
    public Double getObjetivoY() { return objetivoY; }
    public void setObjetivoY(Double objetivoY) { this.objetivoY = objetivoY; }
    public boolean isAtacando() { return atacando; }
    public void setAtacando(boolean atacando) { this.atacando = atacando; }
    public Punto getPosActual() { return posActual; }
    public void setPosActual(Punto posActual) {
        this.posActual = posActual == null ? new Punto() : copiar(posActual);
        this.historialPosiciones.clear();
    }
}

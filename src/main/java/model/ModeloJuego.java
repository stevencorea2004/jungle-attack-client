package model;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import red.DatosInicio;
import red.EstadoVisible;
import red.ResumenPartida;
import red.VistaAnimal;
import red.VistaMapa;
import util.Observable;

public class ModeloJuego extends Observable {
    private String nickname;
    private String codigoPartida;
    private Especie miEspecie;
    private Especie especieRival;
    private Vector<Especie> especiesDisponibles;
    private VistaMapa mapa;
    private int zonaActual;
    private Vector<Integer> zonasVisibles;
    private Map<Integer, AnimalCliente> animales = new HashMap<>();
    private Vector<Object> tazas = new Vector<>();
    private Punto bandera;
    private String colorBandera;
    private int animalesVivos;
    private int danio;
    private int tazasConsumidas;
    private int botsEliminados;
    private String ultimoError;
    private ResumenPartida resumen;

    public void aplicarInicio(DatosInicio inicio) {
        if (inicio == null) {
            return;
        }
        mapa = inicio.mapa;
        zonaActual = inicio.zonaPropia;
        zonasVisibles = new Vector<>();
        zonasVisibles.add(inicio.zonaPropia);
        animales.clear();
        miEspecie = null;
        resumen = null;
        danio = 0;
        tazasConsumidas = 0;
        botsEliminados = 0;
        tazas = new Vector<>();
        bandera = null;
        colorBandera = null;
        animalesVivos = inicio.manada == null ? 0 : inicio.manada.size();

        if (inicio.manada != null) {
            for (VistaAnimal vista : inicio.manada) {
                if (vista == null) {
                    continue;
                }
                agregarVistaAnimal(vista, System.currentTimeMillis(), animales);
                if (vista.esMio && miEspecie == null) {
                    miEspecie = vista.especie;
                }
            }
        }
        notifyObservers(this);
    }

    public void aplicarEstado(EstadoVisible estado, long recibido) {
        if (estado == null) {
            return;
        }
        this.zonaActual = estado.idZona;
        this.danio = estado.danio;
        this.tazasConsumidas = estado.tazasConsumidas;
        this.botsEliminados = estado.botsEliminados;
        this.bandera = estado.bandera;
        this.colorBandera = estado.colorBandera;
        this.animalesVivos = estado.animalesVivos;
        this.tazas = estado.tazas == null ? new Vector<>() : new Vector<>(estado.tazas);
        zonasVisibles = estado.zonasVisibles == null
                ? new Vector<>() : new Vector<>(estado.zonasVisibles);

        Map<Integer, AnimalCliente> anteriores = animales;
        animales = new HashMap<>();
        if (estado.animales != null) {
            for (VistaAnimal vista : estado.animales) {
                if (vista == null) {
                    continue;
                }
                agregarVistaAnimal(vista, recibido, anteriores);
            }
        }

        notifyObservers(this);
    }

    private void agregarVistaAnimal(VistaAnimal vista, long recibido,
            Map<Integer, AnimalCliente> anteriores) {
        AnimalCliente animal = anteriores.getOrDefault(vista.id, new AnimalCliente());
        animal.setId(vista.id);
        animal.setEspecie(vista.especie);
        animal.setEsBot(vista.esBot);
        animal.setEsMio(vista.esMio);
        animal.setEnergia(vista.energia);
        animal.setIdObjetivo(vista.idObjetivo);
        animal.setObjetivoX(vista.objetivoX);
        animal.setObjetivoY(vista.objetivoY);
        animal.setAtacando(vista.atacando);
        animal.actualizarPos(new Punto(vista.x, vista.y), recibido);
        animales.put(vista.id, animal);
    }

    public Vector<AnimalCliente> getMisAnimales() {
        Vector<AnimalCliente> resultado = new Vector<>();
        for (AnimalCliente animal : animales.values()) {
            if (animal != null && animal.isEsMio()) {
                resultado.add(animal);
            }
        }
        return resultado;
    }

    public AnimalCliente getAnimalEn(Punto p) {
        if (p == null) {
            return null;
        }
        for (AnimalCliente animal : animales.values()) {
            if (animal != null && animal.getPosActual() != null && animal.getPosActual().distancia(p) < 1.0) {
                return animal;
            }
        }
        return null;
    }

    public void setResumen(ResumenPartida resumen) {
        this.resumen = resumen;
        notifyObservers(this);
    }

    public void reiniciarPartida() {
        codigoPartida = null;
        miEspecie = null;
        especieRival = null;
        especiesDisponibles = null;
        mapa = null;
        zonaActual = 0;
        zonasVisibles = new Vector<>();
        animales.clear();
        tazas = new Vector<>();
        bandera = null;
        colorBandera = null;
        animalesVivos = 0;
        danio = 0;
        tazasConsumidas = 0;
        botsEliminados = 0;
        ultimoError = null;
        resumen = null;
        notifyObservers(this);
    }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getCodigoPartida() { return codigoPartida; }
    public void setCodigoPartida(String codigoPartida) { this.codigoPartida = codigoPartida; }
    public Especie getMiEspecie() { return miEspecie; }
    public void setMiEspecie(Especie miEspecie) { this.miEspecie = miEspecie; }
    public Especie getEspecieRival() { return especieRival; }
    public void setEspecieRival(Especie especieRival) { this.especieRival = especieRival; }
    public Vector<Especie> getEspeciesDisponibles() { return especiesDisponibles; }
    public void setEspeciesDisponibles(Vector<Especie> especiesDisponibles) { this.especiesDisponibles = especiesDisponibles; }
    public VistaMapa getMapa() { return mapa; }
    public void setMapa(VistaMapa mapa) { this.mapa = mapa; }
    public int getZonaActual() { return zonaActual; }
    public void setZonaActual(int zonaActual) { this.zonaActual = zonaActual; }
    public Vector<Integer> getZonasVisibles() { return zonasVisibles; }
    public void setZonasVisibles(Vector<Integer> zonasVisibles) { this.zonasVisibles = zonasVisibles; }
    public Map<Integer, AnimalCliente> getAnimales() { return animales; }
    public void setAnimales(Map<Integer, AnimalCliente> animales) { this.animales = animales; }
    public Vector<Object> getTazas() { return tazas; }
    public void setTazas(Vector<Object> tazas) { this.tazas = tazas; }
    public Punto getBandera() { return bandera; }
    public void setBandera(Punto bandera) { this.bandera = bandera; }
    public String getColorBandera() { return colorBandera; }
    public void setColorBandera(String colorBandera) { this.colorBandera = colorBandera; }
    public int getAnimalesVivos() { return animalesVivos; }
    public void setAnimalesVivos(int animalesVivos) { this.animalesVivos = animalesVivos; }
    public int getDanio() { return danio; }
    public void setDanio(int danio) { this.danio = danio; }
    public int getTazasConsumidas() { return tazasConsumidas; }
    public void setTazasConsumidas(int tazasConsumidas) { this.tazasConsumidas = tazasConsumidas; }
    public int getBotsEliminados() { return botsEliminados; }
    public void setBotsEliminados(int botsEliminados) { this.botsEliminados = botsEliminados; }
    public String getUltimoError() { return ultimoError; }
    public void setUltimoError(String ultimoError) { this.ultimoError = ultimoError; notifyObservers(this); }
    public void limpiarUltimoError() { ultimoError = null; }
    public ResumenPartida getResumen() { return resumen; }
    public void notificarCambio() { notifyObservers(this); }
}

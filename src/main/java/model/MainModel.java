package model;

import java.util.Vector;
import red.EstadoVisible;
import red.VistaAnimal;
import util.IObserver;
import util.Observable;

public final class MainModel {
    private MainModel() {
    }

    public static void main(String[] args) {
        verificarObservable();
        verificarInterpolacion();
        verificarEstado();
        System.out.println("MainModel OK");
    }

    private static void verificarObservable() {
        ModeloJuego modelo = new ModeloJuego();
        ContadorObserver observer = new ContadorObserver();
        modelo.addObserver(observer);
        modelo.setNickname("Jugador");
        modelo.notifyObservers(modelo);
        if (observer.count != 1) {
            throw new AssertionError("Observable no notifico");
        }
        modelo.removeObserver(observer);
        modelo.notifyObservers(modelo);
        if (observer.count != 1) {
            throw new AssertionError("Observable no removio observador");
        }
    }

    private static void verificarInterpolacion() {
        AnimalCliente a = new AnimalCliente();
        a.setPosActual(new Punto(0, 0));
        a.actualizarPos(new Punto(10, 0), 1000L);
        a.actualizarPos(new Punto(20, 0), 2000L);
        Punto p = a.posInterpolada(1500L);
        if (Math.abs(p.getX() - 15.0) > 0.0001) {
            throw new AssertionError("Interpolacion incorrecta: " + p.getX());
        }
    }

    private static void verificarEstado() {
        ModeloJuego modelo = new ModeloJuego();
        EstadoVisible estado = new EstadoVisible();
        estado.idZona = 3;
        estado.danio = 2;
        estado.tazasConsumidas = 4;
        estado.botsEliminados = 1;
        estado.bandera = new Punto(50, 60);
        estado.tazas = new Vector<>();
        estado.animales = new Vector<>();

        VistaAnimal animal = new VistaAnimal();
        animal.id = 7;
        animal.especie = Especie.JAGUAR;
        animal.esBot = false;
        animal.esMio = true;
        animal.x = 10;
        animal.y = 20;
        animal.energia = 9;
        estado.animales.add(animal);

        modelo.aplicarEstado(estado, 5000L);
        if (modelo.getZonaActual() != 3) {
            throw new AssertionError("Zona actual incorrecta");
        }
        if (modelo.getAnimales().get(7) == null) {
            throw new AssertionError("Animal no agregado");
        }
        if (modelo.getMisAnimales().size() != 1) {
            throw new AssertionError("Mis animales incorrectos");
        }
    }

    private static final class ContadorObserver implements IObserver {
        private int count;

        @Override
        public void update(Object source, Observable ob) {
            count++;
        }
    }
}

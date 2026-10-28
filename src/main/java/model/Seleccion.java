package model;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Seleccion {
    private final Set<Integer> seleccionados = new HashSet<>();
    private final List<Runnable> listeners = new ArrayList<>();

    public void seleccionar(int id) {
        if (seleccionados.add(id)) {
            notificarCambio();
        }
    }

    public void deseleccionar(int id) {
        if (seleccionados.remove(id)) {
            notificarCambio();
        }
    }

    public void alternar(int id) {
        if (seleccionados.contains(id)) {
            deseleccionar(id);
        } else {
            seleccionados.add(id);
            notificarCambio();
        }
    }

    public void seleccionarEnRectangulo(Collection<AnimalCliente> animales, Rectangle area) {
        seleccionarEnRectangulo(animales, area, System.currentTimeMillis());
    }

    void seleccionarEnRectangulo(Collection<AnimalCliente> animales, Rectangle area, long ahora) {
        if (animales == null || area == null) {
            return;
        }
        Set<Integer> anteriores = new HashSet<>(seleccionados);
        seleccionados.clear();
        for (AnimalCliente animal : animales) {
            if (animal == null) {
                continue;
            }
            Punto posicion = animal.posInterpolada(ahora);
            int x = (int) Math.round(posicion.getX());
            int y = (int) Math.round(posicion.getY());
            if (area.contains(x, y)) {
                seleccionados.add(animal.getId());
            }
        }
        if (!seleccionados.equals(anteriores)) {
            notificarCambio();
        }
    }

    public void limpiar() {
        if (!seleccionados.isEmpty()) {
            seleccionados.clear();
            notificarCambio();
        }
    }

    public void addChangeListener(Runnable listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeChangeListener(Runnable listener) {
        listeners.remove(listener);
    }

    private void notificarCambio() {
        for (Runnable listener : new ArrayList<>(listeners)) {
            listener.run();
        }
    }

    public boolean estaSeleccionado(int id) {
        return seleccionados.contains(id);
    }

    public Set<Integer> getSeleccionados() {
        return new HashSet<>(seleccionados);
    }
}

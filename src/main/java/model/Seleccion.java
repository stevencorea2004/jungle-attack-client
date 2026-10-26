package model;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class Seleccion {
    private final Set<Integer> seleccionados = new HashSet<>();

    public void seleccionar(int id) {
        seleccionados.add(id);
    }

    public void alternar(int id) {
        if (seleccionados.contains(id)) {
            seleccionados.remove(id);
        } else {
            seleccionados.add(id);
        }
    }

    public void seleccionarEnRectangulo(Vector<AnimalCliente> animales, Rectangle area) {
        if (animales == null || area == null) {
            return;
        }
        seleccionados.clear();
        for (AnimalCliente animal : animales) {
            if (animal == null || animal.getPosActual() == null) {
                continue;
            }
            int x = (int) Math.round(animal.getPosActual().getX());
            int y = (int) Math.round(animal.getPosActual().getY());
            if (area.contains(x, y)) {
                seleccionados.add(animal.getId());
            }
        }
    }

    public void limpiar() {
        seleccionados.clear();
    }

    public boolean estaSeleccionado(int id) {
        return seleccionados.contains(id);
    }

    public Set<Integer> getSeleccionados() {
        return new HashSet<>(seleccionados);
    }
}

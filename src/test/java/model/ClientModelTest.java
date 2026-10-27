package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Vector;
import org.junit.jupiter.api.Test;
import red.EstadoVisible;
import red.VistaAnimal;
import util.IObserver;
import util.Observable;

class ClientModelTest {
    @Test
    void observableNotifiesAndRemovesObservers() {
        ModeloJuego modelo = new ModeloJuego();
        int[] count = {0};
        IObserver observer = (source, observable) -> count[0]++;

        modelo.addObserver(observer);
        modelo.notifyObservers(modelo);
        modelo.removeObserver(observer);
        modelo.notifyObservers(modelo);

        assertEquals(1, count[0]);
    }

    @Test
    void appliesVisibleStateAndPreservesAnimalAcrossSnapshots() {
        ModeloJuego modelo = new ModeloJuego();
        EstadoVisible estado = estadoConAnimal(6, 10, 20, true);
        modelo.aplicarEstado(estado, 1_000L);

        AnimalCliente animal = modelo.getAnimales().get(6);
        assertNotNull(animal);
        assertEquals(1, modelo.getMisAnimales().size());
        assertEquals(4, modelo.getZonaActual());

        modelo.aplicarEstado(estadoConAnimal(6, 30, 40, true), 2_000L);
        assertEquals(animal, modelo.getAnimales().get(6));
        assertEquals(20.0, animal.posInterpolada(1_500L).getX(), 0.0001);
    }

    @Test
    void selectsAnimalsWithinRectangleAndTogglesSelection() {
        AnimalCliente animal = new AnimalCliente();
        animal.setId(12);
        animal.setPosActual(new Punto(20, 30));
        Seleccion seleccion = new Seleccion();

        seleccion.seleccionarEnRectangulo(new Vector<>(java.util.List.of(animal)),
                new java.awt.Rectangle(10, 10, 20, 30));
        assertTrue(seleccion.estaSeleccionado(12));
        seleccion.alternar(12);
        assertTrue(!seleccion.estaSeleccionado(12));
    }

    @Test
    void rectangleSelectionUsesTheSameInterpolatedPositionAsRendering() {
        AnimalCliente animal = new AnimalCliente();
        animal.setId(13);
        animal.actualizarPos(new Punto(20, 30), 1_000L);
        animal.actualizarPos(new Punto(40, 50), 2_000L);
        Seleccion seleccion = new Seleccion();

        seleccion.seleccionarEnRectangulo(new Vector<>(java.util.List.of(animal)),
                new java.awt.Rectangle(29, 39, 2, 2), 1_500L);

        assertTrue(seleccion.estaSeleccionado(13));
    }

    @Test
    void emptyVisibleStateDoesNotCreateAnimals() {
        ModeloJuego modelo = new ModeloJuego();
        EstadoVisible estado = new EstadoVisible();
        estado.animales = new Vector<>();
        estado.tazas = new Vector<>();

        modelo.aplicarEstado(estado, 5L);

        assertNull(modelo.getAnimalEn(new Punto(0, 0)));
        assertTrue(modelo.getAnimales().isEmpty());
    }

    @Test
    void visibleStateKeepsTotalAliveCountAndReplacesPreviousZoneAnimals() {
        ModeloJuego modelo = new ModeloJuego();
        EstadoVisible primeraZona = estadoConAnimal(6, 10, 20, true);
        primeraZona.animalesVivos = 8;
        modelo.aplicarEstado(primeraZona, 1_000L);

        EstadoVisible segundaZona = new EstadoVisible();
        segundaZona.idZona = 5;
        segundaZona.animales = new Vector<>();
        segundaZona.tazas = new Vector<>();
        segundaZona.animalesVivos = 8;
        modelo.aplicarEstado(segundaZona, 2_000L);

        assertEquals(8, modelo.getAnimalesVivos());
        assertEquals(5, modelo.getZonaActual());
        assertTrue(modelo.getAnimales().isEmpty());
    }

    private EstadoVisible estadoConAnimal(int id, double x, double y, boolean mio) {
        EstadoVisible estado = new EstadoVisible();
        estado.idZona = 4;
        estado.animales = new Vector<>();
        estado.tazas = new Vector<>();
        VistaAnimal vista = new VistaAnimal();
        vista.id = id;
        vista.especie = Especie.JAGUAR;
        vista.esMio = mio;
        vista.esBot = false;
        vista.x = x;
        vista.y = y;
        vista.energia = 18;
        estado.animales.add(vista);
        return estado;
    }
}

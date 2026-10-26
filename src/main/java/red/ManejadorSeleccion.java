package red;

import java.util.Objects;
import java.util.Vector;
import model.Especie;
import model.ModeloJuego;

public class ManejadorSeleccion implements IManejadorMensaje {
    private final ModeloJuego modelo;

    public ManejadorSeleccion(ModeloJuego modelo) {
        this.modelo = Objects.requireNonNull(modelo, "El modelo es obligatorio");
    }

    @Override
    public void manejar(Mensaje mensaje) {
        if (mensaje == null) {
            throw new IllegalArgumentException("El mensaje es obligatorio");
        }
        DatosSeleccion datos = (DatosSeleccion) mensaje.getPayload(DatosSeleccion.class);
        if (datos == null) {
            throw new IllegalArgumentException("La respuesta no contiene las especies disponibles");
        }
        Vector<Especie> disponibles =
                datos.disponibles == null ? new Vector<>() : new Vector<>(datos.disponibles);
        modelo.setEspeciesDisponibles(disponibles);
        modelo.setEspecieRival(datos.eleccionRival);
        modelo.notificarCambio();
    }
}

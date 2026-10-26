package red;

import java.util.Objects;
import model.ModeloJuego;

public class ManejadorEstado implements IManejadorMensaje {
    private final ModeloJuego modelo;

    public ManejadorEstado(ModeloJuego modelo) {
        this.modelo = Objects.requireNonNull(modelo, "El modelo es obligatorio");
    }

    @Override
    public void manejar(Mensaje mensaje) {
        if (mensaje == null) {
            throw new IllegalArgumentException("El mensaje es obligatorio");
        }
        EstadoVisible estado = (EstadoVisible) mensaje.getPayload(EstadoVisible.class);
        if (estado == null) {
            throw new IllegalArgumentException("La respuesta no contiene el estado visible");
        }
        modelo.aplicarEstado(estado, System.currentTimeMillis());
    }
}

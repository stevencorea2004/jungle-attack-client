package red;

import java.util.Objects;
import model.ModeloJuego;

public class ManejadorFinPartida implements IManejadorMensaje {
    private final ModeloJuego modelo;

    public ManejadorFinPartida(ModeloJuego modelo) {
        this.modelo = Objects.requireNonNull(modelo, "El modelo es obligatorio");
    }

    @Override
    public void manejar(Mensaje mensaje) {
        if (mensaje == null) {
            throw new IllegalArgumentException("El mensaje es obligatorio");
        }
        ResumenPartida resumen = (ResumenPartida) mensaje.getPayload(ResumenPartida.class);
        if (resumen == null) {
            throw new IllegalArgumentException("La respuesta no contiene el resumen de la partida");
        }
        modelo.setResumen(resumen);
    }
}

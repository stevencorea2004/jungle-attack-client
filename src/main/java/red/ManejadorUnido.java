package red;

import java.util.Objects;
import model.ModeloJuego;

public class ManejadorUnido implements IManejadorMensaje {
    private final ModeloJuego modelo;

    public ManejadorUnido(ModeloJuego modelo) {
        this.modelo = Objects.requireNonNull(modelo, "El modelo es obligatorio");
    }

    @Override
    public void manejar(Mensaje mensaje) {
        if (mensaje == null) {
            throw new IllegalArgumentException("El mensaje es obligatorio");
        }
        if (mensaje.getPayloadJson() != null
                && mensaje.getPayloadJson().has("codigo")
                && !mensaje.getPayloadJson().get("codigo").isJsonNull()) {
            String codigo = mensaje.getPayloadJson().get("codigo").getAsString();
            if (!codigo.isBlank()) {
                modelo.setCodigoPartida(codigo);
            }
        }
        modelo.notificarCambio();
    }
}

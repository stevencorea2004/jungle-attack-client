package red;

import java.util.Objects;
import model.ModeloJuego;

public class ManejadorError implements IManejadorMensaje {
    private final ModeloJuego modelo;

    public ManejadorError(ModeloJuego modelo) {
        this.modelo = Objects.requireNonNull(modelo, "El modelo es obligatorio");
    }

    @Override
    public void manejar(Mensaje mensaje) {
        String error = "Error del servidor";
        if (mensaje != null && mensaje.getPayloadJson() != null) {
            var payload = mensaje.getPayloadJson();
            if (payload.has("error") && !payload.get("error").isJsonNull()) {
                error = payload.get("error").getAsString();
            }
        }
        modelo.setUltimoError(error);
    }
}

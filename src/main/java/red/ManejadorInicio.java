package red;

import java.util.Objects;
import model.ModeloJuego;

public class ManejadorInicio implements IManejadorMensaje {
    private final ModeloJuego modelo;

    public ManejadorInicio(ModeloJuego modelo) {
        this.modelo = Objects.requireNonNull(modelo, "El modelo es obligatorio");
    }

    @Override
    public void manejar(Mensaje mensaje) {
        if (mensaje == null) {
            throw new IllegalArgumentException("El mensaje es obligatorio");
        }
        DatosInicio datos = (DatosInicio) mensaje.getPayload(DatosInicio.class);
        if (datos == null) {
            throw new IllegalArgumentException("La respuesta no contiene los datos de inicio");
        }
        modelo.aplicarInicio(datos);
    }
}

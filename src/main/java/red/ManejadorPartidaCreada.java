package red;

import java.util.Objects;
import model.ModeloJuego;

public class ManejadorPartidaCreada implements IManejadorMensaje {
    private final ModeloJuego modelo;

    public ManejadorPartidaCreada(ModeloJuego modelo) {
        this.modelo = Objects.requireNonNull(modelo, "El modelo es obligatorio");
    }

    @Override
    public void manejar(Mensaje mensaje) {
        DatosPartidaCreada datos = obtenerPayload(mensaje, DatosPartidaCreada.class);
        if (datos.codigo == null || datos.codigo.isBlank()) {
            throw new IllegalArgumentException("La respuesta no contiene el código de partida");
        }
        modelo.setCodigoPartida(datos.codigo);
        modelo.notificarCambio();
    }

    private static <T> T obtenerPayload(Mensaje mensaje, Class<T> tipo) {
        if (mensaje == null) {
            throw new IllegalArgumentException("El mensaje es obligatorio");
        }
        T payload = tipo.cast(mensaje.getPayload(tipo));
        if (payload == null) {
            throw new IllegalArgumentException("El mensaje no contiene datos de partida");
        }
        return payload;
    }
}

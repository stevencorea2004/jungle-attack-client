package red;

import java.util.Map;

public class DespachadorMensajes {
    private Map<TipoMensaje, IManejadorMensaje> manejadores;

    public void registrar(TipoMensaje tipo, IManejadorMensaje manejador) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public void despachar(Mensaje mensaje) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}

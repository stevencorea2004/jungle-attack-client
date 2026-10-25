package red;

public class ManejadorError implements IManejadorMensaje {
    @Override
    public void manejar(Mensaje mensaje) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}

package red;

public final class MainRed {
    private MainRed() {
    }

    public static void main(String[] args) {
        CodificadorJson codificador = CodificadorJson.getInstancia();
        for (TipoMensaje tipo : TipoMensaje.values()) {
            Mensaje original = FabricaMensajes.crear(tipo, null);
            Mensaje recibido = codificador.decodificar(codificador.codificar(original));
            if (recibido == null || recibido.getTipo() != tipo) {
                throw new AssertionError("Falló la ida y vuelta del mensaje " + tipo);
            }
        }
        System.out.println("MainRed OK");
    }
}

package red;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

public class HiloEscucha implements Runnable {
    private final BufferedReader lector;
    private final DespachadorMensajes despachador;
    private final Consumer<String> alDesconectar;

    public HiloEscucha(
            BufferedReader lector,
            DespachadorMensajes despachador,
            Consumer<String> alDesconectar) {
        this.lector = Objects.requireNonNull(lector, "El lector es obligatorio");
        this.despachador = Objects.requireNonNull(despachador, "El despachador es obligatorio");
        this.alDesconectar = Objects.requireNonNull(alDesconectar, "La acción de desconexión es obligatoria");
    }

    @Override
    public void run() {
        try {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.isBlank()) {
                    continue;
                }
                try {
                    Mensaje mensaje = CodificadorJson.getInstancia().decodificar(linea);
                    if (mensaje != null) {
                        despachador.despachar(mensaje);
                    }
                } catch (RuntimeException e) {
                    despachador.despachar(FabricaMensajes.error(
                            "El servidor envió un mensaje inválido: " + e.getMessage()));
                }
            }
            alDesconectar.accept("El servidor cerró la conexión");
        } catch (IOException e) {
            alDesconectar.accept("Se perdió la conexión con el servidor: " + e.getMessage());
        }
    }
}

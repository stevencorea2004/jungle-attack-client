package red;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConexionServidor {
    private Socket socket;
    private PrintWriter escritor;
    private BufferedReader lector;
    private Thread hiloEscucha;
    private DespachadorMensajes despachador;

    public void conectar(String host, int puerto) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public void enviar(Mensaje mensaje) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public void desconectar() {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}

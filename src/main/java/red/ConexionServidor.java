package red;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import model.ModeloJuego;
import util.Constantes;

public class ConexionServidor {
    private static final int TIEMPO_LIMITE_CONEXION_MS = 5000;
    private final DespachadorMensajes despachador;
    private final AtomicBoolean cerrando = new AtomicBoolean(true);
    private Socket socket;
    private PrintWriter escritor;
    private BufferedReader lector;
    private Thread hiloEscucha;

    public ConexionServidor() {
        this(new DespachadorMensajes());
    }

    public ConexionServidor(ModeloJuego modelo) {
        this(new DespachadorMensajes(modelo));
    }

    public ConexionServidor(DespachadorMensajes despachador) {
        this.despachador = Objects.requireNonNull(despachador, "El despachador es obligatorio");
    }

    public synchronized void conectar(String host, int puerto) {
        if (estaConectado()) {
            throw new IllegalStateException("Ya existe una conexión activa");
        }
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("El host es obligatorio");
        }
        if (puerto < 1 || puerto > 65535) {
            throw new IllegalArgumentException("El puerto debe estar entre 1 y 65535");
        }

        Socket nuevoSocket = new Socket();
        try {
            nuevoSocket.connect(new java.net.InetSocketAddress(host, puerto), TIEMPO_LIMITE_CONEXION_MS);
            Charset charset = Charset.forName(Constantes.CHARSET);
            BufferedReader nuevoLector = new BufferedReader(
                    new InputStreamReader(nuevoSocket.getInputStream(), charset));
            PrintWriter nuevoEscritor = new PrintWriter(
                    new OutputStreamWriter(nuevoSocket.getOutputStream(), charset), true);

            socket = nuevoSocket;
            lector = nuevoLector;
            escritor = nuevoEscritor;
            cerrando.set(false);
            hiloEscucha = new Thread(
                    new HiloEscucha(lector, despachador, this::manejarDesconexionRemota),
                    "jungle-attack-listener");
            hiloEscucha.setDaemon(true);
            hiloEscucha.start();
        } catch (IOException e) {
            try {
                nuevoSocket.close();
            } catch (IOException cierre) {
                e.addSuppressed(cierre);
            }
            throw new UncheckedIOException("No se pudo conectar con el servidor", e);
        }
    }

    public synchronized void enviar(Mensaje mensaje) {
        Objects.requireNonNull(mensaje, "El mensaje es obligatorio");
        if (!estaConectado() || escritor == null) {
            throw new IllegalStateException("No hay una conexión activa con el servidor");
        }

        escritor.println(CodificadorJson.getInstancia().codificar(mensaje));
        if (escritor.checkError()) {
            desconectarInterno();
            throw new UncheckedIOException(new IOException("No se pudo enviar el mensaje al servidor"));
        }
    }

    public synchronized void desconectar() {
        desconectarInterno();
    }

    public synchronized boolean estaConectado() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public DespachadorMensajes getDespachador() {
        return despachador;
    }

    private void manejarDesconexionRemota(String razon) {
        if (cerrando.compareAndSet(false, true)) {
            despachador.despachar(FabricaMensajes.error(razon));
            synchronized (this) {
                cerrarRecursos();
            }
        }
    }

    private void desconectarInterno() {
        cerrando.set(true);
        cerrarRecursos();
    }

    private void cerrarRecursos() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo cerrar la conexión con el servidor", e);
        } finally {
            socket = null;
            lector = null;
            escritor = null;
            hiloEscucha = null;
        }
    }
}

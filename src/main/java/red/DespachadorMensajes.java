package red;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.swing.SwingUtilities;
import model.ModeloJuego;

public class DespachadorMensajes {
    private final ConcurrentMap<TipoMensaje, IManejadorMensaje> manejadores =
            new ConcurrentHashMap<>();

    public DespachadorMensajes() {
    }

    public DespachadorMensajes(ModeloJuego modelo) {
        registrarManejadores(modelo);
    }

    public void registrar(TipoMensaje tipo, IManejadorMensaje manejador) {
        Objects.requireNonNull(tipo, "El tipo de mensaje es obligatorio");
        Objects.requireNonNull(manejador, "El manejador es obligatorio");
        manejadores.put(tipo, manejador);
    }

    public void registrarManejadores(ModeloJuego modelo) {
        Objects.requireNonNull(modelo, "El modelo es obligatorio");
        registrar(TipoMensaje.MATCH_CREATED, new ManejadorPartidaCreada(modelo));
        registrar(TipoMensaje.MATCH_JOINED, new ManejadorUnido(modelo));
        registrar(TipoMensaje.ERROR, new ManejadorError(modelo));
        registrar(TipoMensaje.ANIMAL_SELECTION, new ManejadorSeleccion(modelo));
        registrar(TipoMensaje.GAME_STARTED, new ManejadorInicio(modelo));
        registrar(TipoMensaje.STATE_UPDATE, new ManejadorEstado(modelo));
        registrar(TipoMensaje.GAME_OVER, new ManejadorFinPartida(modelo));
    }

    public void despachar(Mensaje mensaje) {
        Objects.requireNonNull(mensaje, "El mensaje es obligatorio");
        if (mensaje.getTipo() == null) {
            throw new IllegalArgumentException("El tipo del mensaje es obligatorio");
        }
        SwingUtilities.invokeLater(() -> ejecutar(mensaje));
    }

    private void ejecutar(Mensaje mensaje) {
        IManejadorMensaje manejador = manejadores.get(mensaje.getTipo());
        if (manejador == null) {
            manejarErrorNoRegistrado(mensaje.getTipo());
            return;
        }
        try {
            manejador.manejar(mensaje);
        } catch (RuntimeException e) {
            manejarErrorNoRegistrado(mensaje.getTipo(), e);
        }
    }

    private void manejarErrorNoRegistrado(TipoMensaje tipo) {
        manejarErrorNoRegistrado(tipo, null);
    }

    private void manejarErrorNoRegistrado(TipoMensaje tipo, RuntimeException causa) {
        String razon = "No hay un manejador registrado para " + tipo;
        if (causa != null) {
            razon = "No se pudo procesar " + tipo + ": " + causa.getMessage();
        }
        IManejadorMensaje manejadorError = manejadores.get(TipoMensaje.ERROR);
        if (manejadorError != null && tipo != TipoMensaje.ERROR) {
            manejadorError.manejar(FabricaMensajes.error(razon));
            return;
        }
        System.err.println(razon);
    }
}

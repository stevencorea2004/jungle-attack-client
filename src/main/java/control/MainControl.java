package control;

import model.Especie;
import model.ModeloJuego;
import model.Punto;
import model.Seleccion;
import red.ConexionServidor;
import red.DatosAtacar;
import red.DatosCrearPartida;
import red.DatosMover;
import red.FabricaMensajes;
import red.Mensaje;
import red.TipoMensaje;

public final class MainControl {
    private MainControl() {
    }

    public static void main(String[] args) {
        verificarLobby();
        verificarJuego();
        System.out.println("MainControl OK");
    }

    private static void verificarLobby() {
        FakeConexion conexion = new FakeConexion();
        ModeloJuego modelo = new ModeloJuego();
        ControladorLobby lobby = new ControladorLobby(conexion, modelo);

        lobby.crearPartida("Ana");
        if (conexion.ultimo == null || conexion.ultimo.getTipo() != TipoMensaje.CREATE_MATCH) {
            throw new AssertionError("CREATE_MATCH no enviado");
        }

        lobby.unirse("Ana", "A1B2");
        if (conexion.ultimo.getTipo() != TipoMensaje.JOIN_MATCH) {
            throw new AssertionError("JOIN_MATCH no enviado");
        }

        lobby.elegirEspecie(Especie.TUCAN);
        if (conexion.ultimo.getTipo() != TipoMensaje.SELECT_ANIMAL) {
            throw new AssertionError("SELECT_ANIMAL no enviado");
        }
    }

    private static void verificarJuego() {
        FakeConexion conexion = new FakeConexion();
        ModeloJuego modelo = new ModeloJuego();
        Seleccion seleccion = new Seleccion();
        ControladorJuego juego = new ControladorJuego(conexion, modelo, seleccion);

        seleccion.seleccionar(1);
        seleccion.seleccionar(2);
        juego.moverSeleccionados(new Punto(15, 25));
        if (conexion.ultimo.getTipo() != TipoMensaje.MOVE_ANIMALS) {
            throw new AssertionError("MOVE_ANIMALS no enviado");
        }
        DatosMover mover = (DatosMover) conexion.ultimo.getPayload(DatosMover.class);
        if (mover.idsAnimales.size() != 2 || mover.destinoX != 15.0 || mover.destinoY != 25.0) {
            throw new AssertionError("Payload de move incorrecto");
        }

        juego.atacar(9);
        if (conexion.ultimo.getTipo() != TipoMensaje.ATTACK) {
            throw new AssertionError("ATTACK no enviado");
        }
        DatosAtacar ataque = (DatosAtacar) conexion.ultimo.getPayload(DatosAtacar.class);
        if (ataque.idObjetivo != 9) {
            throw new AssertionError("Objetivo de ataque incorrecto");
        }

        juego.verZona(4);
        if (conexion.ultimo.getTipo() != TipoMensaje.VIEW_ZONE) {
            throw new AssertionError("VIEW_ZONE no enviado");
        }
    }

    private static final class FakeConexion extends ConexionServidor {
        private Mensaje ultimo;

        @Override
        public void enviar(Mensaje mensaje) {
            this.ultimo = mensaje;
        }
    }
}

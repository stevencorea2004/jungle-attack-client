package control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import model.Especie;
import model.Punto;
import model.Seleccion;
import org.junit.jupiter.api.Test;
import red.ConexionServidor;
import red.DatosAtacar;
import red.DatosCrearPartida;
import red.DatosElegirAnimal;
import red.DatosMover;
import red.DatosUnirse;
import red.DatosVerZona;
import red.Mensaje;
import red.TipoMensaje;

class ControladoresTest {
    @Test
    void lobbySendsCreateJoinAndSpeciesSelectionMessages() {
        FakeConexion conexion = new FakeConexion();
        ControladorLobby controlador = new ControladorLobby(conexion);

        controlador.crearPartida("Ana");
        assertEquals(TipoMensaje.CREATE_MATCH, conexion.ultimo.getTipo());
        assertEquals("Ana", ((DatosCrearPartida) conexion.ultimo.getPayload(DatosCrearPartida.class)).nickname);

        controlador.unirse("Luis", "A7K2");
        assertEquals(TipoMensaje.JOIN_MATCH, conexion.ultimo.getTipo());
        DatosUnirse join = (DatosUnirse) conexion.ultimo.getPayload(DatosUnirse.class);
        assertEquals("Luis", join.nickname);
        assertEquals("A7K2", join.codigo);

        controlador.elegirEspecie(Especie.TUCAN);
        assertEquals(TipoMensaje.SELECT_ANIMAL, conexion.ultimo.getTipo());
        assertEquals(Especie.TUCAN,
                ((DatosElegirAnimal) conexion.ultimo.getPayload(DatosElegirAnimal.class)).especie);
    }

    @Test
    void gameControllerSendsMovementAttackAndZoneMessages() {
        FakeConexion conexion = new FakeConexion();
        Seleccion seleccion = new Seleccion();
        seleccion.seleccionar(2);
        seleccion.seleccionar(7);
        ControladorJuego controlador = new ControladorJuego(conexion, seleccion);

        controlador.moverSeleccionados(new Punto(15, 25));
        assertEquals(TipoMensaje.MOVE_ANIMALS, conexion.ultimo.getTipo());
        DatosMover move = (DatosMover) conexion.ultimo.getPayload(DatosMover.class);
        assertEquals(2, move.idsAnimales.size());
        assertEquals(15.0, move.destinoX);
        assertEquals(25.0, move.destinoY);

        controlador.atacar(99);
        assertEquals(TipoMensaje.ATTACK, conexion.ultimo.getTipo());
        DatosAtacar attack = (DatosAtacar) conexion.ultimo.getPayload(DatosAtacar.class);
        assertEquals(99, attack.idObjetivo);
        assertEquals(2, attack.idsAnimales.size());

        controlador.verZona(5);
        assertEquals(TipoMensaje.VIEW_ZONE, conexion.ultimo.getTipo());
        assertEquals(5, ((DatosVerZona) conexion.ultimo.getPayload(DatosVerZona.class)).idZona);
        controlador.salirPartida();
        assertEquals(TipoMensaje.LEAVE_MATCH, conexion.ultimo.getTipo());
        assertNotNull(conexion.ultimo);
    }

    private static final class FakeConexion extends ConexionServidor {
        private Mensaje ultimo;

        @Override
        public void enviar(Mensaje mensaje) {
            ultimo = mensaje;
        }
    }
}

package control;

import model.Especie;
import red.ConexionServidor;
import red.DatosCrearPartida;
import red.DatosElegirAnimal;
import red.DatosUnirse;
import red.FabricaMensajes;
import red.TipoMensaje;

public class ControladorLobby {
    private final ConexionServidor conexion;

    public ControladorLobby(ConexionServidor conexion) {
        this.conexion = conexion;
    }

    public void crearPartida(String nickname) {
        if (conexion == null || nickname == null || nickname.isBlank()) {
            return;
        }
        DatosCrearPartida payload = new DatosCrearPartida();
        payload.nickname = nickname;
        conexion.enviar(FabricaMensajes.crear(TipoMensaje.CREATE_MATCH, payload));
    }

    public void unirse(String nickname, String codigo) {
        if (conexion == null || nickname == null || nickname.isBlank() || codigo == null) {
            return;
        }
        DatosUnirse payload = new DatosUnirse();
        payload.nickname = nickname;
        payload.codigo = codigo;
        conexion.enviar(FabricaMensajes.crear(TipoMensaje.JOIN_MATCH, payload));
    }

    public void elegirEspecie(Especie especie) {
        if (conexion == null || especie == null) {
            return;
        }
        DatosElegirAnimal payload = new DatosElegirAnimal();
        payload.especie = especie;
        conexion.enviar(FabricaMensajes.crear(TipoMensaje.SELECT_ANIMAL, payload));
    }
}

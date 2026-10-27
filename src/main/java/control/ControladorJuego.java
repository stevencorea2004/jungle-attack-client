package control;

import java.util.ArrayList;
import model.Punto;
import model.Seleccion;
import red.ConexionServidor;
import red.DatosAtacar;
import red.DatosMover;
import red.DatosVerZona;
import red.FabricaMensajes;
import red.TipoMensaje;

public class ControladorJuego {
    private final ConexionServidor conexion;
    private final Seleccion seleccion;

    public ControladorJuego(ConexionServidor conexion, Seleccion seleccion) {
        this.conexion = conexion;
        this.seleccion = seleccion;
    }

    public void moverSeleccionados(Punto destino) {
        if (conexion == null || destino == null || seleccion == null) {
            return;
        }
        DatosMover payload = new DatosMover();
        payload.idsAnimales = new ArrayList<>(seleccion.getSeleccionados());
        payload.destinoX = destino.getX();
        payload.destinoY = destino.getY();
        conexion.enviar(FabricaMensajes.crear(TipoMensaje.MOVE_ANIMALS, payload));
    }

    public void atacar(int idObjetivo) {
        if (conexion == null || seleccion == null) {
            return;
        }
        DatosAtacar payload = new DatosAtacar();
        payload.idsAnimales = new ArrayList<>(seleccion.getSeleccionados());
        payload.idObjetivo = idObjetivo;
        conexion.enviar(FabricaMensajes.crear(TipoMensaje.ATTACK, payload));
    }

    public void verZona(int idZona) {
        if (conexion == null) {
            return;
        }
        DatosVerZona payload = new DatosVerZona();
        payload.idZona = idZona;
        conexion.enviar(FabricaMensajes.crear(TipoMensaje.VIEW_ZONE, payload));
    }
}

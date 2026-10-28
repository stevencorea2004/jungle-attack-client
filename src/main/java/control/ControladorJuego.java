package control;

import java.util.Vector;
import model.ModeloJuego;
import model.Punto;
import model.Seleccion;
import red.ConexionServidor;
import red.DatosAtacar;
import red.DatosMover;
import red.DatosVerZona;
import red.FabricaMensajes;
import red.TipoMensaje;

public class ControladorJuego {
    private ConexionServidor conexion;
    private ModeloJuego modelo;
    private Seleccion seleccion;

    public ControladorJuego(ConexionServidor conexion, ModeloJuego modelo, Seleccion seleccion) {
        this.conexion = conexion;
        this.modelo = modelo;
        this.seleccion = seleccion;
    }

    public void moverSeleccionados(Punto destino) {
        if (conexion == null || destino == null || seleccion == null) {
            return;
        }
        DatosMover payload = new DatosMover();
        payload.idsAnimales = new Vector<>(seleccion.getSeleccionados());
        payload.destinoX = destino.getX();
        payload.destinoY = destino.getY();
        conexion.enviar(FabricaMensajes.crear(TipoMensaje.MOVE_ANIMALS, payload));
    }

    public void atacar(int idObjetivo) {
        if (conexion == null || seleccion == null) {
            return;
        }
        DatosAtacar payload = new DatosAtacar();
        payload.idsAnimales = new Vector<>(seleccion.getSeleccionados());
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

    public void salirPartida() {
        if (conexion != null) {
            conexion.enviar(FabricaMensajes.crear(TipoMensaje.LEAVE_MATCH, null));
        }
    }
}

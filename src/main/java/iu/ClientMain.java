package iu;

import control.ControladorJuego;
import control.ControladorLobby;
import javax.swing.SwingUtilities;
import java.util.Vector;
import java.io.UncheckedIOException;
import model.ModeloJuego;
import model.Seleccion;
import red.ConexionServidor;
import util.Constantes;

public final class ClientMain {
    private ClientMain() {
    }

    public static void main(String[] args) {
        Vector<String> imagenes = new Vector<>();
        for (model.Especie especie : model.Especie.values()) {
            imagenes.add(especie.getRutaSvg());
        }
        imagenes.add("svg/taza_cafe.svg");
        imagenes.add("svg/bandera_azul.svg");
        imagenes.add("svg/bandera_roja.svg");
        imagenes.add("svg/niebla.svg");
        CacheSvg.getInstancia().precargar(imagenes);
        SwingUtilities.invokeLater(() -> {
            ModeloJuego modelo = new ModeloJuego();
            Seleccion seleccion = new Seleccion();
            ConexionServidor conexion = new ConexionServidor(modelo);
            try {
                conexion.conectar(Constantes.HOST, Constantes.PUERTO);
            } catch (UncheckedIOException e) {
                DialogoError.mostrar(null,
                        "No se pudo conectar al servidor en " + Constantes.HOST + ":"
                                + Constantes.PUERTO + ". Inicia el servidor y vuelve a abrir el cliente.\n"
                                + e.getMessage());
                return;
            }
            ControladorLobby lobby = new ControladorLobby(conexion, modelo);
            ControladorJuego juego = new ControladorJuego(conexion, modelo, seleccion);
            VentanaJuego ventana = new VentanaJuego(modelo, lobby, juego, seleccion);
            ventana.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    conexion.desconectar();
                }
            });
            ventana.setVisible(true);
        });
    }
}

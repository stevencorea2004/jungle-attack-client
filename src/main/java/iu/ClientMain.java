package iu;

import control.ControladorJuego;
import control.ControladorLobby;
import javax.swing.SwingUtilities;
import java.util.Vector;
import model.ModeloJuego;
import model.Seleccion;

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
        CacheSvg.getInstancia().precargar(imagenes);
        SwingUtilities.invokeLater(() -> {
            ModeloJuego modelo = new ModeloJuego();
            Seleccion seleccion = new Seleccion();
            ControladorLobby lobby = new ControladorLobby(null, modelo);
            ControladorJuego juego = new ControladorJuego(null, modelo, seleccion);
            VentanaJuego ventana = new VentanaJuego(modelo, lobby, juego, seleccion);
            ventana.setVisible(true);
        });
    }
}

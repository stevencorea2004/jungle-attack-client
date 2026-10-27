package util;

import java.awt.Color;

public final class Constantes {
    public static final String HOST = "localhost";
    public static final int PUERTO = 5050;
    public static final String CHARSET = "UTF-8";
    public static final int ENERGIA_MAX_JUGADOR = 20;
    public static final int ENERGIA_BOT = 10;
    public static final int CANTIDAD_ESPECIES = 7;
    public static final int LARGO_CODIGO = 4;
    public static final int ANCHO_VENTANA = 1280;
    public static final int ALTO_VENTANA = 800;
    public static final int FPS = 30;
    public static final int ALTO_BARRA_ENERGIA = 6;
    public static final Color COLOR_SELECCION = new Color(255, 211, 92);
    public static final String RUTA_SVG_ANIMALES = "svg/";
    public static final String RUTA_SVG_TAZA = "svg/taza_cafe.svg";
    public static final String RUTA_SVG_BANDERA = "svg/bandera_azul.svg";

    private Constantes() {
        throw new AssertionError("Clase de constantes");
    }
}

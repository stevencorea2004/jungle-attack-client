package util;

import java.awt.Color;

public final class Constantes {
    public static final String HOST = "";
    public static final int PUERTO = 0;
    public static final String CHARSET = "UTF-8";
    public static final int ENERGIA_MAX_JUGADOR = 20;
    public static final int ENERGIA_BOT = 10;
    public static final int CANTIDAD_ESPECIES = 7;
    public static final int LARGO_CODIGO = 4;
    public static final int ANCHO_VENTANA = 0;
    public static final int ALTO_VENTANA = 0;
    public static final int FPS = 30;
    public static final int ALTO_BARRA_ENERGIA = 0;
    public static final Color COLOR_SELECCION = null;
    public static final String RUTA_SVG_ANIMALES = "";
    public static final String RUTA_SVG_TAZA = "";
    public static final String RUTA_SVG_BANDERA = "";

    private Constantes() {
        throw new AssertionError("Clase de constantes");
    }
}

package iu;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Vector;

public final class CacheSvg {
    private static final CacheSvg INSTANCIA = new CacheSvg();
    private Map<String, BufferedImage> imagenes;

    private CacheSvg() {
    }

    public static CacheSvg getInstancia() {
        return INSTANCIA;
    }

    public void precargar(Vector<String> rutas) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }

    public BufferedImage getImagen(String ruta) {
        throw new UnsupportedOperationException("Pendiente de implementación");
    }
}

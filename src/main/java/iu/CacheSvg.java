package iu;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

public final class CacheSvg {
    private static final CacheSvg INSTANCIA = new CacheSvg();
    private final Map<String, BufferedImage> imagenes = new HashMap<>();

    private CacheSvg() {
    }

    public static CacheSvg getInstancia() {
        return INSTANCIA;
    }

    public synchronized void precargar(List<String> rutas) {
        if (rutas == null) {
            return;
        }
        for (String ruta : rutas) {
            getImagen(ruta);
        }
    }

    public synchronized BufferedImage getImagen(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            return null;
        }
        if (imagenes.containsKey(ruta)) {
            return imagenes.get(ruta);
        }
        String recurso = ruta.startsWith("/") ? ruta.substring(1) : ruta;
        if (!recurso.startsWith("svg/")) {
            recurso = "svg/" + recurso;
        }
        try (InputStream entrada = CacheSvg.class.getClassLoader().getResourceAsStream(recurso)) {
            if (entrada == null) {
                throw new IllegalArgumentException("No se encontro el recurso SVG: " + recurso);
            }
            BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
            transcoder.transcode(new TranscoderInput(entrada), null);
            BufferedImage imagen = transcoder.getImagen();
            if (imagen == null) {
                throw new IllegalStateException("No se pudo rasterizar el recurso SVG: " + recurso);
            }
            imagenes.put(ruta, imagen);
            return imagen;
        } catch (IOException | TranscoderException e) {
            throw new IllegalStateException("Error al cargar el recurso SVG: " + recurso, e);
        }
    }

    private static final class BufferedImageTranscoder extends ImageTranscoder {
        private BufferedImage imagen;

        @Override
        public BufferedImage createImage(int width, int height) {
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }

        @Override
        public void writeImage(BufferedImage image, TranscoderOutput output) {
            this.imagen = image;
        }

        private BufferedImage getImagen() {
            return imagen;
        }
    }
}

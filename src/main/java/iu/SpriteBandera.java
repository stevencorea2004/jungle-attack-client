package iu;

import java.awt.Graphics2D;
import model.Punto;

public class SpriteBandera extends Sprite {
    private final Punto posicion;
    private final String color;

    public SpriteBandera(Punto posicion, String color) {
        this.posicion = posicion;
        this.color = color;
        this.cache = CacheSvg.getInstancia();
    }

    @Override
    public void dibujar(Graphics2D g2, long ahora) {
        if (posicion == null) {
            return;
        }
        String ruta = color != null && color.equalsIgnoreCase("roja")
                ? "svg/bandera_roja.svg" : "svg/bandera_azul.svg";
        java.awt.image.BufferedImage imagen = cache.getImagen(ruta);
        int x = (int) posicion.getX();
        int y = (int) posicion.getY();
        if (imagen != null) {
            g2.drawImage(imagen, x - 20, y - 30, 40, 40, null);
        } else {
            g2.setColor(color != null && color.equalsIgnoreCase("roja")
                    ? new java.awt.Color(194, 72, 54) : new java.awt.Color(63, 111, 187));
            g2.fillRect(x - 2, y - 28, 4, 42);
            g2.fillOval(x - 17, y - 28, 24, 18);
        }
    }
}

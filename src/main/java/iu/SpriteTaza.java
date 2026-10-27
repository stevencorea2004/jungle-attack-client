package iu;

import java.awt.Graphics2D;
import model.Punto;

public class SpriteTaza extends Sprite {
    private final Punto posicion;

    public SpriteTaza(Punto posicion) {
        this.posicion = posicion;
        this.cache = CacheSvg.getInstancia();
    }

    @Override
    public void dibujar(Graphics2D g2, long ahora) {
        if (posicion == null) {
            return;
        }
        java.awt.image.BufferedImage imagen = cache.getImagen("svg/taza_cafe.svg");
        int x = (int) posicion.getX();
        int y = (int) posicion.getY();
        if (imagen != null) {
            g2.drawImage(imagen, x - 15, y - 15, 30, 30, null);
        } else {
            g2.setColor(new java.awt.Color(234, 194, 96));
            g2.fillOval(x - 11, y - 11, 22, 22);
            g2.setColor(new java.awt.Color(75, 47, 28));
            g2.drawString("c", x - 3, y + 5);
        }
    }
}

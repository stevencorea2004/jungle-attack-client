package iu;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class SpriteZona extends Sprite {
    private final Rectangle2D limites;
    private final String nombre;

    public SpriteZona(Rectangle2D limites, String nombre) {
        this.limites = limites;
        this.nombre = nombre;
    }

    @Override
    public void dibujar(Graphics2D g2, long ahora) {
        if (limites == null) {
            return;
        }
        g2.setColor(new java.awt.Color(255, 255, 255, 24));
        g2.fill(limites);
        g2.setColor(new java.awt.Color(236, 230, 198, 80));
        g2.draw(limites);
        if (nombre != null) {
            g2.setColor(new java.awt.Color(250, 246, 219, 140));
            g2.drawString(nombre, (float) limites.getX() + 10, (float) limites.getY() + 20);
        }
    }
}

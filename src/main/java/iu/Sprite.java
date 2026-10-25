package iu;

import java.awt.Graphics2D;

public abstract class Sprite implements IDibujable {
    protected CacheSvg cache;

    @Override
    public abstract void dibujar(Graphics2D g2, long ahora);
}

package iu;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import model.AnimalCliente;
import model.Punto;
import model.Seleccion;
import util.Constantes;

public class SpriteAnimal extends Sprite {
    private final AnimalCliente animal;
    private final Seleccion seleccion;

    public SpriteAnimal(AnimalCliente animal, Seleccion seleccion) {
        this.animal = animal;
        this.seleccion = seleccion;
        this.cache = CacheSvg.getInstancia();
    }

    @Override
    public void dibujar(Graphics2D g2, long ahora) {
        if (animal == null) {
            return;
        }
        Punto posicion = animal.posInterpolada(ahora);
        int x = (int) Math.round(posicion.getX());
        int y = (int) Math.round(posicion.getY());
        boolean marcada = seleccion != null && seleccion.estaSeleccionado(animal.getId());
        if (marcada) {
            g2.setColor(Constantes.COLOR_SELECCION);
            g2.fillOval(x - 24, y - 24, 48, 48);
        }

        BufferedImage imagen = animal.getEspecie() == null ? null : cache.getImagen(animal.getEspecie().getRutaSvg());
        if (imagen != null) {
            Object antialiasAnterior = g2.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(imagen, x - 18, y - 18, 36, 36, null);
            if (antialiasAnterior != null) {
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, antialiasAnterior);
            }
        } else {
            g2.setColor(animal.isEsMio() ? new java.awt.Color(51, 122, 77) : new java.awt.Color(168, 68, 54));
            g2.fillOval(x - 15, y - 15, 30, 30);
        }

        int energia = Math.max(0, Math.min(Constantes.ENERGIA_MAX_JUGADOR, animal.getEnergia()));
        g2.setColor(new java.awt.Color(20, 35, 25, 190));
        g2.fillRoundRect(x - 20, y + 20, 40, Constantes.ALTO_BARRA_ENERGIA, 4, 4);
        g2.setColor(energia > 5 ? new java.awt.Color(106, 191, 93) : new java.awt.Color(221, 80, 62));
        g2.fillRoundRect(x - 20, y + 20, energia * 2, Constantes.ALTO_BARRA_ENERGIA, 4, 4);
        g2.setFont(g2.getFont().deriveFont(java.awt.Font.BOLD, 10f));
        g2.setColor(java.awt.Color.WHITE);
        String nombre = animal.getEspecie() == null ? "Animal" : animal.getEspecie().getNombre();
        g2.drawString(animal.isEsMio() ? "Tu " + nombre : nombre, x - 30, y + 39);
    }
}

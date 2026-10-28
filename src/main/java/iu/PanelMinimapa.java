package iu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import model.ModeloJuego;
import red.VistaCamino;
import red.VistaMapa;
import red.VistaZona;

final class PanelMinimapa extends JPanel {
    private static final Color FONDO = new Color(15, 30, 25, 225);
    private static final Color ZONA_EXPLORADA = new Color(64, 117, 70);
    private static final Color ZONA_NUBLADA = new Color(95, 98, 97);
    private final ModeloJuego modelo;

    PanelMinimapa(ModeloJuego modelo) {
        this.modelo = modelo;
        setName("minimapa");
        setOpaque(false);
        setToolTipText("Minimapa: animales propios por región");
        setPreferredSize(new java.awt.Dimension(220, 150));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(FONDO);
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
        g.setColor(new Color(225, 214, 174, 210));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 14, 14);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 10f));
        g.drawString("MINIMAPA", 9, 14);

        VistaMapa mapa = modelo == null ? null : modelo.getMapa();
        if (mapa == null || mapa.zonas == null || mapa.zonas.isEmpty()) {
            g.dispose();
            return;
        }

        Rectangle2D limites = limitesMapa(mapa);
        if (limites == null) {
            g.dispose();
            return;
        }
        int margen = 8;
        double x = margen;
        double y = 20;
        double ancho = Math.max(1, getWidth() - margen * 2);
        double alto = Math.max(1, getHeight() - y - margen);
        Map<Integer, Rectangle2D.Double> rectangulos = new HashMap<>();
        for (VistaZona zona : mapa.zonas) {
            if (zona == null || zona.ancho <= 0.0 || zona.alto <= 0.0) {
                continue;
            }
            double zonaX = x + (zona.x - limites.getX()) * ancho / limites.getWidth();
            double zonaY = y + (zona.y - limites.getY()) * alto / limites.getHeight();
            double zonaAncho = zona.ancho * ancho / limites.getWidth();
            double zonaAlto = zona.alto * alto / limites.getHeight();
            rectangulos.put(zona.id, new Rectangle2D.Double(zonaX, zonaY, zonaAncho, zonaAlto));
        }

        dibujarConexiones(g, mapa, rectangulos);
        BufferedImage iconoNiebla = CacheSvg.getInstancia().getImagen("svg/niebla.svg");
        for (VistaZona zona : mapa.zonas) {
            Rectangle2D.Double rectangulo = zona == null ? null : rectangulos.get(zona.id);
            if (rectangulo == null) {
                continue;
            }
            boolean explorada = modelo.getZonasVisibles() != null
                    && modelo.getZonasVisibles().contains(zona.id);
            g.setColor(explorada ? ZONA_EXPLORADA : ZONA_NUBLADA);
            g.fill(rectangulo);
            g.setColor(new Color(15, 34, 28, 230));
            g.setStroke(new BasicStroke(1f));
            g.draw(rectangulo);
            if (explorada) {
                dibujarCantidad(g, zona.id, rectangulo);
            } else {
                int lado = Math.max(8, (int) Math.min(rectangulo.width, rectangulo.height) - 5);
                int iconoX = (int) Math.round(rectangulo.getCenterX() - lado / 2.0);
                int iconoY = (int) Math.round(rectangulo.getCenterY() - lado / 2.0);
                g.drawImage(iconoNiebla, iconoX, iconoY, lado, lado, null);
            }
            if (zona.id == modelo.getZonaActual()) {
                g.setColor(TemaUI.ORO);
                g.setStroke(new BasicStroke(2f));
                g.draw(rectangulo);
            }
        }
        g.dispose();
    }

    private void dibujarCantidad(Graphics2D g, int idZona, Rectangle2D.Double rectangulo) {
        int cantidad = modelo.getAnimalesPorZona().getOrDefault(idZona, 0);
        float fuente = (float) Math.max(8, Math.min(20, rectangulo.height * 0.57));
        g.setFont(g.getFont().deriveFont(Font.BOLD, fuente));
        g.setColor(new Color(255, 255, 255, 235));
        String texto = Integer.toString(cantidad);
        java.awt.FontMetrics metricas = g.getFontMetrics();
        float textoX = (float) (rectangulo.getCenterX() - metricas.stringWidth(texto) / 2.0);
        float textoY = (float) (rectangulo.getCenterY()
                + (metricas.getAscent() - metricas.getDescent()) / 2.0);
        g.drawString(texto, textoX, textoY);
    }

    private static void dibujarConexiones(Graphics2D g, VistaMapa mapa,
            Map<Integer, Rectangle2D.Double> rectangulos) {
        if (mapa.caminos == null) {
            return;
        }
        g.setColor(new Color(207, 190, 143, 180));
        g.setStroke(new BasicStroke(2f));
        for (VistaCamino camino : mapa.caminos) {
            if (camino == null) {
                continue;
            }
            Rectangle2D.Double origen = rectangulos.get(camino.zonaA);
            Rectangle2D.Double destino = rectangulos.get(camino.zonaB);
            if (origen != null && destino != null) {
                g.drawLine((int) origen.getCenterX(), (int) origen.getCenterY(),
                        (int) destino.getCenterX(), (int) destino.getCenterY());
            }
        }
    }

    private static Rectangle2D limitesMapa(VistaMapa mapa) {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (VistaZona zona : mapa.zonas) {
            if (zona == null || !Double.isFinite(zona.x) || !Double.isFinite(zona.y)
                    || !Double.isFinite(zona.ancho) || !Double.isFinite(zona.alto)
                    || zona.ancho <= 0.0 || zona.alto <= 0.0) {
                continue;
            }
            minX = Math.min(minX, zona.x);
            minY = Math.min(minY, zona.y);
            maxX = Math.max(maxX, zona.x + zona.ancho);
            maxY = Math.max(maxY, zona.y + zona.alto);
        }
        if (!Double.isFinite(minX) || maxX <= minX || maxY <= minY) {
            return null;
        }
        return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
    }
}

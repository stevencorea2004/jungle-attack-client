package iu;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import javax.swing.JPanel;
import model.AnimalCliente;
import model.ModeloJuego;
import model.Seleccion;

public class PanelMarcador extends JPanel {
    private final ModeloJuego modelo;
    private final Seleccion seleccion;

    public PanelMarcador(ModeloJuego modelo, Seleccion seleccion) {
        this.modelo = modelo;
        this.seleccion = seleccion;
        setOpaque(true);
        setBackground(TemaUI.VERDE_NOCHE);
        setPreferredSize(new java.awt.Dimension(250, 400));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        g2.setPaint(new GradientPaint(0, 0, new Color(27, 65, 47), 0, getHeight(), TemaUI.VERDE_NOCHE));
        g2.fillRoundRect(12, 12, w - 24, getHeight() - 24, 22, 22);
        g2.setColor(new Color(222, 185, 104));
        g2.setStroke(new java.awt.BasicStroke(1.5f));
        g2.drawRoundRect(12, 12, w - 24, getHeight() - 24, 22, 22);

        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        g2.setColor(TemaUI.CREMA);
        g2.drawString("ESTADO", 30, 48);
        g2.setColor(TemaUI.ORO);
        g2.drawLine(30, 60, w - 30, 60);

        int vidas = 0;
        if (modelo != null) {
            for (AnimalCliente animal : modelo.getMisAnimales()) {
                if (animal.getEnergia() > 0) {
                    vidas++;
                }
            }
        }
        int escogidos = seleccion == null ? 0 : seleccion.getSeleccionados().size();
        int zona = modelo == null ? 0 : modelo.getZonaActual();
        int danio = modelo == null ? 0 : modelo.getDanio();
        int tazas = modelo == null ? 0 : modelo.getTazasConsumidas();
        int bots = modelo == null ? 0 : modelo.getBotsEliminados();
        String[] etiquetas = {"Zona explorada", "Animales vivos", "Seleccionados", "Danio por ataque", "Cafes tomados", "Bots eliminados"};
        String[] valores = {zona == 0 ? "--" : String.valueOf(zona), vidas + " / 10", String.valueOf(escogidos),
            String.valueOf(danio), String.valueOf(tazas), String.valueOf(bots)};
        int y = 100;
        for (int i = 0; i < etiquetas.length; i++) {
            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            g2.setColor(new Color(208, 220, 197));
            g2.drawString(etiquetas[i], 30, y);
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 21));
            g2.setColor(TemaUI.CREMA);
            g2.drawString(valores[i], 30, y + 27);
            y += 69;
        }
        g2.setColor(new Color(218, 204, 165));
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        g2.drawString("Selecciona unidades con clic o arrastre.", 25, getHeight() - 52);
        g2.drawString("Clic derecho: caminar por el sendero.", 25, getHeight() - 34);
        g2.dispose();
    }
}

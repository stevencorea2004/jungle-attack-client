package iu;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.JPanel;
import model.AnimalCliente;
import model.ModeloJuego;
import model.Seleccion;

public class PanelMarcador extends JPanel {
    private static final int COLUMNA_ICONOS = 30;
    private static final int INICIO_RECUADRO = 358;
    private static final int TAMANO_ICONO = 36;
    private static final int ESPACIO_ICONO = 44;
    private static final int COLUMNAS = 4;
    private final ModeloJuego modelo;
    private final Seleccion seleccion;
    private final Runnable repintarAlCambiarSeleccion = this::repaint;

    public PanelMarcador(ModeloJuego modelo, Seleccion seleccion) {
        this.modelo = modelo;
        this.seleccion = seleccion;
        setOpaque(true);
        setBackground(TemaUI.VERDE_NOCHE);
        setPreferredSize(new java.awt.Dimension(250, 400));
        if (seleccion != null) {
            seleccion.addChangeListener(repintarAlCambiarSeleccion);
        }
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    seleccionarIcono(e.getPoint(), e.isShiftDown());
                }
            }
        });
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

        int vidas = modelo == null ? 0 : modelo.getAnimalesVivos();
        int zona = modelo == null ? 0 : modelo.getZonaActual();
        int danio = modelo == null ? 0 : modelo.getDanio();
        int tazas = modelo == null ? 0 : modelo.getTazasConsumidas();
        int bots = modelo == null ? 0 : modelo.getBotsEliminados();
        String[] etiquetas = {"Zona explorada", "Animales vivos", "Danio por ataque", "Cafes tomados", "Bots eliminados"};
        String[] valores = {zona == 0 ? "--" : String.valueOf(zona), vidas + " / 10",
            String.valueOf(danio), String.valueOf(tazas), String.valueOf(bots)};
        int y = 100;
        for (int i = 0; i < etiquetas.length; i++) {
            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            g2.setColor(new Color(208, 220, 197));
            g2.drawString(etiquetas[i], 30, y);
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 21));
            g2.setColor(TemaUI.CREMA);
            g2.drawString(valores[i], 30, y + 27);
            y += 51;
        }

        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        g2.setColor(TemaUI.CREMA);
        g2.drawString("UNIDADES SELECCIONADAS", 30, INICIO_RECUADRO - 10);
        Rectangle recuadro = recuadroIconos();
        g2.setColor(new Color(14, 38, 28, 190));
        g2.fillRoundRect(recuadro.x, recuadro.y, recuadro.width, recuadro.height, 10, 10);
        g2.setColor(new Color(222, 185, 104));
        g2.drawRoundRect(recuadro.x, recuadro.y, recuadro.width, recuadro.height, 10, 10);
        dibujarIconosSeleccionados(g2, animalesSeleccionados(), recuadro);

        g2.setColor(new Color(218, 204, 165));
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        g2.drawString("Clic: seleccionar | Shift: quitar.", 25, getHeight() - 49);
        g2.drawString("Clic derecho: caminar.", 25, getHeight() - 31);
        g2.dispose();
    }

    private void dibujarIconosSeleccionados(Graphics2D g2, List<AnimalCliente> animales,
            Rectangle recuadro) {
        if (animales.isEmpty()) {
            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
            g2.setColor(new Color(208, 220, 197));
            g2.drawString("Sin unidades seleccionadas", recuadro.x + 10, recuadro.y + 23);
            return;
        }
        for (int i = 0; i < animales.size(); i++) {
            AnimalCliente animal = animales.get(i);
            int x = COLUMNA_ICONOS + (i % COLUMNAS) * ESPACIO_ICONO;
            int y = recuadro.y + 7 + (i / COLUMNAS) * ESPACIO_ICONO;
            if (y + TAMANO_ICONO > recuadro.y + recuadro.height - 4) {
                break;
            }
            g2.setColor(new Color(225, 190, 96));
            g2.fillRoundRect(x, y, TAMANO_ICONO, TAMANO_ICONO, 7, 7);
            g2.setColor(new Color(25, 49, 35));
            g2.fillRoundRect(x + 2, y + 2, TAMANO_ICONO - 4, TAMANO_ICONO - 4, 5, 5);
            BufferedImage imagen = animal.getEspecie() == null ? null
                    : CacheSvg.getInstancia().getImagen(animal.getEspecie().getRutaSvg());
            if (imagen != null) {
                g2.drawImage(imagen, x + 4, y + 4, TAMANO_ICONO - 8, TAMANO_ICONO - 8, null);
            }
        }
    }

    private void seleccionarIcono(java.awt.Point punto, boolean shift) {
        if (seleccion == null) {
            return;
        }
        List<AnimalCliente> animales = animalesSeleccionados();
        for (int i = 0; i < animales.size(); i++) {
            Rectangle icono = limitesIcono(i);
            if (icono.contains(punto)) {
                int id = animales.get(i).getId();
                if (shift) {
                    seleccion.deseleccionar(id);
                } else {
                    seleccion.limpiar();
                    seleccion.seleccionar(id);
                }
                return;
            }
        }
    }

    private List<AnimalCliente> animalesSeleccionados() {
        List<AnimalCliente> animales = new ArrayList<>();
        if (modelo == null || seleccion == null) {
            return animales;
        }
        for (AnimalCliente animal : modelo.getMisAnimales()) {
            if (animal.getEnergia() > 0 && seleccion.estaSeleccionado(animal.getId())) {
                animales.add(animal);
            }
        }
        animales.sort(Comparator.comparingInt(AnimalCliente::getId));
        return animales;
    }

    private Rectangle limitesIcono(int indice) {
        Rectangle recuadro = recuadroIconos();
        return new Rectangle(COLUMNA_ICONOS + (indice % COLUMNAS) * ESPACIO_ICONO,
                recuadro.y + 7 + (indice / COLUMNAS) * ESPACIO_ICONO,
                TAMANO_ICONO, TAMANO_ICONO);
    }

    private Rectangle recuadroIconos() {
        int y = INICIO_RECUADRO;
        int abajo = Math.max(y + 1, getHeight() - 72);
        return new Rectangle(24, y, Math.max(1, getWidth() - 48), abajo - y);
    }
}

package iu;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

final class TemaUI {
    static final Color VERDE_NOCHE = new Color(15, 43, 34);
    static final Color VERDE_HOJA = new Color(34, 82, 57);
    static final Color VERDE_CLARO = new Color(157, 188, 111);
    static final Color ORO = new Color(225, 183, 95);
    static final Color CREMA = new Color(246, 241, 220);
    static final Color TEXTO = new Color(34, 48, 36);
    static final Color PANEL = new Color(250, 247, 233);

    private TemaUI() {
    }

    static JPanel fondo() {
        return new JPanel() {
            {
                setOpaque(false);
                setLayout(new java.awt.GridBagLayout());
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, VERDE_NOCHE, getWidth(), getHeight(), VERDE_HOJA));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 12));
                for (int i = 0; i < getWidth(); i += 70) {
                    g2.drawArc(i, -40, 120, 130, 190, 150);
                    g2.drawArc(i - 30, getHeight() - 100, 150, 150, 10, 150);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }

    static JButton boton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(boton.getFont().deriveFont(java.awt.Font.BOLD, 15f));
        boton.setForeground(VERDE_NOCHE);
        boton.setBackground(ORO);
        boton.setFocusPainted(false);
        boton.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(245, 221, 166), 1),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)));
        return boton;
    }

    static JLabel etiqueta(String texto, int estilo, float tamano, Color color) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(estilo, tamano));
        label.setForeground(color);
        return label;
    }

    static JPanel tarjeta() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(213, 199, 158), 1),
                BorderFactory.createEmptyBorder(28, 34, 28, 34)));
        return panel;
    }
}

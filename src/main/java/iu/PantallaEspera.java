package iu;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.ModeloJuego;

public class PantallaEspera extends Pantalla {
    private JLabel codigo;
    private final Runnable continuar;

    public PantallaEspera(ModeloJuego modelo, Runnable continuar) {
        super(modelo);
        this.continuar = continuar;
        construir();
    }

    @Override
    public void construir() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(TemaUI.VERDE_NOCHE);
        JPanel tarjeta = TemaUI.tarjeta();
        tarjeta.setLayout(new javax.swing.BoxLayout(tarjeta, javax.swing.BoxLayout.Y_AXIS));
        tarjeta.setPreferredSize(new java.awt.Dimension(480, 380));

        JLabel icono = TemaUI.etiqueta("♧", Font.BOLD, 60f, TemaUI.VERDE_HOJA);
        JLabel titulo = TemaUI.etiqueta("Partida creada", Font.BOLD, 30f, TemaUI.VERDE_NOCHE);
        JLabel texto = TemaUI.etiqueta("Comparte este codigo con tu rival", Font.PLAIN, 15f, TemaUI.TEXTO);
        codigo = TemaUI.etiqueta("----", Font.BOLD, 42f, TemaUI.VERDE_HOJA);
        codigo.setFont(new Font(Font.MONOSPACED, Font.BOLD, 42));
        for (JLabel label : new JLabel[] {icono, titulo, texto, codigo}) {
            label.setAlignmentX(CENTER_ALIGNMENT);
            tarjeta.add(label);
            tarjeta.add(javax.swing.Box.createVerticalStrut(12));
        }
        JLabel espera = TemaUI.etiqueta("Esperando que se una el segundo jugador...", Font.PLAIN, 13f, TemaUI.TEXTO);
        espera.setAlignmentX(CENTER_ALIGNMENT);
        tarjeta.add(espera);
        tarjeta.add(javax.swing.Box.createVerticalStrut(22));
        javax.swing.JButton continuar = TemaUI.boton("Continuar a seleccion");
        continuar.setAlignmentX(CENTER_ALIGNMENT);
        continuar.addActionListener(e -> this.continuar.run());
        tarjeta.add(continuar);

        JPanel wrapper = new JPanel(new java.awt.GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(tarjeta);
        add(wrapper, BorderLayout.CENTER);
    }

    @Override
    public void refrescar() {
        if (codigo != null && modelo != null && modelo.getCodigoPartida() != null) {
            codigo.setText(modelo.getCodigoPartida());
        }
    }
}

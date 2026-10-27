package iu;

import control.ControladorLobby;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import model.ModeloJuego;

public class PantallaLobby extends Pantalla {
    private final ControladorLobby controlador;
    private final Consumer<Boolean> continuar;
    private JTextField nickname;
    private JTextField codigo;
    private JLabel mensaje;

    public PantallaLobby(ModeloJuego modelo, ControladorLobby controlador, Consumer<Boolean> continuar) {
        super(modelo);
        this.controlador = controlador;
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
        tarjeta.setPreferredSize(new Dimension(440, 500));
        tarjeta.setMinimumSize(new Dimension(440, 500));
        tarjeta.setMaximumSize(new Dimension(440, 500));

        JLabel logo = TemaUI.etiqueta("JUNGLE", Font.BOLD, 42f, TemaUI.VERDE_NOCHE);
        JLabel subtitulo = TemaUI.etiqueta("A T T A C K", Font.BOLD, 17f, TemaUI.VERDE_HOJA);
        JLabel descripcion = TemaUI.etiqueta("Batalla estrategica en el bosque costarricense", Font.PLAIN, 14f, TemaUI.TEXTO);
        logo.setAlignmentX(CENTER_ALIGNMENT);
        subtitulo.setAlignmentX(CENTER_ALIGNMENT);
        descripcion.setAlignmentX(CENTER_ALIGNMENT);
        tarjeta.add(logo);
        tarjeta.add(subtitulo);
        tarjeta.add(javax.swing.Box.createVerticalStrut(8));
        tarjeta.add(descripcion);
        tarjeta.add(javax.swing.Box.createVerticalStrut(28));

        JLabel nicknameLabel = new JLabel("Tu nombre de jugador");
        nicknameLabel.setForeground(TemaUI.TEXTO);
        nicknameLabel.setHorizontalAlignment(JLabel.CENTER);
        nicknameLabel.setAlignmentX(CENTER_ALIGNMENT);
        nickname = new JTextField();
        nickname.setName("nickname");
        nickname.setMaximumSize(new Dimension(340, 42));
        nickname.setFont(nickname.getFont().deriveFont(16f));
        nickname.setHorizontalAlignment(JTextField.CENTER);
        nickname.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(191, 189, 158)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JButton crear = TemaUI.boton("Crear partida");
        JButton unirse = TemaUI.boton("Unirse con codigo");
        crear.setName("crearPartida");
        unirse.setName("unirsePartida");
        crear.setAlignmentX(CENTER_ALIGNMENT);
        crear.setMaximumSize(new Dimension(260, 44));
        crear.setPreferredSize(new Dimension(260, 44));
        unirse.setAlignmentX(CENTER_ALIGNMENT);
        unirse.setMaximumSize(new Dimension(260, 44));
        unirse.setPreferredSize(new Dimension(260, 44));
        codigo = new JTextField();
        codigo.setName("codigoPartida");
        codigo.setMaximumSize(new Dimension(220, 40));
        codigo.setPreferredSize(new Dimension(220, 40));
        codigo.setHorizontalAlignment(JTextField.CENTER);
        codigo.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
        codigo.setToolTipText("Codigo de cuatro caracteres");
        codigo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(191, 189, 158)),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        mensaje = TemaUI.etiqueta("Ingresa tu nombre para comenzar", Font.PLAIN, 13f, TemaUI.VERDE_HOJA);
        mensaje.setName("mensajeLobby");
        mensaje.setHorizontalAlignment(JLabel.CENTER);
        mensaje.setAlignmentX(CENTER_ALIGNMENT);
        mensaje.setPreferredSize(new Dimension(340, 42));
        mensaje.setMinimumSize(new Dimension(340, 42));
        mensaje.setMaximumSize(new Dimension(340, 42));

        tarjeta.add(nicknameLabel);
        tarjeta.add(javax.swing.Box.createVerticalStrut(7));
        tarjeta.add(nickname);
        tarjeta.add(javax.swing.Box.createVerticalStrut(22));
        tarjeta.add(crear);
        tarjeta.add(javax.swing.Box.createVerticalStrut(12));
        tarjeta.add(codigo);
        tarjeta.add(javax.swing.Box.createVerticalStrut(10));
        tarjeta.add(unirse);
        tarjeta.add(javax.swing.Box.createVerticalStrut(18));
        JPanel contenedorMensaje = new JPanel(new GridBagLayout());
        contenedorMensaje.setOpaque(false);
        contenedorMensaje.setPreferredSize(new Dimension(340, 42));
        contenedorMensaje.setMinimumSize(new Dimension(340, 42));
        contenedorMensaje.setMaximumSize(new Dimension(340, 42));
        contenedorMensaje.add(mensaje, new GridBagConstraints());
        tarjeta.add(contenedorMensaje);

        JPanel contenedor = new JPanel(new GridBagLayout());
        contenedor.setOpaque(false);
        contenedor.setBorder(BorderFactory.createEmptyBorder(35, 20, 35, 20));
        contenedor.add(tarjeta);
        add(contenedor, BorderLayout.CENTER);

        crear.addActionListener(e -> iniciar(false));
        unirse.addActionListener(e -> iniciar(true));
    }

    @Override
    public void refrescar() {
        if (modelo != null && modelo.getUltimoError() != null && mensaje != null) {
            mensaje.setText(modelo.getUltimoError());
            mensaje.setForeground(new java.awt.Color(166, 55, 44));
        }
    }

    private void iniciar(boolean unirse) {
        String nombre = nickname.getText().trim();
        if (nombre.isEmpty()) {
            mensaje.setText("Escribe tu nombre antes de continuar");
            mensaje.setForeground(new java.awt.Color(166, 55, 44));
            nickname.requestFocusInWindow();
            return;
        }
        if (modelo != null) {
            modelo.setNickname(nombre);
        }
        if (unirse) {
            String clave = codigo.getText().trim().toUpperCase(java.util.Locale.ROOT);
            if (clave.length() != 4 || !clave.matches("[A-Z0-9]{4}")) {
                mensaje.setText("El codigo debe tener 4 letras o numeros");
                mensaje.setForeground(new java.awt.Color(166, 55, 44));
                codigo.requestFocusInWindow();
                return;
            }
            if (controlador != null) {
                controlador.unirse(nombre, clave);
            }
        } else if (controlador != null) {
            controlador.crearPartida(nombre);
        }
        mensaje.setText("Preparando partida...");
        mensaje.setForeground(TemaUI.VERDE_HOJA);
        if (continuar != null) {
            continuar.accept(!unirse);
        }
    }
}

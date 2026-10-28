package iu;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

final class DialogoError {
    private DialogoError() {
    }

    static void mostrar(Component padre, String mensaje) {
        Window propietario = padre == null ? null : SwingUtilities.getWindowAncestor(padre);
        JDialog dialogo = new JDialog(propietario, Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setUndecorated(true);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contenido = new JPanel(new BorderLayout(0, 16));
        contenido.setBackground(TemaUI.PANEL);
        contenido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TemaUI.ORO, 2),
                BorderFactory.createEmptyBorder(22, 26, 20, 26)));

        JLabel titulo = TemaUI.etiqueta("No se pudo completar", java.awt.Font.BOLD, 20f,
                TemaUI.VERDE_NOCHE);
        JTextArea detalle = new JTextArea(mensaje == null ? "Ocurrio un error inesperado." : mensaje);
        detalle.setEditable(false);
        detalle.setFocusable(false);
        detalle.setLineWrap(true);
        detalle.setWrapStyleWord(true);
        detalle.setOpaque(false);
        detalle.setForeground(TemaUI.TEXTO);
        detalle.setFont(detalle.getFont().deriveFont(14f));
        detalle.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        JButton aceptar = TemaUI.boton("OK");
        aceptar.setPreferredSize(new java.awt.Dimension(110, 42));
        aceptar.addActionListener(e -> dialogo.dispose());
        JPanel pie = new JPanel();
        pie.setOpaque(false);
        pie.add(aceptar);

        contenido.add(titulo, BorderLayout.NORTH);
        contenido.add(detalle, BorderLayout.CENTER);
        contenido.add(pie, BorderLayout.SOUTH);
        dialogo.setContentPane(contenido);
        dialogo.getRootPane().setDefaultButton(aceptar);
        dialogo.setSize(420, 190);
        dialogo.setMinimumSize(new java.awt.Dimension(360, 170));
        dialogo.setLocationRelativeTo(propietario);
        dialogo.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setVisible(true);
    }
}

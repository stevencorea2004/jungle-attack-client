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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

final class DialogoError {
    private DialogoError() {
    }

    static void mostrar(Component padre, String mensaje) {
        Window propietario = padre == null ? null : SwingUtilities.getWindowAncestor(padre);
        JDialog dialogo = new JDialog(propietario, Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setUndecorated(true);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel contenido = new JPanel(new BorderLayout(0, 14));
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
        JScrollPane desplazamiento = new JScrollPane(detalle);
        desplazamiento.setBorder(BorderFactory.createEmptyBorder());
        desplazamiento.setOpaque(false);
        desplazamiento.getViewport().setOpaque(false);
        desplazamiento.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        desplazamiento.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        desplazamiento.setPreferredSize(new java.awt.Dimension(400, 170));

        JButton aceptar = TemaUI.boton("OK");
        aceptar.setPreferredSize(new java.awt.Dimension(110, 42));
        aceptar.addActionListener(e -> dialogo.dispose());
        JPanel pie = new JPanel();
        pie.setOpaque(false);
        pie.add(aceptar);

        contenido.add(titulo, BorderLayout.NORTH);
        contenido.add(desplazamiento, BorderLayout.CENTER);
        contenido.add(pie, BorderLayout.SOUTH);
        dialogo.setContentPane(contenido);
        dialogo.getRootPane().setDefaultButton(aceptar);
        dialogo.pack();
        java.awt.GraphicsConfiguration configuracion = dialogo.getGraphicsConfiguration();
        java.awt.Rectangle usable = configuracion == null
                ? java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds()
                : configuracion.getBounds();
        java.awt.Insets insets = configuracion == null
                ? new java.awt.Insets(0, 0, 0, 0)
                : java.awt.Toolkit.getDefaultToolkit().getScreenInsets(configuracion);
        int altoMaximo = usable.height - insets.top - insets.bottom - 40;
        if (dialogo.getHeight() > altoMaximo) {
            desplazamiento.setPreferredSize(new java.awt.Dimension(400,
                    Math.max(100, altoMaximo - dialogo.getHeight() + 170)));
            dialogo.pack();
        }
        dialogo.setMinimumSize(new java.awt.Dimension(360, 220));
        dialogo.setLocationRelativeTo(propietario);
        dialogo.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setVisible(true);
    }
}

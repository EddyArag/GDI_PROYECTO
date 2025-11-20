package gui;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

public class LoginAuxCreateDB extends JDialog {
    public interface CreateDBCallback {
        void onCreate(String port, String password);
    }

    public static void showDialog(CreateDBCallback callback) {
        LoginAuxCreateDB dialog = new LoginAuxCreateDB(callback);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    public LoginAuxCreateDB(CreateDBCallback callback) {
        setTitle("Crear Base de Datos (Postgres)");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtPort = new JTextField("5432");
        JPasswordField txtPass = new JPasswordField();

        panel.add(new JLabel("Puerto:"));
        panel.add(txtPort);
        panel.add(new JLabel("Contraseña (postgres):"));
        panel.add(txtPass);

        JButton btnCrear = new JButton("Crear BD y Estructura");
        panel.add(new JLabel());
        panel.add(btnCrear);

        add(panel, BorderLayout.CENTER);

        btnCrear.addActionListener(e -> {
            String port = txtPort.getText().trim();
            String pass = new String(txtPass.getPassword());
            if (port.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar puerto y contraseña.");
                return;
            }
            callback.onCreate(port, pass);
            JOptionPane.showMessageDialog(this, "Creación y carga iniciada. Espere unos segundos.");
            dispose();
        });
    }
}

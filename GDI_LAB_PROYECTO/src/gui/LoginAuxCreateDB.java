package gui;

import java.awt.*;
import javax.swing.*;

public class LoginAuxCreateDB extends JDialog {
    public interface CreateDBCallback {
        void onCreate(String port, String user, String password);
    }

    public static void showDialog(CreateDBCallback callback) {
        LoginAuxCreateDB dialog = new LoginAuxCreateDB(callback);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    public LoginAuxCreateDB(CreateDBCallback callback) {
        setTitle("Crear Base de Datos (Postgres)");
        setSize(380, 230);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(4, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtPort = new JTextField("5432");
        JTextField txtUser = new JTextField("postgres"); // <-- nuevo campo editable
        JPasswordField txtPass = new JPasswordField();

        panel.add(new JLabel("Puerto:"));
        panel.add(txtPort);
        panel.add(new JLabel("Usuario (admin):"));
        panel.add(txtUser);
        panel.add(new JLabel("Contraseña (admin):"));
        panel.add(txtPass);

        JButton btnCrear = new JButton("Crear BD y Estructura");
        panel.add(new JLabel());
        panel.add(btnCrear);

        add(panel, BorderLayout.CENTER);

        btnCrear.addActionListener(e -> {
            String port = txtPort.getText().trim();
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword());
            if (port.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar puerto, usuario y contraseña.");
                return;
            }
            callback.onCreate(port, user, pass);
            JOptionPane.showMessageDialog(this, "Creación y carga iniciada. Espere unos segundos.");
            dispose();
        });
    }
}

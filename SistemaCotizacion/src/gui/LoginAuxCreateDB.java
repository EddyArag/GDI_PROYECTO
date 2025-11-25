package gui;

import java.awt.*;
import javax.swing.*;

/**
 * Diálogo para recopilar credenciales de administrador (postgres) necesarias
 * para crear la base de datos y la estructura inicial.
 *
 * No realiza la creación directamente; invoca el callback con los datos ingresados.
 */
public class LoginAuxCreateDB extends JDialog {
    /**
     * Callback que recibe puerto, usuario y contraseña para realizar la creación.
     */
    public interface CreateDBCallback {
        void onCreate(String port, String user, String password);
    }

    /**
     * Muestra el diálogo de forma modal y simple.
     */
    public static void showDialog(CreateDBCallback callback) {
        LoginAuxCreateDB dialog = new LoginAuxCreateDB(callback);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    /**
     * Constructor que crea la interfaz y valida que los campos no estén vacíos.
     * Al confirmar, invoca el callback y cierra el diálogo.
     */
    public LoginAuxCreateDB(CreateDBCallback callback) {
        setTitle("Crear Base de Datos (Postgres)");
        setSize(400, 260);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Estética mejorada
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 160, 220), 2, true),
            "Datos de conexión administrador", 0, 0, new Font("Segoe UI", Font.BOLD, 15), new Color(100, 160, 220)
        ));
        panel.setBackground(new Color(220, 235, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblPort = new JLabel("Puerto:");
        lblPort.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JTextField txtPort = new JTextField("5432", 10);
        txtPort.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JLabel lblUser = new JLabel("Usuario (admin):");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JTextField txtUser = new JTextField("postgres", 10);
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JLabel lblPass = new JLabel("Contraseña (admin):");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JPasswordField txtPass = new JPasswordField(10);
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JButton btnCrear = new JButton("Crear BD y Estructura");
        btnCrear.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCrear.setBackground(new Color(100, 160, 220));
        btnCrear.setForeground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblPort, gbc);
        gbc.gridx = 1;
        panel.add(txtPort, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblUser, gbc);
        gbc.gridx = 1;
        panel.add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(lblPass, gbc);
        gbc.gridx = 1;
        panel.add(txtPass, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnCrear, gbc);

        add(panel, BorderLayout.CENTER);

        // Mensaje de error (solo si ocurre)
        JLabel lblError = new JLabel();
        lblError.setForeground(Color.RED);
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblError, BorderLayout.SOUTH);

        btnCrear.addActionListener(e -> {
            String port = txtPort.getText().trim();
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword());
            if (port.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                // Muestra error claro en la parte inferior del diálogo.
                lblError.setText("Debe ingresar puerto, usuario y contraseña.");
                return;
            }
            // No mostrar mensaje adicional aquí; delegar la acción al callback.
            callback.onCreate(port, user, pass);
            dispose();
        });
    }
}

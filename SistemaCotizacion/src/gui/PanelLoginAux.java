package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo auxiliar para conectarse a una base de datos PostgreSQL.
 * Muestra campos para host, puerto, nombre de BD, usuario y contraseña.
 *
 * Uso:
 * PanelLoginAux.showDialog(callback) -> el callback se ejecuta cuando la conexión es exitosa.
 */
public class PanelLoginAux extends JDialog {
    /**
     * Interfaz callback para notificar los parámetros de conexión ingresados.
     */
    public interface LoginCallback {
        void onLogin(String host, String port, String db, String user, String pass);
    }

    /**
     * Muestra el diálogo de forma modal y bloqueante hasta que se cierre.
     */
    public static void showDialog(LoginCallback callback) {
        PanelLoginAux dialog = new PanelLoginAux(callback);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    /**
     * Constructor que crea los controles y valida la conexión antes de invocar el callback.
     * Si la conexión falla, permite reintentar o cerrar el diálogo.
     */
    public PanelLoginAux(LoginCallback callback) {
        setTitle("Conexión a Base de Datos");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(6, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtHost = new JTextField("localhost");
        JTextField txtPort = new JTextField("5432");
        JTextField txtDB = new JTextField("sistema_cotizacion");
        JTextField txtUser = new JTextField("innova");
        JPasswordField txtPass = new JPasswordField();

        panel.add(new JLabel("Host:"));
        panel.add(txtHost);
        panel.add(new JLabel("Puerto:"));
        panel.add(txtPort);
        panel.add(new JLabel("Base de Datos:"));
        panel.add(txtDB);
        panel.add(new JLabel("Usuario:"));
        panel.add(txtUser);
        panel.add(new JLabel("Contraseña:"));
        panel.add(txtPass);

        JButton btnIngresar = new JButton("Ingresar");
        panel.add(new JLabel());
        panel.add(btnIngresar);

        add(panel, BorderLayout.CENTER);

        btnIngresar.addActionListener(e -> {
            // Obtiene valores de UI
            String host = txtHost.getText().trim();
            String port = txtPort.getText().trim();
            String db = txtDB.getText().trim();
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword());

            try {
                // Construye la URL JDBC y prueba la conexión.
                String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, db);
                java.sql.Connection conn = java.sql.DriverManager.getConnection(url, user, pass);
                conn.close();
                // Si la conexión es exitosa, invoca el callback con los parámetros.
                callback.onLogin(host, port, db, user, pass);
                JOptionPane.showMessageDialog(this, "Conexión exitosa.");
                dispose();
            } catch (Exception ex2) {
                // Mensaje detallado para ayudar al usuario a corregir datos.
                StringBuilder msg = new StringBuilder("No se pudo conectar. Verifique:\n");
                msg.append("- Host: ").append(host).append("\n");
                msg.append("- Puerto: ").append(port).append("\n");
                msg.append("- Base de Datos: ").append(db).append("\n");
                msg.append("- Usuario: ").append(user).append("\n");
                msg.append("- Contraseña: (verifique que sea correcta)\n");
                msg.append("\n¿Desea volver a intentarlo?");
                int opt = JOptionPane.showConfirmDialog(this, msg.toString(), "Error de conexión", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (opt == JOptionPane.NO_OPTION) {
                    // Cierra el diálogo si el usuario no desea reintentar.
                    dispose();
                }
                // Si selecciona YES, se mantiene abierto para reintentar.
            }
        });
    }
}

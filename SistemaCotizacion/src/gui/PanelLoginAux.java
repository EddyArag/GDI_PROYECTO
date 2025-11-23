package gui;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class PanelLoginAux extends JDialog {
    public interface LoginCallback {
        void onLogin(String host, String port, String db, String user, String pass);
    }

    public static void showDialog(LoginCallback callback) {
        PanelLoginAux dialog = new PanelLoginAux(callback);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

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
            String host = txtHost.getText().trim();
            String port = txtPort.getText().trim();
            String db = txtDB.getText().trim();
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword());

            try {
                String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, db);
                java.sql.Connection conn = java.sql.DriverManager.getConnection(url, user, pass);
                conn.close();
                callback.onLogin(host, port, db, user, pass);
                JOptionPane.showMessageDialog(this, "Conexión exitosa.");
                dispose();
            } catch (Exception ex2) {
                StringBuilder msg = new StringBuilder("No se pudo conectar. Verifique:\n");
                msg.append("- Host: ").append(host).append("\n");
                msg.append("- Puerto: ").append(port).append("\n");
                msg.append("- Base de Datos: ").append(db).append("\n");
                msg.append("- Usuario: ").append(user).append("\n");
                msg.append("- Contraseña: (verifique que sea correcta)\n");
                msg.append("\n¿Desea volver a intentarlo?");
                int opt = JOptionPane.showConfirmDialog(this, msg.toString(), "Error de conexión", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (opt == JOptionPane.NO_OPTION) {
                    dispose();
                }
                // Si YES, deja la ventana abierta para reintentar
            }
        });
    }
}

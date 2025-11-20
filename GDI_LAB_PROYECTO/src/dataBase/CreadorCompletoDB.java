package dataBase;

import java.sql.Connection;
import java.sql.DriverManager;

public class CreadorCompletoDB {
    /**
     * Crea toda la estructura de la base de datos y datos iniciales.
     * Este método debe ejecutarse usando el usuario admin (p.ej. postgres) y la contraseña correspondiente.
     * @param port Puerto de conexión a PostgreSQL.
     * @param adminUser Usuario administrador (antes estaba fijo "postgres").
     * @param password Contraseña del usuario admin.
     */
    public static void crearTodo(String port, String adminUser, String password) {
        Connection conn = null;
        try {
            // 1. Conexión como admin para crear rol y base de datos
            String urlAdmin = String.format("jdbc:postgresql://localhost:%s/postgres", port);
            conn = DriverManager.getConnection(urlAdmin, adminUser, password);

            // 2. Crear rol innova y darle permisos
            dataBase.CargaDb1.ejecutar(conn);

            // 3. Crear base de datos sistema_cotizacion con owner innova
            dataBase.CargaDb2.ejecutar(conn);

            try { conn.close(); } catch (Exception ignore) {}

            // 4. Espera a que la base se cree antes de conectar como innova
            try { Thread.sleep(1000); } catch (InterruptedException ignore) {}

            // 5. Conexión como innova a la nueva base de datos
            String urlApp = String.format("jdbc:postgresql://localhost:%s/sistema_cotizacion", port);
            Connection connNueva = null;
            try {
                connNueva = DriverManager.getConnection(urlApp, "innova", "Soft123!");
                // 6. Crear tablas y estructura
                dataBase.CargaDb3.ejecutar(connNueva);
                // 7. Crear funciones y triggers
                dataBase.CargaDb4.ejecutar(connNueva);
                // 8. Crear procedimientos
                dataBase.CargaDb5.ejecutar(connNueva);
                // 9. Insertar datos iniciales
                dataBase.CargaDb6.ejecutar(connNueva);
            } catch (Exception ex) {
                // Si falla la conexión como innova, muestra mensaje claro
                javax.swing.JOptionPane.showMessageDialog(null,
                    "No se pudo conectar como innova tras crear la base. Verifique que el rol innova tenga permisos y que la base se creó correctamente.");
            } finally {
                try { if (connNueva != null) connNueva.close(); } catch (Exception ignore) {}
            }
        } catch (Exception ex) {
            // No mostrar más ventanas, solo lanza excepción para que DatabaseConnection lo maneje
            throw new RuntimeException("Error de conexión admin.");
        }
    }
}

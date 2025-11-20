package dataBase;

/**
 * Clase de utilidad para obtener conexiones JDBC a la base de datos PostgreSQL.
 * Configura los parámetros de conexión y expone un método estático para obtener la conexión.
 */
import gui.PanelLoginAux;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    // Cambia estos valores si usas otra PC o base de datos:
    // - URL: host, puerto y nombre de la base de datos
    // - USER: usuario de la base de datos
    // - PASSWORD: contraseña de la base de datos
    private static String URL = "jdbc:postgresql://localhost:5432/sistema_cotizacion";
    private static String USER = "innova";      // Usuario creado en script
    private static String PASSWORD = "Soft123!";    // Contraseña del usuario

    /**
     * Obtiene una conexión JDBC a la base de datos PostgreSQL.
     * @return Connection activa a la base de datos.
     * @throws SQLException si ocurre un error de conexión.
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            // Ejecuta CargaDb1 siempre (rol innova)
            CargaDb1.ejecutar(conn);
            // Verifica estructura mínima
            if (!verificarEstructura(conn)) {
                // Si la estructura no existe, verifica si la base existe
                if (!existeBaseDatos(getHostFromUrl(), getPortFromUrl(), getDbNameFromUrl(), USER, PASSWORD)) {
                    // Conecta a postgres y ejecuta CargaDb2 (crear base)
                    try (Connection connPostgres = DriverManager.getConnection(
                            String.format("jdbc:postgresql://%s:%s/postgres", getHostFromUrl(), getPortFromUrl()), USER, PASSWORD)) {
                        CargaDb2.ejecutar(connPostgres);
                    }
                    // Ahora conecta a la nueva base
                    conn = DriverManager.getConnection(URL, USER, PASSWORD);
                }
                // Ejecuta los scripts de carga en orden
                CargaDb3.ejecutar(conn);
                CargaDb4.ejecutar(conn);
                CargaDb5.ejecutar(conn);
            }
            return conn;
        } catch (SQLException ex) {
            String currentPort = URL.split(":")[2].split("/")[0];
            int opt = javax.swing.JOptionPane.showConfirmDialog(null,
                "No se pudo conectar al puerto " + currentPort + ".\n¿Desea intentar con otro puerto?",
                "Error de conexión", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.ERROR_MESSAGE);
            if (opt == javax.swing.JOptionPane.YES_OPTION) {
                String nuevoPuerto = javax.swing.JOptionPane.showInputDialog(null, "Ingrese el nuevo puerto:", currentPort);
                if (nuevoPuerto != null && !nuevoPuerto.trim().isEmpty()) {
                    String[] urlParts = URL.split(":");
                    String dbName = urlParts[2].split("/")[1];
                    String newURL = urlParts[0] + ":" + urlParts[1] + ":" + nuevoPuerto.trim() + "/" + dbName;
                    URL = newURL;
                    try {
                        conn = DriverManager.getConnection(URL, USER, PASSWORD);
                        // Ejecuta CargaDb1 siempre (rol innova)
                        CargaDb1.ejecutar(conn);
                        // Verifica estructura mínima
                        if (!verificarEstructura(conn)) {
                            // Si la estructura no existe, verifica si la base existe
                            if (!existeBaseDatos(getHostFromUrl(), getPortFromUrl(), getDbNameFromUrl(), USER, PASSWORD)) {
                                // Conecta a postgres y ejecuta CargaDb2 (crear base)
                                try (Connection connPostgres = DriverManager.getConnection(
                                        String.format("jdbc:postgresql://%s:%s/postgres", getHostFromUrl(), getPortFromUrl()), USER, PASSWORD)) {
                                    CargaDb2.ejecutar(connPostgres);
                                }
                                // Ahora conecta a la nueva base
                                conn = DriverManager.getConnection(URL, USER, PASSWORD);
                            }
                            // Ejecuta los scripts de carga en orden
                            CargaDb3.ejecutar(conn);
                            CargaDb4.ejecutar(conn);
                            CargaDb5.ejecutar(conn);
                        }
                        return conn;
                    } catch (SQLException ex2) {
                        throw ex2;
                    }
                }
            } else {
                final boolean[] conectado = {false};
                PanelLoginAux.showDialog((host, port, db, user, pass) -> {
                    try {
                        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, db);
                        Connection c = DriverManager.getConnection(url, user, pass);
                        setConnectionParams(host, port, db, user, pass);
                        // Ejecuta CargaDb1 siempre (rol innova)
                        CargaDb1.ejecutar(c);
                        // Verifica estructura mínima
                        if (!verificarEstructura(c)) {
                            // Si la estructura no existe, verifica si la base existe
                            if (!existeBaseDatos(getHostFromUrl(), getPortFromUrl(), getDbNameFromUrl(), USER, PASSWORD)) {
                                // Conecta a postgres y ejecuta CargaDb2 (crear base)
                                try (Connection connPostgres = DriverManager.getConnection(
                                        String.format("jdbc:postgresql://%s:%s/postgres", getHostFromUrl(), getPortFromUrl()), USER, PASSWORD)) {
                                    CargaDb2.ejecutar(connPostgres);
                                }
                                // Ahora conecta a la nueva base
                                c = DriverManager.getConnection(URL, USER, PASSWORD);
                            }
                            // Ejecuta los scripts de carga en orden
                            CargaDb3.ejecutar(c);
                            CargaDb4.ejecutar(c);
                            CargaDb5.ejecutar(c);
                            CargaDb6.ejecutar(c);
                        }
                        conectado[0] = true;
                    } catch (SQLException e) {
                        javax.swing.JOptionPane.showMessageDialog(null, "No se pudo conectar con los datos ingresados.");
                    }
                });
                if (conectado[0]) {
                    conn = DriverManager.getConnection(URL, USER, PASSWORD);
                    // Ejecuta CargaDb1 siempre (rol innova)
                    CargaDb1.ejecutar(conn);
                    // Verifica estructura mínima
                    if (!verificarEstructura(conn)) {
                        // Si la estructura no existe, verifica si la base existe
                        if (!existeBaseDatos(getHostFromUrl(), getPortFromUrl(), getDbNameFromUrl(), USER, PASSWORD)) {
                            // Conecta a postgres y ejecuta CargaDb2 (crear base)
                            try (Connection connPostgres = DriverManager.getConnection(
                                    String.format("jdbc:postgresql://%s:%s/postgres", getHostFromUrl(), getPortFromUrl()), USER, PASSWORD)) {
                                CargaDb2.ejecutar(connPostgres);
                            }
                            // Ahora conecta a la nueva base
                            conn = DriverManager.getConnection(URL, USER, PASSWORD);
                        }
                        // Ejecuta los scripts de carga en orden
                        CargaDb3.ejecutar(conn);
                        CargaDb4.ejecutar(conn);
                        CargaDb5.ejecutar(conn);
                    }
                    return conn;
                }
            }
            throw ex;
        }
    }

    public static void setConnectionParams(String host, String port, String db, String user, String pass) {
        URL = String.format("jdbc:postgresql://%s:%s/%s", host, port, db);
        USER = user;
        PASSWORD = pass;
    }

    // Verificación interna de estructura mínima (no muestra nada al usuario)
    private static boolean verificarEstructura(Connection conn) {
        try (Statement st = conn.createStatement()) {
            // Verifica existencia de tablas y funciones mínimas (ajusta según tu modelo)
            st.executeQuery("SELECT 1 FROM information_schema.tables WHERE table_name = 'cliente' LIMIT 1");
            st.executeQuery("SELECT 1 FROM information_schema.tables WHERE table_name = 'cotizacion' LIMIT 1");
            st.executeQuery("SELECT 1 FROM information_schema.tables WHERE table_name = 'servicio_producto' LIMIT 1");
            // Si no lanza excepción, existe la estructura mínima
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

    // Verifica si la base de datos existe en el servidor
    private static boolean existeBaseDatos(String host, String port, String dbName, String user, String pass) {
        try (Connection conn = DriverManager.getConnection(
                String.format("jdbc:postgresql://%s:%s/postgres", host, port), user, pass);
             Statement st = conn.createStatement();
             java.sql.ResultSet rs = st.executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'")) {
            return rs.next();
        } catch (Exception ex) {
            return false;
        }
    }

    // Helpers para extraer host, puerto y db de la URL actual
    private static String getHostFromUrl() {
        String[] parts = URL.split(":");
        return parts[1].replaceAll("/", "");
    }
    private static String getPortFromUrl() {
        String[] parts = URL.split(":");
        return parts[2].split("/")[0];
    }
    private static String getDbNameFromUrl() {
        String[] parts = URL.split(":");
        return parts[2].split("/")[1];
    }
}
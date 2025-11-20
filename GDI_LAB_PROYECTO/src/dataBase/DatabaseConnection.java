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
        String host = getHostFromUrl();
        String[] portHolder = new String[] { getPortFromUrl() };
        String dbName = getDbNameFromUrl();

        try {
            // Intenta conectar directamente
            try {
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException ex) {
                // Verifica si la base existe usando usuario postgres (no innova)
                boolean bdExiste = existeBaseDatos(host, portHolder[0], dbName, "postgres", "Soft123!");
                if (!bdExiste) {
                    // Crea la base usando usuario admin provisto por el usuario
                    final boolean[] creado = {false};
                    final boolean[] errorAdmin = {false};
                    gui.LoginAuxCreateDB.showDialog((p, adminUser, password) -> {
                        try {
                            dataBase.CreadorCompletoDB.crearTodo(p, adminUser, password);
                            portHolder[0] = p;
                            URL = String.format("jdbc:postgresql://%s:%s/%s", host, portHolder[0], dbName);
                            // Verifica si la base se creó correctamente usando las credenciales admin ingresadas
                            if (existeBaseDatos(host, portHolder[0], dbName, adminUser, password)) {
                                creado[0] = true;
                            }
                        } catch (Exception e) {
                            errorAdmin[0] = true;
                        }
                    });
                    try { Thread.sleep(1000); } catch (InterruptedException ignore) {}
                    // Si hubo error de admin, muestra solo ese error y detén el flujo
                    if (errorAdmin[0]) {
                        javax.swing.JOptionPane.showMessageDialog(null, "No se pudo conectar al servidor con el puerto, usuario o contraseña ingresados.\nVerifique los datos e intente nuevamente.");
                        throw new SQLException("Error de conexión admin.");
                    }
                    // Solo muestra error si realmente NO se creó la base
                    if (!creado[0]) {
                        javax.swing.JOptionPane.showMessageDialog(null, "No se pudo crear la base de datos. Verifique el puerto y las credenciales de administrador.");
                        throw new SQLException("No se pudo crear la base de datos.");
                    }
                    // Ahora intenta conectar usando innova (usuario de la aplicación)
                    try {
                        conn = DriverManager.getConnection(URL, USER, PASSWORD);
                    } catch (SQLException ex2) {
                        javax.swing.JOptionPane.showMessageDialog(null, "No se pudo conectar a la base recién creada con el usuario de la aplicación (innova). Verifique que el usuario innova tenga permisos.");
                        throw ex2;
                    }
                } else {
                    // Si la base existe pero no conecta, relanza el error para manejo de puerto/login
                    throw ex;
                }
            }

            // Si la base existe y conecta, sigue el flujo normal
            CargaDb1.ejecutar(conn);
            if (!verificarEstructura(conn)) {
                CargaDb3.ejecutar(conn);
                CargaDb4.ejecutar(conn);
                CargaDb5.ejecutar(conn);
                CargaDb6.ejecutar(conn);
            }
            return conn;
        } catch (SQLException ex) {
            // Si el error fue por admin, no mostrar más ventanas
            if (ex.getMessage() != null && ex.getMessage().contains("Error de conexión admin.")) {
                throw ex;
            }
            // Solo maneja error de puerto aquí
            String currentPort = portHolder[0];
            int opt = javax.swing.JOptionPane.showConfirmDialog(null,
                "No se pudo conectar al puerto " + currentPort + ".\n¿Desea intentar con otro puerto?",
                "Error de conexión", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.ERROR_MESSAGE);
            if (opt == javax.swing.JOptionPane.YES_OPTION) {
                String nuevoPuerto = javax.swing.JOptionPane.showInputDialog(null, "Ingrese el nuevo puerto:", currentPort);
                if (nuevoPuerto != null && !nuevoPuerto.trim().isEmpty()) {
                    portHolder[0] = nuevoPuerto.trim();
                    URL = String.format("jdbc:postgresql://%s:%s/%s", host, portHolder[0], dbName);
                    try {
                        conn = DriverManager.getConnection(URL, USER, PASSWORD);
                        CargaDb1.ejecutar(conn);
                        if (!verificarEstructura(conn)) {
                            CargaDb3.ejecutar(conn);
                            CargaDb4.ejecutar(conn);
                            CargaDb5.ejecutar(conn);
                            CargaDb6.ejecutar(conn);
                        }
                        return conn;
                    } catch (SQLException ex2) {
                        throw ex2;
                    }
                }
            } else {
                final boolean[] conectado = {false};
                PanelLoginAux.showDialog((h, p, db, user, pass) -> {
                    try {
                        String url = String.format("jdbc:postgresql://%s:%s/%s", h, p, db);
                        Connection c = DriverManager.getConnection(url, user, pass);
                        setConnectionParams(h, p, db, user, pass);
                        CargaDb1.ejecutar(c);
                        if (!verificarEstructura(c)) {
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
                    CargaDb1.ejecutar(conn);
                    if (!verificarEstructura(conn)) {
                        CargaDb3.ejecutar(conn);
                        CargaDb4.ejecutar(conn);
                        CargaDb5.ejecutar(conn);
                        CargaDb6.ejecutar(conn);
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
        // jdbc:postgresql://localhost:5432/sistema_cotizacion
        // Remove protocol and get host
        try {
            String url = URL;
            int idx1 = url.indexOf("//");
            int idx2 = url.indexOf(":", idx1 + 2);
            if (idx1 >= 0 && idx2 > idx1) {
                return url.substring(idx1 + 2, idx2);
            }
            // fallback
            return "localhost";
        } catch (Exception ex) {
            return "localhost";
        }
    }
    private static String getPortFromUrl() {
        // jdbc:postgresql://localhost:5432/sistema_cotizacion
        try {
            String url = URL;
            int idx1 = url.indexOf("//");
            int idx2 = url.indexOf(":", idx1 + 2);
            int idx3 = url.indexOf("/", idx2 + 1);
            if (idx2 > idx1 && idx3 > idx2) {
                return url.substring(idx2 + 1, idx3);
            }
            // fallback
            return "5432";
        } catch (Exception ex) {
            return "5432";
        }
    }
    private static String getDbNameFromUrl() {
        // jdbc:postgresql://localhost:5432/sistema_cotizacion
        try {
            String url = URL;
            int idx3 = url.lastIndexOf("/");
            if (idx3 >= 0 && idx3 < url.length() - 1) {
                return url.substring(idx3 + 1);
            }
            // fallback
            return "sistema_cotizacion";
        } catch (Exception ex) {
            return "sistema_cotizacion";
        }
    }
}
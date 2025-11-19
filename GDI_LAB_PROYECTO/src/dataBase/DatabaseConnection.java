package dataBase;

/**
 * Clase de utilidad para obtener conexiones JDBC a la base de datos PostgreSQL.
 * Configura los parámetros de conexión y expone un método estático para obtener la conexión.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Cambia estos valores si usas otra PC o base de datos:
    // - URL: host, puerto y nombre de la base de datos
    // - USER: usuario de la base de datos
    // - PASSWORD: contraseña de la base de datos
    private static final String URL = "jdbc:postgresql://localhost:5432/sistema_cotizacion_gdi";
    private static final String USER = "postgres";
    private static final String PASSWORD = "eddy";

    /**
     * Obtiene una conexión JDBC a la base de datos PostgreSQL.
     * @return Connection activa a la base de datos.
     * @throws SQLException si ocurre un error de conexión.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
package dataBase;

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

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
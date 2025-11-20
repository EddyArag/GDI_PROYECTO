package dataBase;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class CargaDb2 {
    public static void ejecutar(Connection conn) {
        Statement st = null;
        try {
            st = conn.createStatement();
            // Script 2: Crear la base de datos con owner innova si no existe
            // El CREATE DATABASE no puede ejecutarse dentro de DO $$ ... $$ ni en transacción.
            // Por eso, ejecuta solo el CREATE DATABASE si no existe.
            java.sql.ResultSet rs = st.executeQuery("SELECT 1 FROM pg_database WHERE datname = 'sistema_cotizacion'");
            boolean existe = rs.next();
            rs.close();
            if (!existe) {
                st.executeUpdate(
                    "CREATE DATABASE sistema_cotizacion " +
                    "WITH OWNER = innova " +
                    "ENCODING = 'UTF8' " +
                    "TABLESPACE = pg_default " +
                    "CONNECTION LIMIT = -1;"
                );
            }
        } catch (SQLException ex) {
            // No mostrar nada al usuario
        } finally {
            try { if (st != null) st.close(); } catch (Exception ignore) {}
        }
    }
}

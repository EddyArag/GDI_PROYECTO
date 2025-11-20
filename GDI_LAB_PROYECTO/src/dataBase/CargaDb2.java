package dataBase;

import java.sql.Connection;
import java.sql.Statement;

public class CargaDb2 {
    public static void ejecutar(Connection conn) {
        try (Statement st = conn.createStatement()) {
            // Script 2: Crear la base de datos con owner innova si no existe
            st.executeUpdate(
                "DO $$\n" +
                "BEGIN\n" +
                "   IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'sistema_cotizacion') THEN\n" +
                "      CREATE DATABASE sistema_cotizacion\n" +
                "          WITH OWNER = innova\n" +
                "          ENCODING = 'UTF8'\n" +
                "          TABLESPACE = pg_default\n" +
                "          CONNECTION LIMIT = -1;\n" +
                "   END IF;\n" +
                "END\n" +
                "$$;"
            );
        } catch (Exception ex) {
            // No mostrar nada al usuario
        }
    }
}

package dataBase;

import java.sql.Connection;
import java.sql.Statement;

public class CargaDb1 {
    public static void ejecutar(Connection conn) {
        try (Statement st = conn.createStatement()) {
            // Script 1: Crear rol innova si no existe y darle SUPERUSER
            st.executeUpdate(
                "DO $$\n" +
                "BEGIN\n" +
                "   IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'innova') THEN\n" +
                "      CREATE ROLE innova LOGIN\n" +
                "          PASSWORD 'Soft123!'\n" +
                "          NOSUPERUSER\n" +
                "          INHERIT\n" +
                "          CREATEDB\n" +
                "          CREATEROLE\n" +
                "          NOREPLICATION;\n" +
                "   END IF;\n" +
                "END\n" +
                "$$;"
            );
            st.executeUpdate("ALTER ROLE innova WITH SUPERUSER;");
        } catch (Exception ex) {
            // No mostrar nada al usuario
        }
    }
}

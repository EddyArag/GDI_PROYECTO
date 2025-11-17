package dataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.util.PGobject;
import java.math.BigDecimal; // Importar para manejar tipos monetarios

public class CotizacionDB {

    public static List<String[]> listarCotizaciones() throws SQLException {
        List<String[]> cotizaciones = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt
                        .executeQuery("SELECT ncot, femi, cliente_id, garantia FROM FN_LISTAR_COTIZACIONES()")) {
            while (rs.next()) {
                cotizaciones.add(new String[] {
                        rs.getString("ncot"),
                        rs.getString("femi"),
                        rs.getString("cliente_id"),
                        rs.getString("garantia")
                });
            }
        }
        return cotizaciones;
    }

    public static List<String[]> listarCotizacionesDesactivadas() throws SQLException {
        List<String[]> cotizaciones = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT ncot, femi, cliente_id, garantia FROM FN_LISTAR_COTIZACIONES_DESACTIVADAS()")) {
            while (rs.next()) {
                cotizaciones.add(new String[] {
                        rs.getString("ncot"),
                        rs.getString("femi"),
                        rs.getString("cliente_id"),
                        rs.getString("garantia")
                });
            }
        }
        return cotizaciones;
    }

    public static void eliminarLogicoCotizacion(String ncot) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("CALL SP_ELIMINAR_LOGICO_COTIZACION(?)")) {
            cs.setString(1, ncot);
            cs.execute();
        }
    }

    public static void reactivarCotizacion(String ncot) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("CALL SP_REACTIVAR_COTIZACION(?)")) {
            cs.setString(1, ncot);
            cs.execute();
        }
    }

    public static void crearCotizacionCompleta(
            String ncot,
            int idCli,
            int idEmp,
            java.sql.Date femi,
            double desct,
            String cond,
            String gara,
            String tent,
            java.sql.Date vofer,
            java.util.List<DetalleCotizacion> detalles) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Insertar cabecera
            try (CallableStatement cs = conn
                    .prepareCall("CALL SP_INSERTAR_CABECERA_COTIZACION(?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                String ncotFixed = String.format("%-10s", ncot); // CHAR(10)
                cs.setString(1, ncotFixed);
                cs.setInt(2, idCli);
                cs.setInt(3, idEmp);
                cs.setDate(4, femi);
                cs.setBigDecimal(5, BigDecimal.valueOf(desct)); // DECIMAL(10,2)
                cs.setString(6, cond); // VARCHAR(100)
                cs.setString(7, gara); // VARCHAR(100)
                cs.setString(8, tent); // VARCHAR(50)
                cs.setDate(9, vofer);
                cs.execute();
            }

            // 2. Insertar detalles
            for (DetalleCotizacion d : detalles) {
                try (CallableStatement cs = conn.prepareCall("CALL SP_AGREGAR_DETALLE(?, ?, ?)")) {
                    cs.setString(1, String.format("%-10s", ncot));
                    cs.setString(2, String.format("%-4s", d.id_serv_in));
                    cs.setInt(3, d.cant_in);
                    cs.execute();
                }
            }

            conn.commit();
        } catch (SQLException ex) {
            if (conn != null)
                conn.rollback();
            throw ex;
        } finally {
            if (conn != null)
                conn.setAutoCommit(true);
            if (conn != null)
                conn.close();
        }
    }

    public static void modificarCabeceraCotizacion(
            String ncot,
            BigDecimal desct,
            String cond,
            String tent,
            Date vofer) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("CALL SP_MODIFICAR_CABECERA_COTIZACION(?, ?, ?, ?, ?)")) {
            cs.setString(1, String.format("%-10s", ncot));
            cs.setBigDecimal(2, desct);
            cs.setString(3, cond);
            cs.setString(4, tent);
            cs.setDate(5, vofer);
            cs.execute();
        }
    }

    // Clase auxiliar para detalle (permanece igual, pero asegúrate de que el
    // aplicativo use BigDecimal/Date en el llamado)
    public static class DetalleCotizacion implements java.io.Serializable {
        public String id_serv_in;
        public int cant_in;

        public DetalleCotizacion(String id_serv_in, int cant_in) {
            this.id_serv_in = id_serv_in;
            this.cant_in = cant_in;
        }
    }
}

// Puedes agregar métodos para crear/modificar cotizaciones y detalles usando
// los procedimientos correspondientes.

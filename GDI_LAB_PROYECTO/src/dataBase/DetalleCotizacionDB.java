package dataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleCotizacionDB {

    public static List<String[]> listarLineasCotizacion(String ncot) throws SQLException {
        List<String[]> lineas = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT id_det, id_serv, descp, punit, cant, linea_total FROM FN_LINEAS_COTIZACION(?)")) {
            ps.setString(1, ncot);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lineas.add(new String[] {
                        String.valueOf(rs.getInt("id_det")),
                        rs.getString("id_serv"),
                        rs.getString("descp"),
                        rs.getString("punit"),
                        rs.getString("cant"),
                        rs.getString("linea_total")
                });
            }
        }
        return lineas;
    }

    // No modificar, solo usar para agregar detalles a cotizaciones reales.
    public static void agregarDetalle(String ncot, String idServ, int cant) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("{ call SP_AGREGAR_DETALLE(?, ?, ?) }")) {
            cs.setString(1, ncot);
            cs.setString(2, idServ);
            cs.setInt(3, cant);
            cs.execute();
        }
    }

    public static void modificarDetalle(int idDet, String ncot, String idServ, int cant) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("{ call SP_MODIFICAR_DETALLE(?, ?, ?, ?) }")) {
            cs.setInt(1, idDet);
            cs.setString(2, ncot);
            cs.setString(3, idServ);
            cs.setInt(4, cant);
            cs.execute();
        }
    }

    public static void eliminarDetalle(int idDet, String ncot) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("{ call SP_ELIMINAR_DETALLE(?, ?) }")) {
            cs.setInt(1, idDet);
            cs.setString(2, ncot);
            cs.execute();
        }
    }
}

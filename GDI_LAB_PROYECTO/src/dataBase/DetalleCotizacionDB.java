package dataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a datos para operaciones sobre los detalles de cotización.
 * Permite listar, agregar, modificar y eliminar detalles de cotización.
 */
public class DetalleCotizacionDB {

    /**
     * Lista todas las líneas de detalle de una cotización usando
     * FN_LINEAS_COTIZACION.
     * 
     * @param ncot Número de cotización.
     * @return Lista de detalles (ID, servicio, descripción, precio unitario,
     *         cantidad, total línea).
     */
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

    /**
     * Agrega un detalle a una cotización usando SP_AGREGAR_DETALLE.
     */
    public static void agregarDetalle(String ncot, String idServ, int cant) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("CALL SP_AGREGAR_DETALLE(?, ?, ?)")) {
            cs.setString(1, String.format("%-10s", ncot));
            cs.setString(2, String.format("%-4s", idServ));
            cs.setInt(3, cant);
            cs.execute();
        }
    }

    /**
     * Modifica un detalle de cotización usando SP_MODIFICAR_DETALLE.
     */
    public static void modificarDetalle(int idDet, String ncot, String idServ, int cant) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("CALL SP_MODIFICAR_DETALLE(?, ?, ?, ?)")) {
            cs.setInt(1, idDet);
            cs.setString(2, String.format("%-10s", ncot));
            cs.setString(3, String.format("%-4s", idServ));
            cs.setInt(4, cant);
            cs.execute();
        }
    }

    /**
     * Elimina un detalle de cotización usando SP_ELIMINAR_DETALLE.
     */
    public static void eliminarDetalle(int idDet, String ncot) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("CALL SP_ELIMINAR_DETALLE(?, ?)")) {
            cs.setInt(1, idDet);
            cs.setString(2, String.format("%-10s", ncot));
            cs.execute();
        }
    }
}

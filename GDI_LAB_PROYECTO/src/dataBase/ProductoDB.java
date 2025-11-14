package dataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDB {

    public static List<String[]> listarProductos() throws SQLException {
        List<String[]> productos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT id_serv, descripcion, precio_unitario, stock_actual FROM FN_LISTAR_SERVICIOS_PRODUCTOS()")) {
            while (rs.next()) {
                productos.add(new String[] {
                        rs.getString("id_serv"),
                        rs.getString("descripcion"),
                        rs.getString("precio_unitario"),
                        rs.getString("stock_actual")
                });
            }
        }
        return productos;
    }

    public static void insertarProducto(String idServ, String descp, double punit, int stock) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("{ call SP_INSERTAR_PRODUCTO(?, ?, ?, ?) }")) {
            cs.setString(1, idServ);
            cs.setString(2, descp);
            cs.setDouble(3, punit);
            cs.setInt(4, stock);
            cs.execute();
        }
    }

    public static void modificarProducto(String idServ, String descp, double punit, int stock) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("{ call SP_MODIFICAR_PRODUCTO(?, ?, ?, ?) }")) {
            cs.setString(1, idServ);
            cs.setString(2, descp);
            cs.setDouble(3, punit);
            cs.setInt(4, stock);
            cs.execute();
        }
    }

    public static void eliminarLogicoProducto(String idServ) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("{ call SP_ELIMINAR_LOGICO_PRODUCTO(?) }")) {
            cs.setString(1, idServ);
            cs.execute();
        }
    }

    public static void reactivarProducto(String idServ) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("{ call SP_REACTIVAR_PRODUCTO(?) }")) {
            cs.setString(1, idServ);
            cs.execute();
        }
    }
}

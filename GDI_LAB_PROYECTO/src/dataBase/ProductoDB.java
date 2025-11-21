package dataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a datos para operaciones CRUD y consultas sobre
 * productos/servicios.
 */
public class ProductoDB {

    /**
     * Lista todos los productos/servicios activos usando
     * FN_LISTAR_SERVICIOS_PRODUCTOS().
     * 
     * @return Lista de productos (ID, descripción, precio, stock).
     */
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

    /**
     * Inserta un nuevo producto/servicio usando SP_INSERTAR_PRODUCTO.
     */
    public static void insertarProducto(String idServ, String descp, double punit, int stock) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("CALL SP_INSERTAR_PRODUCTO(?, ?, ?, ?)")) {
            cs.setString(1, String.format("%-4s", idServ)); // CHAR(4)
            cs.setString(2, descp); // VARCHAR(200)
            cs.setBigDecimal(3, new java.math.BigDecimal(punit).setScale(2)); // DECIMAL(7,2)
            cs.setInt(4, stock); // INT
            cs.execute();
        }
    }

    /**
     * Modifica los datos de un producto/servicio usando SP_MODIFICAR_PRODUCTO.
     */
    public static void modificarProducto(String idServ, String descp, double punit, int stock) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("CALL SP_MODIFICAR_PRODUCTO(?, ?, ?, ?)")) {
            cs.setString(1, String.format("%-4s", idServ)); // CHAR(4)
            cs.setString(2, descp); // VARCHAR(200)
            cs.setBigDecimal(3, new java.math.BigDecimal(punit).setScale(2)); // DECIMAL(7,2)
            cs.setInt(4, stock); // INT
            cs.execute();
        }
    }

    /**
     * Elimina lógicamente un producto/servicio usando SP_ELIMINAR_LOGICO_PRODUCTO.
     */
    public static void eliminarLogicoProducto(String idServ) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("CALL SP_ELIMINAR_LOGICO_PRODUCTO(?)")) {
            cs.setString(1, String.format("%-4s", idServ)); // CHAR(4)
            cs.execute();
        }
    }

    /**
     * Reactiva un producto/servicio desactivado usando SP_REACTIVAR_PRODUCTO.
     */
    public static void reactivarProducto(String idServ) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("CALL SP_REACTIVAR_PRODUCTO(?)")) {
            cs.setString(1, String.format("%-4s", idServ));
            cs.execute();
        }
    }

    /**
     * Lista todos los productos/servicios desactivados usando
     * FN_LISTAR_SERVICIOS_PRODUCTOS_DESACTIVADOS().
     * 
     * @return Lista de productos desactivados.
     */
    public static List<String[]> listarProductosDesactivados() throws SQLException {
        List<String[]> productos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT id_serv, descripcion, precio_unitario, stock_actual FROM FN_LISTAR_SERVICIOS_PRODUCTOS_DESACTIVADOS()")) {
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

    /**
     * Busca productos/servicios por texto usando FN_BUSCAR_PRODUCTOS_POR_TEXTO.
     * 
     * @param texto Texto a buscar en la descripción.
     * @return Lista de productos filtrados.
     */
    public static List<String[]> buscarProductosPorTexto(String texto) throws SQLException {
        List<String[]> productos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT id_serv, descp, punit, stock FROM FN_BUSCAR_PRODUCTOS_POR_TEXTO(?)")) {
            ps.setString(1, texto);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                productos.add(new String[] {
                        rs.getString("id_serv"),
                        rs.getString("descp"),
                        rs.getString("punit"),
                        rs.getString("stock")
                });
            }
        }
        return productos;
    }

    /**
     * Lista productos/servicios activos ordenados por precio descendente.
     */
    public static List<String[]> listarProductosPorPrecioDesc() throws SQLException {
        List<String[]> productos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT id_serv, descripcion, precio_unitario, stock_actual FROM FN_LISTAR_SERVICIOS_PRODUCTOS() ORDER BY precio_unitario DESC")) {
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

    /**
     * Lista productos/servicios activos ordenados por precio ascendente.
     */
    public static List<String[]> listarProductosPorPrecioAsc() throws SQLException {
        List<String[]> productos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT id_serv, descripcion, precio_unitario, stock_actual FROM FN_LISTAR_SERVICIOS_PRODUCTOS() ORDER BY precio_unitario ASC")) {
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
}

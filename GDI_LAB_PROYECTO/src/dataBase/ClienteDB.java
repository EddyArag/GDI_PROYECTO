package dataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a datos para operaciones CRUD y consultas sobre la entidad Cliente.
 */
public class ClienteDB {

    /**
     * Lista todos los clientes activos usando la función FN_LISTAR_CLIENTES().
     * @return Lista de clientes (ID, nombre completo, RUC, observaciones).
     */
    public static List<String[]> listarClientes() throws SQLException {
        List<String[]> clientes = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt
                        .executeQuery("SELECT id_cli, nombre_completo, ruc, observaciones FROM FN_LISTAR_CLIENTES()")) {
            while (rs.next()) {
                clientes.add(new String[] {
                        String.valueOf(rs.getInt("id_cli")),
                        rs.getString("nombre_completo"),
                        rs.getString("ruc"),
                        rs.getString("observaciones")
                });
            }
        }
        return clientes;
    }

    /**
     * Inserta un nuevo cliente usando el procedimiento almacenado SP_INSERTAR_CLIENTE.
     * @return El ID generado para el nuevo cliente.
     */
    public static int insertarCliente(String p_nomb, String ape_p, String ape_m, String ruc, String obs)
            throws SQLException {
        int nuevoId = -1;
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL SP_INSERTAR_CLIENTE(?, ?, ?, ?, ?, ?)")) {
            cs.setString(1, p_nomb); // VARCHAR(50)
            cs.setString(2, ape_p);  // VARCHAR(50)
            cs.setString(3, ape_m);  // VARCHAR(50)
            if (ruc == null || ruc.trim().isEmpty()) {
                cs.setNull(4, Types.CHAR); // CHAR(11) - manda null si no hay RUC
            } else {
                cs.setString(4, ruc);      // CHAR(11)
            }
            cs.setString(5, obs);    // VARCHAR(200)
            cs.registerOutParameter(6, Types.INTEGER); // OUT p_new_id_cli INT
            cs.execute();
            nuevoId = cs.getInt(6);
        }
        return nuevoId;
    }

    /**
     * Modifica los datos de un cliente existente usando SP_MODIFICAR_CLIENTE.
     */
    public static void modificarCliente(int id, String p_nomb, String ape_p, String ape_m, String ruc, String obs)
            throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL SP_MODIFICAR_CLIENTE(?, ?, ?, ?, ?, ?)")) {
            cs.setInt(1, id);        // INT
            cs.setString(2, p_nomb); // VARCHAR(50)
            cs.setString(3, ape_p);  // VARCHAR(50)
            cs.setString(4, ape_m);  // VARCHAR(50)
            cs.setString(5, ruc);    // CHAR(11)
            cs.setString(6, obs);    // VARCHAR(200)
            cs.execute();
        }
    }

    /**
     * Elimina lógicamente un cliente usando SP_ELIMINAR_LOGICO_CLIENTE.
     */
    public static void eliminarLogicoCliente(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL SP_ELIMINAR_LOGICO_CLIENTE(?)")) {
            cs.setInt(1, id);
            cs.execute();
        }
    }

    /**
     * Reactiva un cliente desactivado usando SP_REACTIVAR_CLIENTE.
     */
    public static void reactivarCliente(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("CALL SP_REACTIVAR_CLIENTE(?)")) {
            cs.setInt(1, id);
            cs.execute();
        }
    }

    /**
     * Obtiene las direcciones asociadas a un cliente usando FN_GET_DIRECCIONES_CLIENTE.
     * @return Lista de direcciones (ID, dirección).
     */
    public static List<String[]> getDireccionesCliente(int idCli) throws SQLException {
        List<String[]> direcciones = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn
                        .prepareStatement("SELECT id_dircli, direccion FROM FN_GET_DIRECCIONES_CLIENTE(?)")) {
            ps.setInt(1, idCli);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                direcciones.add(new String[] {
                        String.valueOf(rs.getInt("id_dircli")),
                        rs.getString("direccion")
                });
            }
        }
        return direcciones;
    }

    /**
     * Obtiene los teléfonos asociados a un cliente usando FN_GET_TELEFONOS_CLIENTE.
     * @return Lista de teléfonos (ID, teléfono).
     */
    public static List<String[]> getTelefonosCliente(int idCli) throws SQLException {
        List<String[]> telefonos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn
                        .prepareStatement("SELECT id_telcli, telefono FROM FN_GET_TELEFONOS_CLIENTE(?)")) {
            ps.setInt(1, idCli);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                telefonos.add(new String[] {
                        String.valueOf(rs.getInt("id_telcli")),
                        rs.getString("telefono")
                });
            }
        }
        return telefonos;
    }

    /**
     * Lista todos los clientes desactivados usando FN_LISTAR_CLIENTES_DESACTIVADOS().
     * @return Lista de clientes desactivados.
     */
    public static List<String[]> listarClientesDesactivados() throws SQLException {
        List<String[]> clientes = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT id_cli, nombre_completo, ruc, observaciones FROM FN_LISTAR_CLIENTES_DESACTIVADOS()")) {
            while (rs.next()) {
                clientes.add(new String[] {
                        String.valueOf(rs.getInt("id_cli")),
                        rs.getString("nombre_completo"),
                        rs.getString("ruc"),
                        rs.getString("observaciones")
                });
            }
        }
        return clientes;
    }
}

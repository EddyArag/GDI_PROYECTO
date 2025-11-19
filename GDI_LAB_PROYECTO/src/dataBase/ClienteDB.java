package dataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDB {

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

    public static void eliminarLogicoCliente(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("CALL SP_ELIMINAR_LOGICO_CLIENTE(?)")) {
            cs.setInt(1, id);
            cs.execute();
        }
    }

    public static void reactivarCliente(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("CALL SP_REACTIVAR_CLIENTE(?)")) {
            cs.setInt(1, id);
            cs.execute();
        }
    }

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

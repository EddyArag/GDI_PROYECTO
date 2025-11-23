package dataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a datos para operaciones CRUD y consultas sobre la entidad
 * Cliente.
 * Permite listar, buscar, agregar, modificar, eliminar y reactivar clientes.
 */
public class ClienteDB {

    /**
     * Lista todos los clientes activos usando la función FN_LISTAR_CLIENTES().
     * 
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
     * Inserta un nuevo cliente usando el procedimiento almacenado
     * SP_INSERTAR_CLIENTE.
     * 
     * @return El ID generado para el nuevo cliente.
     */
    public static int insertarCliente(String p_nomb, String ape_p, String ape_m, String ruc, String obs)
            throws SQLException {
        int nuevoId = -1;
        String rucLimpio = (ruc != null) ? ruc.trim() : null;
        if (rucLimpio != null && rucLimpio.isEmpty())
            rucLimpio = null;
        // System.out.println("DEBUG RUC/DNI insert: [" + rucLimpio + "]"); // <--
        // Quitar debug
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("CALL SP_INSERTAR_CLIENTE(?, ?, ?, ?, ?, ?)")) {
            cs.setString(1, p_nomb);
            cs.setString(2, ape_p);
            cs.setString(3, ape_m);
            if (rucLimpio == null) {
                cs.setNull(4, Types.CHAR);
            } else {
                if (!rucLimpio.matches("\\d{8}|\\d{11}")) {
                    throw new SQLException("El campo RUC/DNI debe contener solo números y tener 8 o 11 dígitos.");
                }
                cs.setString(4, rucLimpio);
            }
            cs.setString(5, obs);
            cs.registerOutParameter(6, Types.INTEGER);
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
        String rucLimpio = (ruc != null) ? ruc.trim() : null;
        if (rucLimpio != null && rucLimpio.isEmpty())
            rucLimpio = null;
        // System.out.println("DEBUG RUC/DNI update: [" + rucLimpio + "]"); // <--
        // Quitar debug
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("CALL SP_MODIFICAR_CLIENTE(?, ?, ?, ?, ?, ?)")) {
            cs.setInt(1, id);
            cs.setString(2, p_nomb);
            cs.setString(3, ape_p);
            cs.setString(4, ape_m);
            if (rucLimpio == null) {
                cs.setNull(5, Types.CHAR);
            } else {
                if (!rucLimpio.matches("\\d{8}|\\d{11}")) {
                    throw new SQLException("El campo RUC/DNI debe contener solo números y tener 8 o 11 dígitos.");
                }
                cs.setString(5, rucLimpio);
            }
            cs.setString(6, obs);
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
     * Obtiene las direcciones asociadas a un cliente usando
     * FN_GET_DIRECCIONES_CLIENTE.
     * 
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
     * 
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
     * Lista todos los clientes desactivados usando
     * FN_LISTAR_CLIENTES_DESACTIVADOS().
     * 
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

    /**
     * Busca clientes activos por nombre o RUC (filtro simple).
     * 
     * @param filtro Texto a buscar en nombre completo o RUC.
     * @return Lista de clientes filtrados.
     */
    public static List<String[]> buscarClientes(String filtro) throws SQLException {
        List<String[]> clientes = new ArrayList<>();
        String sql = "SELECT id_cli, TRIM(p_nomb || ' ' || COALESCE(ape_p, '') || ' ' || COALESCE(ape_m, '')) AS nombre_completo, ruc, obs AS observaciones "
                +
                "FROM Cliente WHERE ACTIVO = TRUE AND (" +
                "LOWER(p_nomb || ' ' || COALESCE(ape_p, '') || ' ' || COALESCE(ape_m, '')) LIKE ? OR " +
                "ruc LIKE ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + filtro.toLowerCase() + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ResultSet rs = ps.executeQuery();
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

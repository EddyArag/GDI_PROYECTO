package dataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a datos para operaciones sobre la entidad Empresa.
 */
public class EmpresaDB {

    /**
     * Modifica el logo de la empresa usando SP_MODIFICAR_LOGO_EMPRESA.
     */
    public static void modificarLogoEmpresa(int idEmp, byte[] logo) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("{ call SP_MODIFICAR_LOGO_EMPRESA(?, ?) }")) {
            cs.setInt(1, idEmp);
            cs.setBytes(2, logo);
            cs.execute();
        }
    }

    /**
     * Obtiene las direcciones asociadas a la empresa usando FN_GET_DIRECCIONES_EMPRESA.
     * @return Lista de direcciones (ID, dirección).
     */
    public static List<String[]> getDireccionesEmpresa(int idEmp) throws SQLException {
        List<String[]> direcciones = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn
                        .prepareStatement("SELECT id_diremp, direccion FROM FN_GET_DIRECCIONES_EMPRESA(?)")) {
            ps.setInt(1, idEmp);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                direcciones.add(new String[] {
                        String.valueOf(rs.getInt("id_diremp")),
                        rs.getString("direccion")
                });
            }
        }
        return direcciones;
    }

    /**
     * Obtiene los teléfonos asociados a la empresa usando FN_GET_TELEFONOS_EMPRESA.
     * @return Lista de teléfonos (ID, teléfono).
     */
    public static List<String[]> getTelefonosEmpresa(int idEmp) throws SQLException {
        List<String[]> telefonos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn
                        .prepareStatement("SELECT id_telemp, telefono FROM FN_GET_TELEFONOS_EMPRESA(?)")) {
            ps.setInt(1, idEmp);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                telefonos.add(new String[] {
                        String.valueOf(rs.getInt("id_telemp")),
                        rs.getString("telefono")
                });
            }
        }
        return telefonos;
    }

    /**
     * Obtiene los correos electrónicos asociados a la empresa usando FN_GET_MAILS_EMPRESA.
     * @return Lista de mails (ID, mail).
     */
    public static List<String[]> getMailsEmpresa(int idEmp) throws SQLException {
        List<String[]> mails = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT id_mailemp, mail FROM FN_GET_MAILS_EMPRESA(?)")) {
            ps.setInt(1, idEmp);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                mails.add(new String[] {
                        String.valueOf(rs.getInt("id_mailemp")),
                        rs.getString("mail")
                });
            }
        }
        return mails;
    }
}

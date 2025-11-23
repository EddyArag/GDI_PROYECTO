package dataBase;

import java.sql.*;

/**
 * Clase de utilidad para actualizar la tasa de IGV por defecto en la base de datos.
 */
public class IGVDB {

    /**
     * Actualiza la tasa de IGV por defecto usando el procedimiento almacenado SP_ACTUALIZAR_IGV_POR_DEFECTO.
     * @param nuevaTasa Nueva tasa de IGV (ejemplo: 0.18).
     */
    public static void actualizarIGVPorDefecto(double nuevaTasa) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("{ call SP_ACTUALIZAR_IGV_POR_DEFECTO(?) }")) {
            cs.setDouble(1, nuevaTasa);
            cs.execute();
        }
    }
}

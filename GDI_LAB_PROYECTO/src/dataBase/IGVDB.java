package dataBase;

import java.sql.*;

public class IGVDB {

    public static void actualizarIGVPorDefecto(double nuevaTasa) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                CallableStatement cs = conn.prepareCall("{ call SP_ACTUALIZAR_IGV_POR_DEFECTO(?) }")) {
            cs.setDouble(1, nuevaTasa);
            cs.execute();
        }
    }
}

package dataBase;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Clase utilitaria para crear las tablas principales y relaciones de la base de
 * datos.
 * Ejecuta los comandos SQL necesarios para inicializar la estructura del
 * esquema.
 */
public class CargaDb3 {
        /**
         * Ejecuta la creación de tablas, claves foráneas e índices en la base de datos.
         * 
         * @param conn Conexión activa a la base de datos.
         */
        public static void ejecutar(Connection conn) {
                try (Statement st = conn.createStatement()) {
                        // Tablas principales con eliminación lógica
                        st.executeUpdate(
                                        "CREATE TABLE IF NOT EXISTS Cliente (" +
                                                        "  ID_CLI SERIAL PRIMARY KEY," +
                                                        "  P_NOMB VARCHAR(100) NOT NULL," +
                                                        "  APE_P VARCHAR(50) NULL," +
                                                        "  APE_M VARCHAR(50)," +
                                                        "  RUC CHAR(11) UNIQUE," +
                                                        "  OBS VARCHAR(200)," +
                                                        "  ACTIVO BOOLEAN NOT NULL DEFAULT TRUE" +
                                                        ");");
                        st.executeUpdate(
                                        "CREATE TABLE IF NOT EXISTS Empresa (" +
                                                        "  ID_EMP SERIAL PRIMARY KEY," +
                                                        "  LOGO BYTEA NOT NULL" +
                                                        ");");
                        st.executeUpdate(
                                        "CREATE TABLE IF NOT EXISTS Servicio_Producto (" +
                                                        "  ID_SERV CHAR(4) PRIMARY KEY," +
                                                        "  DESCP VARCHAR(200) NOT NULL," +
                                                        "  PUNIT DECIMAL(7,2) CHECK (PUNIT > 0) NOT NULL," +
                                                        "  STOCK INT CHECK (STOCK>=0)," +
                                                        "  ACTIVO BOOLEAN NOT NULL DEFAULT TRUE" +
                                                        ");");
                        st.executeUpdate(
                                        "CREATE TABLE IF NOT EXISTS Cotizacion (" +
                                                        "  NCOT CHAR(10) PRIMARY KEY," +
                                                        "  ID_CLI INT NOT NULL," +
                                                        "  ID_EMP INT NOT NULL," +
                                                        "  FEMI DATE NOT NULL," +
                                                        "  DESCT DECIMAL(10,2) CHECK (DESCT >= 0) DEFAULT 0," +
                                                        "  IGV DECIMAL(5,2) NOT NULL DEFAULT 0.18," +
                                                        "  COND VARCHAR(100)," +
                                                        "  GARA VARCHAR(100) NOT NULL," +
                                                        "  TENT VARCHAR(50)," +
                                                        "  VOFER DATE," +
                                                        "  ACTIVO BOOLEAN NOT NULL DEFAULT TRUE" +
                                                        ");");
                        st.executeUpdate(
                                        "CREATE TABLE IF NOT EXISTS Cotizacion_Detalle (" +
                                                        "  ID_DET SERIAL," +
                                                        "  NCOT CHAR(10) NOT NULL," +
                                                        "  ID_SERV CHAR(4) NOT NULL," +
                                                        "  CANT INT CHECK (CANT > 0) NOT NULL," +
                                                        "  PRIMARY KEY (ID_DET, NCOT, ID_SERV)" +
                                                        ");");
                        st.executeUpdate(
                                        "CREATE TABLE IF NOT EXISTS Direccion_Cli (" +
                                                        "  ID_DIRCLI SERIAL," +
                                                        "  ID_CLI INT NOT NULL," +
                                                        "  DIR VARCHAR(100) NOT NULL," +
                                                        "  PRIMARY KEY (ID_DIRCLI, ID_CLI)" +
                                                        ");");
                        st.executeUpdate(
                                        "CREATE TABLE IF NOT EXISTS Telefono_Cli (" +
                                                        "  ID_TELCLI SERIAL," +
                                                        "  ID_CLI INT NOT NULL," +
                                                        "  TELE VARCHAR(12) NOT NULL," +
                                                        "  PRIMARY KEY (ID_TELCLI, ID_CLI)" +
                                                        ");");
                        st.executeUpdate(
                                        "CREATE TABLE IF NOT EXISTS Direccion_Emp (" +
                                                        "  ID_DIREMP SERIAL," +
                                                        "  ID_EMP INT NOT NULL," +
                                                        "  DIR VARCHAR(100) NOT NULL," +
                                                        "  PRIMARY KEY (ID_DIREMP, ID_EMP)" +
                                                        ");");
                        st.executeUpdate(
                                        "CREATE TABLE IF NOT EXISTS Telefono_Emp (" +
                                                        "  ID_TELEMP SERIAL," +
                                                        "  ID_EMP INT NOT NULL," +
                                                        "  TELE VARCHAR(12)," +
                                                        "  PRIMARY KEY (ID_TELEMP, ID_EMP)" +
                                                        ");");
                        st.executeUpdate(
                                        "CREATE TABLE IF NOT EXISTS Mail_Emp (" +
                                                        "  ID_MAILEMP SERIAL," +
                                                        "  ID_EMP INT NOT NULL," +
                                                        "  MAIL VARCHAR(50) NOT NULL," +
                                                        "  PRIMARY KEY (ID_MAILEMP, ID_EMP)" +
                                                        ");");
                        // Claves foráneas
                        st.executeUpdate(
                                        "DO $$ BEGIN " +
                                                        "IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_cot_cli') THEN "
                                                        +
                                                        "ALTER TABLE Cotizacion ADD CONSTRAINT fk_cot_cli FOREIGN KEY (ID_CLI) REFERENCES Cliente (ID_CLI) ON DELETE RESTRICT ON UPDATE CASCADE; END IF; END $$;");
                        st.executeUpdate(
                                        "DO $$ BEGIN " +
                                                        "IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_cot_emp') THEN "
                                                        +
                                                        "ALTER TABLE Cotizacion ADD CONSTRAINT fk_cot_emp FOREIGN KEY (ID_EMP) REFERENCES Empresa (ID_EMP) ON DELETE RESTRICT ON UPDATE CASCADE; END IF; END $$;");
                        st.executeUpdate(
                                        "DO $$ BEGIN " +
                                                        "IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_det_cot') THEN "
                                                        +
                                                        "ALTER TABLE Cotizacion_Detalle ADD CONSTRAINT fk_det_cot FOREIGN KEY (NCOT) REFERENCES Cotizacion (NCOT) ON DELETE CASCADE; END IF; END $$;");
                        st.executeUpdate(
                                        "DO $$ BEGIN " +
                                                        "IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_det_serv') THEN "
                                                        +
                                                        "ALTER TABLE Cotizacion_Detalle ADD CONSTRAINT fk_det_serv FOREIGN KEY (ID_SERV) REFERENCES Servicio_Producto (ID_SERV) ON DELETE RESTRICT; END IF; END $$;");
                        st.executeUpdate(
                                        "DO $$ BEGIN " +
                                                        "IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_dir_cli') THEN "
                                                        +
                                                        "ALTER TABLE Direccion_Cli ADD CONSTRAINT fk_dir_cli FOREIGN KEY (ID_CLI) REFERENCES Cliente (ID_CLI) ON DELETE CASCADE; END IF; END $$;");
                        st.executeUpdate(
                                        "DO $$ BEGIN " +
                                                        "IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_tel_cli') THEN "
                                                        +
                                                        "ALTER TABLE Telefono_Cli ADD CONSTRAINT fk_tel_cli FOREIGN KEY (ID_CLI) REFERENCES Cliente (ID_CLI) ON DELETE CASCADE; END IF; END $$;");
                        st.executeUpdate(
                                        "DO $$ BEGIN " +
                                                        "IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_dir_emp') THEN "
                                                        +
                                                        "ALTER TABLE Direccion_Emp ADD CONSTRAINT fk_dir_emp FOREIGN KEY (ID_EMP) REFERENCES Empresa (ID_EMP) ON DELETE CASCADE; END IF; END $$;");
                        st.executeUpdate(
                                        "DO $$ BEGIN " +
                                                        "IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_tel_emp') THEN "
                                                        +
                                                        "ALTER TABLE Telefono_Emp ADD CONSTRAINT fk_tel_emp FOREIGN KEY (ID_EMP) REFERENCES Empresa (ID_EMP) ON DELETE CASCADE; END IF; END $$;");
                        st.executeUpdate(
                                        "DO $$ BEGIN " +
                                                        "IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_mail_emp') THEN "
                                                        +
                                                        "ALTER TABLE Mail_Emp ADD CONSTRAINT fk_mail_emp FOREIGN KEY (ID_EMP) REFERENCES Empresa (ID_EMP) ON DELETE CASCADE; END IF; END $$;");
                        // Índices y extensión
                        st.executeUpdate("CREATE EXTENSION IF NOT EXISTS pg_trgm;");
                        st.executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS idx_cliente_ruc ON Cliente (RUC);");
                        st.executeUpdate(
                                        "CREATE INDEX IF NOT EXISTS idx_cliente_nombre_completo ON Cliente (P_NOMB, APE_P, APE_M);");
                        st.executeUpdate(
                                        "CREATE INDEX IF NOT EXISTS idx_sp_descripcion_trgm ON Servicio_Producto USING GIN (DESCP gin_trgm_ops);");
                        st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_cot_fecha_emision ON Cotizacion (FEMI);");
                        st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_cot_validez_oferta ON Cotizacion (VOFER);");
                        st.executeUpdate(
                                        "CREATE INDEX IF NOT EXISTS idx_cotdet_servicio ON Cotizacion_Detalle (ID_SERV);");
                } catch (Exception ex) {
                        // No mostrar nada al usuario
                }
        }
}

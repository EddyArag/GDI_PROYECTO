package dataBase;

import java.sql.Connection;
import java.sql.Statement;

public class CargaDb5 {
    public static void ejecutar(Connection conn) {
        try (Statement st = conn.createStatement()) {
            // ... CLIENTE
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_INSERTAR_CLIENTE(" +
                            "IN p_p_nomb VARCHAR(50), IN p_ape_p VARCHAR(50), IN p_ape_m VARCHAR(50), IN p_ruc CHAR(11), IN p_obs VARCHAR(200), OUT p_new_id_cli INT) "
                            +
                            "LANGUAGE plpgsql AS $$ " +
                            "BEGIN " +
                            "IF p_p_nomb IS NULL OR TRIM(p_p_nomb) = '' THEN " +
                            "RAISE EXCEPTION 'ERROR DE VALIDACIÓN: El Nombre Principal (Razón Social) es un campo obligatorio y no puede estar vacío.'; END IF; "
                            +
                            "IF p_ruc IS NULL AND (p_ape_p IS NULL OR TRIM(p_ape_p) = '') THEN " +
                            "RAISE EXCEPTION 'ERROR DE VALIDACIÓN: Para personas naturales (sin RUC), el Apellido Paterno es obligatorio.'; END IF; "
                            +
                            "INSERT INTO Cliente (P_NOMB, APE_P, APE_M, RUC, OBS, ACTIVO) VALUES (p_p_nomb, p_ape_p, p_ape_m, p_ruc, p_obs, TRUE) RETURNING ID_CLI INTO p_new_id_cli; "
                            +
                            "EXCEPTION WHEN unique_violation THEN " +
                            "RAISE EXCEPTION 'ERROR DE UNICIDAD: El número de RUC/DNI ingresado ya existe en la base de datos.'; "
                            +
                            "WHEN others THEN RAISE EXCEPTION '%', SQLERRM; END; $$;");
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_MODIFICAR_CLIENTE(" +
                            "IN p_id_cli INT, IN p_new_p_nomb VARCHAR(50), IN p_new_ape_p VARCHAR(50), IN p_new_ape_m VARCHAR(50), IN p_new_ruc CHAR(11), IN p_new_obs VARCHAR(200)) "
                            +
                            "LANGUAGE plpgsql AS $$ " +
                            "BEGIN " +
                            "IF p_new_p_nomb IS NULL OR TRIM(p_new_p_nomb) = '' THEN " +
                            "RAISE EXCEPTION 'ERROR DE VALIDACIÓN: El Nombre Principal (Razón Social) es un campo obligatorio y no puede estar vacío durante la modificación.'; END IF; "
                            +
                            "IF p_new_ruc IS NULL AND (p_new_ape_p IS NULL OR TRIM(p_new_ape_p) = '') THEN " +
                            "RAISE EXCEPTION 'ERROR DE VALIDACIÓN: Para personas naturales (sin RUC), el Apellido Paterno es obligatorio al modificar.'; END IF; "
                            +
                            "UPDATE Cliente SET P_NOMB = p_new_p_nomb, APE_P = p_new_ape_p, APE_M = p_new_ape_m, RUC = p_new_ruc, OBS = p_new_obs WHERE ID_CLI = p_id_cli; "
                            +
                            "EXCEPTION WHEN unique_violation THEN " +
                            "RAISE EXCEPTION 'ERROR DE UNICIDAD: El número de RUC/DNI ingresado ya existe en la base de datos (Error al modificar).'; "
                            +
                            "WHEN others THEN RAISE EXCEPTION '%', SQLERRM; END; $$;");
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_ELIMINAR_LOGICO_CLIENTE(IN p_id_cli INT) LANGUAGE sql AS $$ UPDATE Cliente SET ACTIVO = FALSE WHERE ID_CLI = p_id_cli; $$;");
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_AGREGAR_DIR_CLIENTE(IN p_id_cli INT, IN p_dir VARCHAR(100)) LANGUAGE sql AS $$ INSERT INTO Direccion_Cli (ID_CLI, DIR) VALUES (p_id_cli, p_dir); $$;");
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_MODIFICAR_DIR_CLIENTE(IN p_id_dircli INT, IN p_id_cli INT, IN p_new_dir VARCHAR(100)) LANGUAGE sql AS $$ UPDATE Direccion_Cli SET DIR = p_new_dir WHERE ID_DIRCLI = p_id_dircli AND ID_CLI = p_id_cli; $$;");
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_ELIMINAR_DIR_CLIENTE(IN p_id_dircli INT, IN p_id_cli INT) LANGUAGE sql AS $$ DELETE FROM Direccion_Cli WHERE ID_DIRCLI = p_id_dircli AND ID_CLI = p_id_cli; $$;");
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_AGREGAR_TEL_CLIENTE(IN p_id_cli INT, IN p_tele VARCHAR(12)) LANGUAGE sql AS $$ INSERT INTO Telefono_Cli (ID_CLI, TELE) VALUES (p_id_cli, p_tele); $$;");
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_MODIFICAR_TELEFONO_CLIENTE(" +
                            "IN p_id_cli INT, IN p_telefonos TEXT[] " +
                            ") LANGUAGE plpgsql AS $$ " +
                            "DECLARE v_telefono TEXT; v_clean_tele TEXT; BEGIN " +
                            "IF NOT EXISTS (SELECT 1 FROM Cliente WHERE ID_CLI = p_id_cli) THEN " +
                            "RAISE EXCEPTION 'El cliente con ID % no existe.', p_id_cli; END IF; " +
                            "DELETE FROM Telefono_Cli WHERE ID_CLI = p_id_cli; " +
                            "IF p_telefonos IS NOT NULL AND array_length(p_telefonos, 1) > 0 THEN " +
                            "FOREACH v_telefono IN ARRAY p_telefonos LOOP " +
                            "v_clean_tele := regexp_replace(v_telefono, '[^0-9\\+]', '', 'g'); " +
                            "IF LENGTH(v_clean_tele) > 12 THEN " +
                            "RAISE EXCEPTION 'ERROR DE VALIDACIÓN: El teléfono % excede la longitud máxima (12 caracteres).', v_telefono; END IF; "
                            +
                            "IF v_clean_tele ~ '^[0-9]+$' AND LENGTH(v_clean_tele) NOT IN (7, 8, 9) THEN " +
                            "RAISE EXCEPTION 'ERROR DE VALIDACIÓN: El formato telefónico peruano requiere 7, 8 (fijo) o 9 (móvil) dígitos.'; END IF; "
                            +
                            "INSERT INTO Telefono_Cli (ID_CLI, TELE) VALUES (p_id_cli, v_clean_tele); " +
                            "END LOOP; END IF; " +
                            "RAISE NOTICE 'Teléfonos del cliente con ID % modificados exitosamente.', p_id_cli; " +
                            "EXCEPTION WHEN OTHERS THEN " +
                            // Solo muestra el mensaje personalizado, sin SQLERRM ni contexto
                            "RAISE; " +
                            "END; $$;");
            // ... SERVICIO_PRODUCTO
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_INSERTAR_PRODUCTO(" +
                            "IN p_id_serv CHAR(4), IN p_descp VARCHAR(200), IN p_punit DECIMAL(7,2), IN p_stock INT) " +
                            "LANGUAGE plpgsql AS $$ " +
                            "BEGIN " +
                            "IF p_descp IS NULL OR TRIM(p_descp) = '' THEN " +
                            "RAISE EXCEPTION 'ERROR DE VALIDACIÓN: La Descripción del Servicio/Producto no puede estar vacía.'; END IF; "
                            +
                            "IF p_punit IS NULL OR p_punit <= 0 THEN " +
                            "RAISE EXCEPTION 'ERROR DE VALIDACIÓN: El Precio Unitario (PUNIT) debe ser un valor positivo y no puede ser nulo.'; END IF; "
                            +
                            "INSERT INTO Servicio_Producto (ID_SERV, DESCP, PUNIT, STOCK, ACTIVO) VALUES (p_id_serv, p_descp, p_punit, p_stock, TRUE); "
                            +
                            "EXCEPTION WHEN unique_violation THEN " +
                            "RAISE EXCEPTION 'ERROR DE UNICIDAD: El Código de Servicio/Producto (%) ya existe.', p_id_serv; "
                            +
                            "WHEN others THEN RAISE EXCEPTION 'Error al insertar producto: %', SQLERRM; END; $$;");
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_MODIFICAR_PRODUCTO(" +
                            "IN p_id_serv CHAR(4), IN p_new_descp VARCHAR(200), IN p_new_punit DECIMAL(7,2), IN p_new_stock INT) "
                            +
                            "LANGUAGE plpgsql AS $$ " +
                            "BEGIN " +
                            "IF p_new_descp IS NULL OR TRIM(p_new_descp) = '' THEN " +
                            "RAISE EXCEPTION 'ERROR DE VALIDACIÓN: La Descripción del Servicio/Producto no puede estar vacía.'; END IF; "
                            +
                            "IF p_new_punit IS NULL OR p_new_punit <= 0 THEN " +
                            "RAISE EXCEPTION 'ERROR DE VALIDACIÓN: El Precio Unitario (PUNIT) debe ser un valor positivo y no puede ser nulo.'; END IF; "
                            +
                            "UPDATE Servicio_Producto SET DESCP = p_new_descp, PUNIT = p_new_punit, STOCK = p_new_stock WHERE ID_SERV = p_id_serv; "
                            +
                            "EXCEPTION WHEN others THEN RAISE EXCEPTION 'Error al modificar producto %: %', p_id_serv, SQLERRM; END; $$;");
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_ELIMINAR_LOGICO_PRODUCTO(IN p_id_serv CHAR(4)) LANGUAGE sql AS $$ UPDATE Servicio_Producto SET ACTIVO = FALSE WHERE ID_SERV = p_id_serv; $$;");
            // ... COTIZACION
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_INSERTAR_CABECERA_COTIZACION(" +
                            "IN p_ncot CHAR(10), IN p_id_cli INT, IN p_id_emp INT, IN p_femi DATE, IN p_desct DECIMAL(10,2), IN p_cond VARCHAR(100), IN p_gara VARCHAR(100), IN p_tent VARCHAR(50), IN p_vofer DATE) "
                            +
                            "LANGUAGE plpgsql AS $$ " +
                            "BEGIN " +
                            "IF p_femi IS NULL THEN RAISE EXCEPTION 'ERROR DE VALIDACIÓN: La Fecha de Emisión (FEMI) es obligatoria.'; END IF; "
                            +
                            "IF p_femi > CURRENT_DATE THEN RAISE EXCEPTION 'ERROR DE VALIDACIÓN: La Fecha de Emisión no puede ser una fecha futura.'; END IF; "
                            +
                            "IF p_desct IS NULL OR p_desct < 0 THEN RAISE EXCEPTION 'ERROR DE VALIDACIÓN: El Descuento (DESCT) no puede ser un valor negativo.'; END IF; "
                            +
                            "IF p_vofer IS NOT NULL AND p_vofer < p_femi THEN RAISE EXCEPTION 'ERROR DE VALIDACIÓN: La Validez de Oferta (VOFER) no puede ser anterior a la Fecha de Emisión.'; END IF; "
                            +
                            "INSERT INTO Cotizacion (NCOT, ID_CLI, ID_EMP, FEMI, DESCT, IGV, COND, GARA, TENT, VOFER, ACTIVO) VALUES (p_ncot, p_id_cli, p_id_emp, p_femi, p_desct, 0.18, p_cond, p_gara, p_tent, p_vofer, TRUE); "
                            +
                            "EXCEPTION WHEN unique_violation THEN RAISE EXCEPTION 'ERROR DE UNICIDAD: El número de Cotización (%) ya existe.', p_ncot; "
                            +
                            "WHEN others THEN RAISE EXCEPTION 'Error al insertar cotización: %', SQLERRM; END; $$;");

            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_MODIFICAR_CABECERA_COTIZACION(" +
                            "IN p_ncot CHAR(10), IN p_new_desct DECIMAL(10,2), IN p_new_cond VARCHAR(100), IN p_new_tent VARCHAR(50), IN p_new_vofer DATE) "
                            +
                            "LANGUAGE plpgsql AS $$ " +
                            "DECLARE v_femi DATE; BEGIN " +
                            "SELECT FEMI INTO v_femi FROM Cotizacion WHERE NCOT = p_ncot; " +
                            "IF p_new_desct IS NULL OR p_new_desct < 0 THEN RAISE EXCEPTION 'ERROR DE VALIDACIÓN: El Descuento (DESCT) no puede ser un valor negativo.'; END IF; "
                            +
                            "IF p_new_vofer IS NOT NULL AND v_femi IS NOT NULL AND p_new_vofer < v_femi THEN RAISE EXCEPTION 'ERROR DE VALIDACIÓN: La Validez de Oferta (VOFER) no puede ser anterior a la Fecha de Emisión original (%).', v_femi; END IF; "
                            +
                            "UPDATE Cotizacion SET DESCT = p_new_desct, COND = p_new_cond, TENT = p_new_tent, VOFER = p_new_vofer WHERE NCOT = p_ncot; "
                            +
                            "EXCEPTION WHEN others THEN RAISE EXCEPTION 'Error al modificar cabecera de cotización %: %', p_ncot, SQLERRM; END; $$;");

            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_ELIMINAR_LOGICO_COTIZACION(IN p_ncot CHAR(10)) LANGUAGE sql AS $$ UPDATE Cotizacion SET ACTIVO = FALSE WHERE NCOT = p_ncot; $$;");

            // C.1 AUXILIARES DE DETALLE (ELIMINACIÓN Y MODIFICACIÓN DE LÍNEAS)
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_AGREGAR_DETALLE(IN p_ncot CHAR(10), IN p_id_serv CHAR(4), IN p_cant INT) LANGUAGE plpgsql AS $$ BEGIN INSERT INTO Cotizacion_Detalle (NCOT, ID_SERV, CANT) VALUES (p_ncot, p_id_serv, p_cant); END; $$;");
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_MODIFICAR_DETALLE(IN p_id_det INT, IN p_ncot CHAR(10), IN p_new_id_serv CHAR(4), IN p_new_cant INT) LANGUAGE plpgsql AS $$ BEGIN UPDATE Cotizacion_Detalle SET ID_SERV = p_new_id_serv, CANT = p_new_cant WHERE ID_DET = p_id_det AND NCOT = p_ncot; END; $$;");
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_ELIMINAR_DETALLE(IN p_id_det INT, IN p_ncot CHAR(10)) LANGUAGE sql AS $$ DELETE FROM Cotizacion_Detalle WHERE ID_DET = p_id_det AND NCOT = p_ncot; $$;");

            // D. PROCEDIMIENTOS DE EMPRESA -------------------------
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_MODIFICAR_LOGO_EMPRESA(IN p_id_emp INT, IN p_new_logo BYTEA) LANGUAGE sql AS $$ UPDATE Empresa SET LOGO = p_new_logo WHERE ID_EMP = p_id_emp; $$;");
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_AGREGAR_DIR_EMPRESA(IN p_id_emp INT, IN p_dir VARCHAR(100)) LANGUAGE sql AS $$ INSERT INTO Direccion_Emp (ID_EMP, DIR) VALUES (p_id_emp, p_dir); $$;");
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_MODIFICAR_DIR_EMPRESA(IN p_id_diremp INT, IN p_id_emp INT, IN p_new_dir VARCHAR(100)) LANGUAGE sql AS $$ UPDATE Direccion_Emp SET DIR = p_new_dir WHERE ID_DIREMP = p_id_diremp AND ID_EMP = p_id_emp; $$;");
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_AGREGAR_TEL_EMPRESA(IN p_id_emp INT, IN p_tele VARCHAR(12)) LANGUAGE sql AS $$ INSERT INTO Telefono_Emp (ID_EMP, TELE) VALUES (p_id_emp, p_tele); $$;");
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_AGREGAR_MAIL_EMPRESA(IN p_id_emp INT, IN p_mail VARCHAR(50)) LANGUAGE sql AS $$ INSERT INTO Mail_Emp (ID_EMP, MAIL) VALUES (p_id_emp, p_mail); $$;");

            // REACTIVAR
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_REACTIVAR_CLIENTE(IN p_id_cli INT) LANGUAGE sql AS $$ UPDATE Cliente SET ACTIVO = TRUE WHERE ID_CLI = p_id_cli; $$;");
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_REACTIVAR_PRODUCTO(IN p_id_serv CHAR(4)) LANGUAGE sql AS $$ UPDATE Servicio_Producto SET ACTIVO = TRUE WHERE ID_SERV = p_id_serv; $$;");
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_REACTIVAR_COTIZACION(IN p_ncot CHAR(10)) LANGUAGE sql AS $$ UPDATE Cotizacion SET ACTIVO = TRUE WHERE NCOT = p_ncot; $$;");

            // IGV
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_ACTUALIZAR_IGV_POR_DEFECTO(IN p_new_igv_tasa DECIMAL(5,2)) LANGUAGE plpgsql AS $$ BEGIN EXECUTE 'ALTER TABLE Cotizacion ALTER COLUMN IGV SET DEFAULT ' || p_new_igv_tasa; RAISE NOTICE 'El valor por defecto del IGV ha sido actualizado a %', p_new_igv_tasa; END; $$;");

            // PROCEDIMIENTO MAESTRO DE INSERCIÓN
            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_CREAR_COTIZACION_COMPLETA(" +
                            "IN p_ncot CHAR(10), IN p_id_cli INT, IN p_id_emp INT, IN p_femi DATE, IN p_desct DECIMAL(10,2), IN p_cond VARCHAR(100), IN p_gara VARCHAR(100), IN p_tent VARCHAR(50), IN p_vofer DATE, IN p_detalles_text text[]) "
                            +
                            "LANGUAGE plpgsql AS $$ " +
                            "DECLARE v_detalle tipo_detalle_cotizacion; v_detalles tipo_detalle_cotizacion[]; " +
                            "BEGIN v_detalles := ARRAY(SELECT t::tipo_detalle_cotizacion FROM unnest(p_detalles_text) AS t); "
                            +
                            "INSERT INTO Cotizacion (NCOT, ID_CLI, ID_EMP, FEMI, DESCT, IGV, COND, GARA, TENT, VOFER, ACTIVO) VALUES (p_ncot, p_id_cli, p_id_emp, p_femi, p_desct, 0.18, p_cond, p_gara, p_tent, p_vofer, TRUE); "
                            +
                            "FOREACH v_detalle IN ARRAY v_detalles LOOP " +
                            "INSERT INTO Cotizacion_Detalle (NCOT, ID_SERV, CANT) VALUES (p_ncot, v_detalle.id_serv_in, v_detalle.cant_in); "
                            +
                            "END LOOP; END; $$;");

            st.executeUpdate(
                    "CREATE OR REPLACE PROCEDURE SP_ELIMINAR_DETALLES_POR_NCOT(IN p_ncot CHAR(10)) LANGUAGE sql AS $$ DELETE FROM Cotizacion_Detalle WHERE NCOT = p_ncot; $$;");
        } catch (Exception ex) {
            // No mostrar nada al usuario
        }
    }
}

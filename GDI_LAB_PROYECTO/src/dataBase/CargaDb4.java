package dataBase;

import java.sql.Connection;
import java.sql.Statement;

public class CargaDb4 {
    public static void ejecutar(Connection conn) {
        try (Statement st = conn.createStatement()) {
            // Tipo de dato para Inserción Maestra de Cotización
            st.executeUpdate(
                "DO $$ BEGIN " +
                "IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'tipo_detalle_cotizacion') THEN " +
                "CREATE TYPE tipo_detalle_cotizacion AS (id_serv_in CHAR(4), cant_in INT); " +
                "END IF; END $$;"
            );

            // FUNCIÓN: CALCULAR_SUBTOTAL_COTIZACION
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION CALCULAR_SUBTOTAL_COTIZACION(p_ncot CHAR(10)) " +
                "RETURNS NUMERIC AS $$ " +
                "BEGIN " +
                "    RETURN (SELECT COALESCE(SUM(CD.CANT * SP.PUNIT), 0) FROM Cotizacion_Detalle CD JOIN Servicio_Producto SP ON CD.ID_SERV = SP.ID_SERV WHERE CD.NCOT = p_ncot); " +
                "END; $$ LANGUAGE plpgsql;"
            );

            // FUNCIÓN: CALCULAR_TOTAL_COTIZACION
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION CALCULAR_TOTAL_COTIZACION(p_ncot CHAR(10)) " +
                "RETURNS NUMERIC AS $$ " +
                "DECLARE " +
                "    v_subtotal NUMERIC; " +
                "    v_descuento NUMERIC; " +
                "    v_igv_tasa DECIMAL(5,2); " +
                "    v_total_calculado NUMERIC; " +
                "BEGIN " +
                "    v_subtotal := CALCULAR_SUBTOTAL_COTIZACION(p_ncot); " +
                "    SELECT C.DESCT, C.IGV INTO v_descuento, v_igv_tasa FROM Cotizacion C WHERE C.NCOT = p_ncot; " +
                "    v_total_calculado := ROUND((v_subtotal - COALESCE(v_descuento, 0)) * (1 + v_igv_tasa), 2); " +
                "    RETURN v_total_calculado; " +
                "EXCEPTION WHEN NO_DATA_FOUND THEN RETURN 0; " +
                "END; $$ LANGUAGE plpgsql;"
            );

            // FN_LISTAR_CLIENTES
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION FN_LISTAR_CLIENTES() " +
                "RETURNS TABLE (id_cli INT, nombre_completo VARCHAR(152), ruc CHAR(11), observaciones VARCHAR(200)) " +
                "LANGUAGE sql AS $$ " +
                "SELECT C.ID_CLI, TRIM(C.P_NOMB || ' ' || COALESCE(C.APE_P, '') || ' ' || COALESCE(C.APE_M, '')), C.RUC, C.OBS FROM Cliente C WHERE C.ACTIVO = TRUE ORDER BY C.P_NOMB; $$;"
            );

            // FN_LISTAR_SERVICIOS_PRODUCTOS
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION FN_LISTAR_SERVICIOS_PRODUCTOS() " +
                "RETURNS TABLE (id_serv CHAR(4), descripcion VARCHAR(200), precio_unitario DECIMAL(7,2), stock_actual INT) " +
                "LANGUAGE sql AS $$ " +
                "SELECT SP.ID_SERV, SP.DESCP, SP.PUNIT, SP.STOCK FROM Servicio_Producto SP WHERE SP.ACTIVO = TRUE ORDER BY SP.ID_SERV; $$;"
            );

            // FN_LISTAR_COTIZACIONES
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION FN_LISTAR_COTIZACIONES() " +
                "RETURNS TABLE (ncot CHAR(10), femi DATE, cliente_id INT, garantia VARCHAR(100)) " +
                "LANGUAGE sql AS $$ " +
                "SELECT C.NCOT, C.FEMI, C.ID_CLI, C.GARA FROM Cotizacion C WHERE C.ACTIVO = TRUE ORDER BY C.FEMI DESC; $$;"
            );

            // FN_RESUMEN_CABECERA_COTIZACION
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION FN_RESUMEN_CABECERA_COTIZACION(p_ncot CHAR(10)) " +
                "RETURNS TABLE (ncot CHAR(10), femi DATE, id_cli INT, desct DECIMAL(10,2), igv DECIMAL(5,2), subtotal NUMERIC, total NUMERIC) " +
                "LANGUAGE sql AS $$ " +
                "SELECT C.NCOT, C.FEMI, C.ID_CLI, C.DESCT, C.IGV, CALCULAR_SUBTOTAL_COTIZACION(C.NCOT), CALCULAR_TOTAL_COTIZACION(C.NCOT) FROM Cotizacion C WHERE C.NCOT = p_ncot; $$;"
            );

            // FN_LINEAS_COTIZACION
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION FN_LINEAS_COTIZACION(p_ncot CHAR(10)) " +
                "RETURNS TABLE (id_det INT, id_serv CHAR(4), descp VARCHAR(200), punit DECIMAL(7,2), cant INT, linea_total NUMERIC) " +
                "LANGUAGE sql AS $$ " +
                "SELECT CD.ID_DET, CD.ID_SERV, SP.DESCP, SP.PUNIT, CD.CANT, (SP.PUNIT * CD.CANT)::NUMERIC(12,2) FROM Cotizacion_Detalle CD JOIN Servicio_Producto SP ON CD.ID_SERV = SP.ID_SERV WHERE CD.NCOT = p_ncot ORDER BY CD.ID_DET; $$;"
            );

            // FN_BUSCAR_PRODUCTOS_POR_TEXTO
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION FN_BUSCAR_PRODUCTOS_POR_TEXTO(p_termino VARCHAR) " +
                "RETURNS TABLE (id_serv CHAR(4), descp VARCHAR(200), punit DECIMAL(7,2), stock INT) " +
                "LANGUAGE sql AS $$ " +
                "SELECT ID_SERV, DESCP, PUNIT, STOCK FROM Servicio_Producto WHERE DESCP ILIKE '%' || p_termino || '%' ORDER BY similarity(DESCP, p_termino) DESC LIMIT 50; $$;"
            );

            // FN_REPORTE_STOCK_DISPONIBLE
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION FN_REPORTE_STOCK_DISPONIBLE() " +
                "RETURNS TABLE (id_serv CHAR(4), descp VARCHAR(200), stock INT, reservado BIGINT, disponible BIGINT) " +
                "LANGUAGE sql AS $$ " +
                "SELECT SP.ID_SERV, SP.DESCP, SP.STOCK, COALESCE(SUM(CD.CANT) FILTER (WHERE C.VOFER IS NULL OR C.VOFER >= CURRENT_DATE), 0) AS reservado, SP.STOCK - COALESCE(SUM(CD.CANT) FILTER (WHERE C.VOFER IS NULL OR C.VOFER >= CURRENT_DATE), 0) AS disponible FROM Servicio_Producto SP LEFT JOIN Cotizacion_Detalle CD ON CD.ID_SERV = SP.ID_SERV LEFT JOIN Cotizacion C ON C.NCOT = CD.NCOT GROUP BY SP.ID_SERV, SP.DESCP, SP.STOCK ORDER BY disponible ASC; $$;"
            );

            // FN_TOP_PRODUCTOS_COTIZADOS
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION FN_TOP_PRODUCTOS_COTIZADOS(p_limite INT) " +
                "RETURNS TABLE (id_serv CHAR(4), descp VARCHAR(200), total_cant BIGINT, valor_estimado NUMERIC) " +
                "LANGUAGE sql AS $$ " +
                "SELECT CD.ID_SERV, SP.DESCP, SUM(CD.CANT) AS total_cant, SUM(CD.CANT * SP.PUNIT)::NUMERIC(14,2) AS valor_estimado FROM Cotizacion_Detalle CD JOIN Servicio_Producto SP ON CD.ID_SERV = SP.ID_SERV GROUP BY CD.ID_SERV, SP.DESCP ORDER BY total_cant DESC LIMIT p_limite; $$;"
            );

            // 6. FN_HISTORIAL_COTIZACIONES_CLIENTE
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION FN_HISTORIAL_COTIZACIONES_CLIENTE(p_id_cli INT) " +
                "RETURNS TABLE (ncot CHAR(10), femi DATE, items BIGINT, subtotal NUMERIC, total NUMERIC) " +
                "LANGUAGE sql AS $$ " +
                "SELECT C.NCOT, C.FEMI, COUNT(CD.ID_DET) AS items, CALCULAR_SUBTOTAL_COTIZACION(C.NCOT), CALCULAR_TOTAL_COTIZACION(C.NCOT) " +
                "FROM Cotizacion C LEFT JOIN Cotizacion_Detalle CD ON CD.NCOT = C.NCOT " +
                "WHERE C.ID_CLI = p_id_cli GROUP BY C.NCOT, C.FEMI ORDER BY C.FEMI DESC; $$;"
            );

            // 7. FN_ALERTAS_VENCIMIENTO
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION FN_ALERTAS_VENCIMIENTO(p_dias_hasta_vencimiento INT) " +
                "RETURNS TABLE (ncot CHAR(10), id_cli INT, femi DATE, vofer DATE, dias_restantes INT) " +
                "LANGUAGE sql AS $$ " +
                "SELECT NCOT, ID_CLI, FEMI, VOFER, (VOFER - CURRENT_DATE) AS dias_restantes " +
                "FROM Cotizacion " +
                "WHERE VOFER IS NOT NULL AND VOFER BETWEEN CURRENT_DATE AND (CURRENT_DATE + (p_dias_hasta_vencimiento || ' days')::interval) " +
                "ORDER BY VOFER; $$;"
            );

            // 8. FN_RESUMEN_MENSUAL_TOTALES
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION FN_RESUMEN_MENSUAL_TOTALES(p_fecha_inicio DATE, p_fecha_fin DATE) " +
                "RETURNS TABLE (mes DATE, num_cotizaciones BIGINT, total_mes NUMERIC) " +
                "LANGUAGE sql AS $$ " +
                "SELECT date_trunc('month', FEMI)::date AS mes, COUNT(*) AS num_cotizaciones, SUM(CALCULAR_TOTAL_COTIZACION(NCOT))::NUMERIC(14,2) AS total_mes " +
                "FROM Cotizacion WHERE FEMI BETWEEN p_fecha_inicio AND p_fecha_fin GROUP BY mes ORDER BY mes; $$;"
            );

            // 9. FN_TOP_CLIENTES_POR_GASTO
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION FN_TOP_CLIENTES_POR_GASTO(p_limite INT) " +
                "RETURNS TABLE (id_cli INT, cliente VARCHAR(152), num_cot BIGINT, total_estimado NUMERIC) " +
                "LANGUAGE sql AS $$ " +
                "SELECT C.ID_CLI, TRIM(CL.P_NOMB || ' ' || COALESCE(CL.APE_P,'') || ' ' || COALESCE(CL.APE_M,'')), COUNT(C.NCOT) AS num_cot, SUM(CALCULAR_TOTAL_COTIZACION(C.NCOT))::NUMERIC(14,2) AS total_estimado " +
                "FROM Cotizacion C JOIN Cliente CL ON CL.ID_CLI = C.ID_CLI GROUP BY C.ID_CLI, 2 ORDER BY total_estimado DESC LIMIT p_limite; $$;"
            );

            // 10. FN_VERIFICAR_INTEGRIDAD_DETALLE
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION FN_VERIFICAR_INTEGRIDAD_DETALLE() " +
                "RETURNS TABLE (id_det INT, ncot CHAR(10), id_serv CHAR(4), cant INT) " +
                "LANGUAGE sql AS $$ " +
                "SELECT CD.ID_DET, CD.NCOT, CD.ID_SERV, CD.CANT FROM Cotizacion_Detalle CD " +
                "LEFT JOIN Servicio_Producto SP ON SP.ID_SERV = CD.ID_SERV " +
                "LEFT JOIN Cotizacion C ON C.NCOT = CD.NCOT " +
                "WHERE SP.ID_SERV IS NULL OR C.NCOT IS NULL; $$;"
            );

            // texto_a_detalle_cotizacion
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION texto_a_detalle_cotizacion(text[]) " +
                "RETURNS tipo_detalle_cotizacion[] AS $$ " +
                "DECLARE arr tipo_detalle_cotizacion[]; i integer; " +
                "BEGIN arr := ARRAY[]::tipo_detalle_cotizacion[]; " +
                "FOR i IN array_lower($1,1)..array_upper($1,1) LOOP arr := arr || ($1[i]::tipo_detalle_cotizacion); END LOOP; " +
                "RETURN arr; END; $$ LANGUAGE plpgsql;"
            );

            // FN_GET_DIRECCIONES_CLIENTE
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION FN_GET_DIRECCIONES_CLIENTE(p_id_cli INT) " +
                "RETURNS TABLE (id_dircli INT, direccion VARCHAR(100)) " +
                "LANGUAGE sql AS $$ " +
                "SELECT DC.ID_DIRCLI, DC.DIR FROM Direccion_Cli DC WHERE DC.ID_CLI = p_id_cli ORDER BY DC.ID_DIRCLI; $$;"
            );

            // FN_GET_TELEFONOS_CLIENTE
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION FN_GET_TELEFONOS_CLIENTE(p_id_cli INT) " +
                "RETURNS TABLE (id_telcli INT, telefono VARCHAR(12)) " +
                "LANGUAGE sql AS $$ " +
                "SELECT TC.ID_TELCLI, TC.TELE FROM Telefono_Cli TC WHERE TC.ID_CLI = p_id_cli ORDER BY TC.ID_TELCLI; $$;"
            );

            // FN_GET_DIRECCIONES_EMPRESA
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION FN_GET_DIRECCIONES_EMPRESA(p_id_emp INT) " +
                "RETURNS TABLE (id_diremp INT, direccion VARCHAR(100)) " +
                "LANGUAGE sql AS $$ " +
                "SELECT DE.ID_DIREMP, DE.DIR FROM Direccion_Emp DE WHERE DE.ID_EMP = p_id_emp ORDER BY DE.ID_DIREMP; $$;"
            );

            // FN_GET_TELEFONOS_EMPRESA
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION FN_GET_TELEFONOS_EMPRESA(p_id_emp INT) " +
                "RETURNS TABLE (id_telemp INT, telefono VARCHAR(12)) " +
                "LANGUAGE sql AS $$ " +
                "SELECT TE.ID_TELEMP, TE.TELE FROM Telefono_Emp TE WHERE TE.ID_EMP = p_id_emp ORDER BY TE.ID_TELEMP; $$;"
            );

            // FN_GET_MAILS_EMPRESA
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION FN_GET_MAILS_EMPRESA(p_id_emp INT) " +
                "RETURNS TABLE (id_mailemp INT, mail VARCHAR(50)) " +
                "LANGUAGE sql AS $$ " +
                "SELECT ME.ID_MAILEMP, ME.MAIL FROM Mail_Emp ME WHERE ME.ID_EMP = p_id_emp ORDER BY ME.ID_MAILEMP; $$;"
            );

            // FN_LISTAR_CLIENTES_DESACTIVADOS
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION FN_LISTAR_CLIENTES_DESACTIVADOS() " +
                "RETURNS TABLE (id_cli INT, nombre_completo VARCHAR(152), ruc CHAR(11), observaciones VARCHAR(200)) " +
                "LANGUAGE sql AS $$ " +
                "SELECT C.ID_CLI, TRIM(C.P_NOMB || ' ' || COALESCE(C.APE_P, '') || ' ' || COALESCE(C.APE_M, '')), C.RUC, C.OBS FROM Cliente C WHERE C.ACTIVO = FALSE ORDER BY C.P_NOMB; $$;"
            );

            // FN_LISTAR_SERVICIOS_PRODUCTOS_DESACTIVADOS
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION FN_LISTAR_SERVICIOS_PRODUCTOS_DESACTIVADOS() " +
                "RETURNS TABLE (id_serv CHAR(4), descripcion VARCHAR(200), precio_unitario DECIMAL(7,2), stock_actual INT) " +
                "LANGUAGE sql AS $$ " +
                "SELECT SP.ID_SERV, SP.DESCP, SP.PUNIT, SP.STOCK FROM Servicio_Producto SP WHERE SP.ACTIVO = FALSE ORDER BY SP.ID_SERV; $$;"
            );

            // FN_LISTAR_COTIZACIONES_DESACTIVADAS
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION FN_LISTAR_COTIZACIONES_DESACTIVADAS() " +
                "RETURNS TABLE (ncot CHAR(10), femi DATE, cliente_id INT, garantia VARCHAR(100)) " +
                "LANGUAGE sql AS $$ " +
                "SELECT C.NCOT, C.FEMI, C.ID_CLI, C.GARA FROM Cotizacion C WHERE C.ACTIVO = FALSE ORDER BY C.FEMI DESC; $$;"
            );

            // TRIGGER: F_VALIDAR_STOCK
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION F_VALIDAR_STOCK() " +
                "RETURNS TRIGGER AS $$ " +
                "DECLARE v_stock_actual INT; " +
                "BEGIN " +
                "IF NEW.ID_SERV LIKE 'P%' THEN " +
                "SELECT STOCK INTO v_stock_actual FROM Servicio_Producto WHERE ID_SERV = NEW.ID_SERV; " +
                "IF NEW.CANT > v_stock_actual THEN " +
                "RAISE EXCEPTION 'ERROR DE INVENTARIO: La cantidad solicitada (%) excede el stock disponible (%) para el producto %', NEW.CANT, v_stock_actual, NEW.ID_SERV; " +
                "END IF; END IF; RETURN NEW; END; $$ LANGUAGE plpgsql;"
            );
            st.executeUpdate(
                "DROP TRIGGER IF EXISTS TR_VALIDAR_STOCK ON Cotizacion_Detalle;"
            );
            st.executeUpdate(
                "CREATE TRIGGER TR_VALIDAR_STOCK " +
                "BEFORE INSERT OR UPDATE ON Cotizacion_Detalle " +
                "FOR EACH ROW EXECUTE FUNCTION F_VALIDAR_STOCK();"
            );

            // FUNCIÓN DEL TRIGGER: F_VALIDAR_EMAIL
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION F_VALIDAR_EMAIL() " +
                "RETURNS TRIGGER AS $$ " +
                "BEGIN " +
                "IF NEW.MAIL !~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$' THEN " +
                "RAISE EXCEPTION 'ERROR DE FORMATO: El correo electrónico (%) no es válido.', NEW.MAIL; " +
                "END IF; " +
                "RETURN NEW; " +
                "END; $$ LANGUAGE plpgsql;"
            );
            st.executeUpdate(
                "DROP TRIGGER IF EXISTS TR_VALIDAR_EMAIL ON Mail_Emp;"
            );
            st.executeUpdate(
                "CREATE TRIGGER TR_VALIDAR_EMAIL " +
                "BEFORE INSERT OR UPDATE ON Mail_Emp " +
                "FOR EACH ROW EXECUTE FUNCTION F_VALIDAR_EMAIL();"
            );

            // FUNCIÓN DEL TRIGGER: F_NORMALIZAR_STOCK
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION F_NORMALIZAR_STOCK() " +
                "RETURNS TRIGGER AS $$ " +
                "BEGIN " +
                "IF NEW.STOCK IS NULL THEN NEW.STOCK := 0; " +
                "ELSIF NEW.STOCK < 0 THEN NEW.STOCK := 0; END IF; " +
                "RETURN NEW; END; $$ LANGUAGE plpgsql;"
            );
            // Puedes crear el trigger si lo necesitas:
            // st.executeUpdate("DROP TRIGGER IF EXISTS TR_NORMALIZAR_STOCK ON Servicio_Producto;");
            // st.executeUpdate("CREATE TRIGGER TR_NORMALIZAR_STOCK BEFORE INSERT OR UPDATE ON Servicio_Producto FOR EACH ROW EXECUTE FUNCTION F_NORMALIZAR_STOCK();");

            // FUNCIÓN DEL TRIGGER: F_VALIDAR_RUC_DNI
            st.executeUpdate(
                "CREATE OR REPLACE FUNCTION F_VALIDAR_RUC_DNI() " +
                "RETURNS TRIGGER AS $$ " +
                "DECLARE v_ruc_length INT; " +
                "BEGIN " +
                "IF NEW.RUC IS NOT NULL AND NEW.RUC <> '' THEN " +
                "NEW.RUC := TRIM(NEW.RUC); v_ruc_length := LENGTH(NEW.RUC); " +
                "IF NEW.RUC !~ '^[0-9]+$' THEN " +
                "RAISE EXCEPTION 'ERROR DE VALIDACIÓN (RUC/DNI): El campo RUC/DNI debe contener solo números.'; END IF; " +
                "IF v_ruc_length <> 8 AND v_ruc_length <> 11 THEN " +
                "RAISE EXCEPTION 'ERROR DE VALIDACIÓN (RUC/DNI): El número de RUC/DNI debe ser de 8 o 11 dígitos, pero se ingresaron %.', v_ruc_length; END IF; END IF; " +
                "RETURN NEW; END; $$ LANGUAGE plpgsql;"
            );
            st.executeUpdate(
                "DROP TRIGGER IF EXISTS TR_VALIDAR_RUC_CLIENTE ON Cliente;"
            );
            st.executeUpdate(
                "CREATE TRIGGER TR_VALIDAR_RUC_CLIENTE " +
                "BEFORE INSERT OR UPDATE OF RUC ON Cliente " +
                "FOR EACH ROW EXECUTE FUNCTION F_VALIDAR_RUC_DNI();"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

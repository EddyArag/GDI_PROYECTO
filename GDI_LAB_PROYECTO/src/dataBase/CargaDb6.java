package dataBase;

import java.sql.Connection;
import java.sql.Statement;

public class CargaDb6 {
    public static void ejecutar(Connection conn) {
        try (Statement st = conn.createStatement()) {
            // Para insertar un logo hexadecimal muy grande, puedes dividirlo en partes y concatenar en SQL.
            // Ejemplo: Supón que tienes 15 partes: hex1, hex2, ..., hex15 (todas String).
            // Reemplaza 'hex1', 'hex2', ... por tus partes reales.
            String hex1 = "parte1...";
            String hex2 = "parte2...";
            // ... String hex3 = "..."; ... hasta hex15
            String hex15 = "parte15...";

            String logoHex = hex1 + hex2 /* + hex3 + ... */ + hex15;

            st.executeUpdate(
                "INSERT INTO Empresa (ID_EMP, LOGO) VALUES " +
                "(1, decode('" + logoHex + "', 'hex'));"
            );
            // Direcciones de la Empresa
            st.executeUpdate(
                "INSERT INTO Direccion_Emp (ID_EMP, DIR) VALUES " +
                "(1, 'Samuel Velarde 201-C, Umacollo-Arequipa');"
            );
            // Teléfonos de la Empresa
            st.executeUpdate(
                "INSERT INTO Telefono_Emp (ID_EMP, TELE) VALUES " +
                "(1, '272976')," +
                "(1, '994459206');"
            );
            // Mails de la Empresa
            st.executeUpdate(
                "INSERT INTO Mail_Emp (ID_EMP, MAIL) VALUES " +
                "(1, 'info@innovasoft.com.pe');"
            );

            // Inserción de clientes y datos relacionados
            st.executeUpdate("BEGIN;");
            st.executeUpdate(
                "INSERT INTO Cliente (P_NOMB, APE_P, APE_M, RUC, OBS, ACTIVO) VALUES " +
                "('María', 'Torres', 'Soto', NULL, 'Cliente recurrente para equipos domésticos.', TRUE);"
            );
            st.executeUpdate(
                "INSERT INTO Direccion_Cli (ID_CLI, DIR) VALUES " +
                "(currval('cliente_id_cli_seq'), 'Av. Los Olivos 101, Cayma, Arequipa');"
            );
            st.executeUpdate(
                "INSERT INTO Telefono_Cli (ID_CLI, TELE) VALUES " +
                "(currval('cliente_id_cli_seq'), '987654321');"
            );
            st.executeUpdate(
                "INSERT INTO Cliente (P_NOMB, APE_P, APE_M, RUC, OBS, ACTIVO) VALUES " +
                "('Juan', 'Cáceres', 'Ríos', '10458923456', 'Cotización urgente para oficina.', TRUE);"
            );
            st.executeUpdate(
                "INSERT INTO Direccion_Cli (ID_CLI, DIR) VALUES " +
                "(currval('cliente_id_cli_seq'), 'Calle Puno 405, Cercado, Arequipa');"
            );
            st.executeUpdate(
                "INSERT INTO Telefono_Cli (ID_CLI, TELE) VALUES " +
                "(currval('cliente_id_cli_seq'), '999888777');"
            );
            st.executeUpdate(
                "INSERT INTO Cliente (P_NOMB, APE_P, APE_M, RUC, OBS, ACTIVO) VALUES " +
                "('SERVIRED', 'SOLUTIONS', 'E.I.R.L.', '20158974521', 'Necesitan servicios de red y cámaras.', TRUE);"
            );
            st.executeUpdate(
                "INSERT INTO Direccion_Cli (ID_CLI, DIR) VALUES " +
                "(currval('cliente_id_cli_seq'), 'Urb. Los Pinos, Mz J Lt 12, Miraflores');"
            );
            st.executeUpdate(
                "INSERT INTO Telefono_Cli (ID_CLI, TELE) VALUES " +
                "(currval('cliente_id_cli_seq'), '054332211');"
            );
            st.executeUpdate(
                "INSERT INTO Cliente (P_NOMB, APE_P, APE_M, RUC, OBS, ACTIVO) VALUES " +
                "('Ana', 'Gómez', 'Flores', NULL, 'Busca una PC gamer de gama media.', TRUE);"
            );
            st.executeUpdate(
                "INSERT INTO Cliente (P_NOMB, APE_P, APE_M, RUC, OBS, ACTIVO) VALUES " +
                "('GESTION', 'TECNOLÓGICA', 'S.A.', '20459871230', 'Requerimiento de renovación de equipos.', TRUE);"
            );
            st.executeUpdate(
                "INSERT INTO Direccion_Cli (ID_CLI, DIR) VALUES " +
                "(currval('cliente_id_cli_seq'), 'Av. Ejército 700, Piso 5, Yanahuara');"
            );
            st.executeUpdate(
                "INSERT INTO Telefono_Cli (ID_CLI, TELE) VALUES " +
                "(currval('cliente_id_cli_seq'), '054456789');"
            );
            st.executeUpdate(
                "INSERT INTO Cliente (P_NOMB, APE_P, APE_M, RUC, OBS, ACTIVO) VALUES " +
                "('Pedro', 'Huamán', 'Choque', '10789456123', 'Servicio de mantenimiento preventivo.', TRUE);"
            );
            st.executeUpdate(
                "INSERT INTO Telefono_Cli (ID_CLI, TELE) VALUES " +
                "(currval('cliente_id_cli_seq'), '912345678');"
            );
            st.executeUpdate(
                "INSERT INTO Cliente (P_NOMB, APE_P, APE_M, RUC, OBS, ACTIVO) VALUES " +
                "('Sofía', 'Rojas', 'Cárdenas', NULL, 'Solicita solo un formateo de laptop.', TRUE);"
            );
            st.executeUpdate(
                "INSERT INTO Cliente (P_NOMB, APE_P, APE_M, RUC, OBS, ACTIVO) VALUES " +
                "('Miguel', 'Lazo', 'Pino', NULL, 'Interesado en una laptop empresarial.', TRUE);"
            );
            st.executeUpdate(
                "INSERT INTO Cliente (P_NOMB, APE_P, APE_M, RUC, OBS, ACTIVO) VALUES " +
                "('Soluciones', 'Globales', 'SRL', '20334455667', 'Cotización para proyecto de cámaras IP.', TRUE);"
            );
            st.executeUpdate(
                "INSERT INTO Telefono_Cli (ID_CLI, TELE) VALUES " +
                "(currval('cliente_id_cli_seq'), '900111222');"
            );
            st.executeUpdate(
                "INSERT INTO Cliente (P_NOMB, APE_P, APE_M, RUC, OBS, ACTIVO) VALUES " +
                "('Elena', 'Zapata', 'Pizarro', NULL, 'Compra de periféricos y accesorios.', TRUE);"
            );
            st.executeUpdate("COMMIT;");

            // Inserción de productos/servicios
            st.executeUpdate(
                "INSERT INTO Servicio_Producto (ID_SERV, DESCP, PUNIT, STOCK, ACTIVO) VALUES " +
                "('P001', 'Procesador AMD Ryzen 5 5600G, 3.90 / 4.4GHz, 6 Núcleos', 540.00, 25, TRUE)," +
                "('P002', 'Placa Madre MSI B550M-A PRO MAX II, Chipset AMD B450', 250.00, 18, TRUE)," +
                "('P003', 'Memoria RAM SODIMM DDR4 8GB 3200 MHz Team Group Elite', 80.00, 150, TRUE)," +
                "('P004', 'SSD M.2 NVMe 500GB Western Digital WD Green SN350', 150.00, 75, TRUE)," +
                "('P005', 'Case Antryx Elegeant 670 c/fuente 350W Real', 170.00, 30, TRUE)," +
                "('P006', 'Monitor LED 21.5\" Teros TE-2150N, 1920x1080 Full HD', 245.00, 40, TRUE)," +
                "('P007', 'Tarjeta Video NVIDIA RTX 3060 12GB', 1450.00, 10, TRUE)," +
                "('P008', 'Disco Duro HDD 1TB Seagate Barracuda', 130.00, 50, TRUE)," +
                "('S001', 'Instalación de Sistema Operativo, Drivers y utilitarios', 50.00, 999, TRUE)," +
                "('S002', 'Servicio de Ensamble y Configuración de Componentes de PC', 80.00, 999, TRUE)," +
                "('S003', 'Mantenimiento Preventivo de Laptop (Limpieza interna y cambio de pasta)', 65.00, 999, TRUE)," +
                "('S004', 'Instalación y configuración de Cámara IP (por unidad)', 75.00, 999, TRUE)," +
                "('S005', 'Cableado de Red UTP Cat. 6 (por metro)', 2.50, 999, TRUE)," +
                "('S006', 'Diagnóstico y Reparación Básica de Impresora (Excluye repuestos)', 120.00, 999, TRUE)," +
                "('S007', 'Asesoría y Campaña Inicial de Marketing Digital (Duración 8 horas)', 90.00, 999, TRUE);"
            );

            // Inserción de cotizaciones
            st.executeUpdate(
                "INSERT INTO Cotizacion (NCOT, ID_CLI, ID_EMP, FEMI, DESCT, IGV, COND, GARA, TENT, VOFER, ACTIVO) VALUES " +
                "('001-000001', 4, 1, '2025-10-23', 0.00, 0.18, NULL, 'Servicio/Piezas: 6 meses. Equipo completo: 1 año.', '5 días hábiles', '2025-11-23', TRUE)," +
                "('001-000002', 3, 1, '2025-10-23', 0.00, 0.18, 'El transporte e instalación corren por cuenta del cliente, salvo acuerdo previo.', 'Servicios: 30 días. Cámaras: 1 año.', '10 días', NULL, TRUE)," +
                "('001-000003', 7, 1, '2025-10-22', 0.00, 0.18, NULL, 'Servicio/Piezas: 6 meses.', '1 día', NULL, TRUE)," +
                "('001-000004', 1, 1, '2025-10-21', 50.00, 0.18, 'Descuento aplicado por fidelidad de cliente.', 'Servicio/Piezas: 6 meses.', '2 días', '2025-11-05', TRUE)," +
                "('001-000005', 5, 1, '2025-10-20', 0.00, 0.18, 'Se requiere factura con RUC de la empresa.', 'Servicio/Piezas: 6 meses. Equipo completo: 1 año.', '7 días hábiles', '2025-11-20', TRUE)," +
                "('001-000006', 6, 1, '2025-10-20', 0.00, 0.18, 'Servicio a realizarse en las oficinas del cliente.', 'Servicios: 30 días.', '3 días', NULL, TRUE)," +
                "('001-000007', 10, 1, '2025-10-18', 0.00, 0.18, NULL, 'Piezas: 3 meses.', 'Entrega inmediata', NULL, TRUE)," +
                "('001-000008', 9, 1, '2025-10-18', 100.00, 0.18, 'Descuento por volumen en el proyecto.', 'Cámaras: 1 año. Cableado: 6 meses.', '15 días', '2025-11-30', TRUE)," +
                "('001-000009', 2, 1, '2025-10-17', 0.00, 0.18, 'Pago 50% al contado y 50% a la entrega.', 'Servicio/Piezas: 6 meses.', '4 días', NULL, TRUE)," +
                "('001-000010', 8, 1, '2025-10-17', 0.00, 0.18, NULL, 'Piezas: 6 meses.', 'Entrega inmediata', NULL, TRUE);"
            );

            st.executeUpdate(
                "INSERT INTO Cotizacion_Detalle (NCOT, ID_SERV, CANT) VALUES " +
                "('001-000001', 'P001', 1)," +
                "('001-000001', 'P002', 1)," +
                "('001-000001', 'P003', 2)," +
                "('001-000001', 'P007', 1)," +
                "('001-000001', 'P004', 1)," +
                "('001-000001', 'P005', 1)," +
                "('001-000001', 'P006', 1)," +
                "('001-000001', 'S002', 1)," +
                "('001-000002', 'S004', 4)," +
                "('001-000002', 'S005', 60)," +
                "('001-000002', 'S007', 1)," +
                "('001-000003', 'S001', 1)," +
                "('001-000004', 'P001', 1)," +
                "('001-000004', 'P002', 1)," +
                "('001-000005', 'P003', 10)," +
                "('001-000005', 'P004', 5)," +
                "('001-000005', 'S002', 5)," +
                "('001-000006', 'S003', 1)," +
                "('001-000006', 'S006', 1)," +
                "('001-000007', 'P008', 1)," +
                "('001-000008', 'S004', 8)," +
                "('001-000008', 'S005', 100)," +
                "('001-000009', 'P006', 1)," +
                "('001-000009', 'S002', 1)," +
                "('001-000010', 'P003', 1)," +
                "('001-000010', 'P004', 1);"
            );
        } catch (Exception ex) {
            // No mostrar nada al usuario
        }
    }
}

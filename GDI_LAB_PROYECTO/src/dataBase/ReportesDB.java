package dataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportesDB {

    public static List<String[]> reporteStockDisponible() throws SQLException {
        List<String[]> reporte = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT id_serv, descp, stock, reservado, disponible FROM FN_REPORTE_STOCK_DISPONIBLE()")) {
            while (rs.next()) {
                reporte.add(new String[] {
                        rs.getString("id_serv"),
                        rs.getString("descp"),
                        rs.getString("stock"),
                        rs.getString("reservado"),
                        rs.getString("disponible")
                });
            }
        }
        return reporte;
    }

    public static List<String[]> topProductosCotizados(int limite) throws SQLException {
        List<String[]> reporte = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT id_serv, descp, total_cant, valor_estimado FROM FN_TOP_PRODUCTOS_COTIZADOS(?)")) {
            ps.setInt(1, limite);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reporte.add(new String[] {
                        rs.getString("id_serv"),
                        rs.getString("descp"),
                        rs.getString("total_cant"),
                        rs.getString("valor_estimado")
                });
            }
        }
        return reporte;
    }

    public static List<String[]> historialCotizacionesCliente(int idCli) throws SQLException {
        List<String[]> reporte = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT ncot, femi, items, subtotal, total FROM FN_HISTORIAL_COTIZACIONES_CLIENTE(?)")) {
            ps.setInt(1, idCli);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reporte.add(new String[] {
                        rs.getString("ncot"),
                        rs.getString("femi"),
                        rs.getString("items"),
                        rs.getString("subtotal"),
                        rs.getString("total")
                });
            }
        }
        return reporte;
    }

    public static List<String[]> alertasVencimiento(int dias) throws SQLException {
        List<String[]> reporte = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT ncot, id_cli, femi, vofer, dias_restantes FROM FN_ALERTAS_VENCIMIENTO(?)")) {
            ps.setInt(1, dias);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reporte.add(new String[] {
                        rs.getString("ncot"),
                        rs.getString("id_cli"),
                        rs.getString("femi"),
                        rs.getString("vofer"),
                        rs.getString("dias_restantes")
                });
            }
        }
        return reporte;
    }

    public static List<String[]> resumenMensualTotales(Date fechaInicio, Date fechaFin) throws SQLException {
        List<String[]> reporte = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT mes, num_cotizaciones, total_mes FROM FN_RESUMEN_MENSUAL_TOTALES(?, ?)")) {
            ps.setDate(1, fechaInicio);
            ps.setDate(2, fechaFin);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reporte.add(new String[] {
                        rs.getString("mes"),
                        rs.getString("num_cotizaciones"),
                        rs.getString("total_mes")
                });
            }
        }
        return reporte;
    }

    public static List<String[]> topClientesPorGasto(int limite) throws SQLException {
        List<String[]> reporte = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT id_cli, cliente, num_cot, total_estimado FROM FN_TOP_CLIENTES_POR_GASTO(?)")) {
            ps.setInt(1, limite);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reporte.add(new String[] {
                        rs.getString("id_cli"),
                        rs.getString("cliente"),
                        rs.getString("num_cot"),
                        rs.getString("total_estimado")
                });
            }
        }
        return reporte;
    }

    public static List<String[]> verificarIntegridadDetalle() throws SQLException {
        List<String[]> reporte = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt
                        .executeQuery("SELECT id_det, ncot, id_serv, cant FROM FN_VERIFICAR_INTEGRIDAD_DETALLE()")) {
            while (rs.next()) {
                reporte.add(new String[] {
                        rs.getString("id_det"),
                        rs.getString("ncot"),
                        rs.getString("id_serv"),
                        rs.getString("cant")
                });
            }
        }
        return reporte;
    }
}

package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;
import dataBase.ReportesDB;

/**
 * Panel para la visualización y exportación de reportes analíticos del sistema.
 * Incluye reportes de stock, productos cotizados, historial de clientes,
 * alertas de vencimiento, resumen mensual, ranking de clientes y verificación
 * de integridad.
 * Permite exportar cada reporte a PDF.
 */
public class ReportesPanel extends JPanel {
    private JTabbedPane tabs;
    private final Color colorFondoPanel = new Color(220, 235, 250);
    private final Color colorBorde = new Color(100, 160, 220);
    private final Font fuenteCampos = new Font("Segoe UI", Font.PLAIN, 16);

    public ReportesPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1100, 650));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(colorBorde, 2, true), "Reportes y Consultas Analíticas"));
        setBackground(colorFondoPanel);

        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tabs.setBackground(colorFondoPanel);

        tabs.addTab("Stock Disponible", crearPanelStockDisponible());
        tabs.addTab("Top Productos Cotizados", crearPanelTopProductos());
        tabs.addTab("Historial Cliente", crearPanelHistorialCliente());
        tabs.addTab("Alertas Vencimiento", crearPanelAlertasVencimiento());
        tabs.addTab("Resumen Mensual", crearPanelResumenMensual());
        tabs.addTab("Top Clientes por Gasto", crearPanelTopClientes());
        tabs.addTab("Integridad Detalle", crearPanelIntegridadDetalle());

        add(tabs, BorderLayout.CENTER);
    }

    /**
     * Crea el panel de reporte de stock disponible.
     * Permite mostrar y exportar el reporte a PDF.
     */
    private JPanel crearPanelStockDisponible() {
        JPanel panel = crearPanelBase("Reporte de Stock Disponible");
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[] { "ID", "Descripción", "Stock", "Reservado", "Disponible" }, 0);
        JTable tabla = crearTablaBonita(modelo);
        JButton btn = crearBoton("Mostrar");
        JButton btnExportar = crearBoton("Exportar PDF");
        btn.addActionListener(e -> {
            modelo.setRowCount(0);
            try {
                List<String[]> datos = ReportesDB.reporteStockDisponible();
                for (String[] fila : datos)
                    modelo.addRow(fila);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        btnExportar.addActionListener(e -> {
            try {
                exportador.ExportarReportePDF.exportarTabla(tabla, "Stock_Disponible");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar PDF: " + ex.getMessage());
            }
        });
        JPanel abajo = new JPanel();
        abajo.setBackground(colorFondoPanel);
        abajo.add(btn);
        abajo.add(btnExportar);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(abajo, BorderLayout.SOUTH);
        btn.doClick();
        return panel;
    }

    /**
     * Crea el panel de top productos cotizados.
     * Permite mostrar y exportar el reporte a PDF.
     */
    private JPanel crearPanelTopProductos() {
        JPanel panel = crearPanelBase("Top Productos Cotizados");
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[] { "ID", "Descripción", "Cantidad", "Valor Estimado" }, 0);
        JTable tabla = crearTablaBonita(modelo);
        JPanel arriba = new JPanel();
        arriba.setBackground(colorFondoPanel);
        arriba.add(new JLabel("Top:"));
        JTextField txtLimite = new JTextField("10", 4);
        txtLimite.setFont(fuenteCampos);
        arriba.add(txtLimite);
        JButton btn = crearBoton("Mostrar");
        JButton btnExportar = crearBoton("Exportar PDF");
        btn.addActionListener(e -> {
            modelo.setRowCount(0);
            try {
                int lim = Integer.parseInt(txtLimite.getText());
                List<String[]> datos = ReportesDB.topProductosCotizados(lim);
                for (String[] fila : datos)
                    modelo.addRow(fila);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        btnExportar.addActionListener(e -> {
            try {
                exportador.ExportarReportePDF.exportarTabla(tabla, "Top_Productos_Cotizados");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar PDF: " + ex.getMessage());
            }
        });
        arriba.add(btn);
        arriba.add(btnExportar);
        panel.add(arriba, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        btn.doClick();
        return panel;
    }

    /**
     * Crea el panel de historial de cotizaciones por cliente.
     * Permite mostrar y exportar el reporte a PDF.
     */
    private JPanel crearPanelHistorialCliente() {
        JPanel panel = crearPanelBase("Historial de Cotizaciones por Cliente");
        DefaultTableModel modelo = new DefaultTableModel(new Object[] { "NCOT", "Fecha", "Items", "Subtotal", "Total" },
                0);
        JTable tabla = crearTablaBonita(modelo);
        JPanel arriba = new JPanel();
        arriba.setBackground(colorFondoPanel);
        arriba.add(new JLabel("ID Cliente:"));
        JTextField txtId = new JTextField(6);
        txtId.setFont(fuenteCampos);
        arriba.add(txtId);
        JButton btn = crearBoton("Mostrar");
        JButton btnExportar = crearBoton("Exportar PDF");
        btn.addActionListener(e -> {
            modelo.setRowCount(0);
            try {
                String val = txtId.getText().trim();
                if (val.isEmpty())
                    return;
                int id = Integer.parseInt(val);
                List<String[]> datos = ReportesDB.historialCotizacionesCliente(id);
                for (String[] fila : datos)
                    modelo.addRow(fila);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        btnExportar.addActionListener(e -> {
            try {
                exportador.ExportarReportePDF.exportarTabla(tabla, "Historial_Cliente");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar PDF: " + ex.getMessage());
            }
        });
        arriba.add(btn);
        arriba.add(btnExportar);
        panel.add(arriba, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Crea el panel de alertas de vencimiento de ofertas.
     * Permite mostrar y exportar el reporte a PDF.
     */
    private JPanel crearPanelAlertasVencimiento() {
        JPanel panel = crearPanelBase("Alertas de Vencimiento de Ofertas");
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[] { "NCOT", "ID Cliente", "Fecha", "Validez", "Días Restantes" }, 0);
        JTable tabla = crearTablaBonita(modelo);
        JPanel arriba = new JPanel();
        arriba.setBackground(colorFondoPanel);
        arriba.add(new JLabel("Días hasta vencimiento (vacío = todos):"));
        JTextField txtDias = new JTextField(4);
        txtDias.setFont(fuenteCampos);
        arriba.add(txtDias);
        JButton btn = crearBoton("Mostrar");
        JButton btnExportar = crearBoton("Exportar PDF");
        btn.addActionListener(e -> {
            modelo.setRowCount(0);
            try {
                List<String[]> datos;
                String val = txtDias.getText().trim();
                if (val.isEmpty()) {
                    datos = ReportesDB.alertasVencimiento(3650);
                } else {
                    int dias = Integer.parseInt(val);
                    datos = ReportesDB.alertasVencimiento(dias);
                }
                for (String[] fila : datos)
                    modelo.addRow(fila);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        btnExportar.addActionListener(e -> {
            try {
                exportador.ExportarReportePDF.exportarTabla(tabla, "Alertas_Vencimiento");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar PDF: " + ex.getMessage());
            }
        });
        arriba.add(btn);
        arriba.add(btnExportar);
        panel.add(arriba, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        btn.doClick();
        return panel;
    }

    /**
     * Crea el panel de resumen mensual de cotizaciones.
     * Permite mostrar y exportar el reporte a PDF.
     */
    private JPanel crearPanelResumenMensual() {
        JPanel panel = crearPanelBase("Resumen Mensual de Cotizaciones");
        DefaultTableModel modelo = new DefaultTableModel(new Object[] { "Mes", "N° Cotizaciones", "Total Mes" }, 0);
        JTable tabla = crearTablaBonita(modelo);
        JPanel arriba = new JPanel();
        arriba.setBackground(colorFondoPanel);
        arriba.add(new JLabel("Fecha Inicio (yyyy-MM-dd):"));
        JTextField txtInicio = new JTextField(10);
        txtInicio.setFont(fuenteCampos);
        arriba.add(txtInicio);
        arriba.add(new JLabel("Fecha Fin (yyyy-MM-dd):"));
        JTextField txtFin = new JTextField(10);
        txtFin.setFont(fuenteCampos);
        arriba.add(txtFin);
        JButton btn = crearBoton("Mostrar");
        JButton btnExportar = crearBoton("Exportar PDF");
        btn.addActionListener(e -> {
            modelo.setRowCount(0);
            try {
                Date ini = Date.valueOf(txtInicio.getText());
                Date fin = Date.valueOf(txtFin.getText());
                List<String[]> datos = ReportesDB.resumenMensualTotales(ini, fin);
                for (String[] fila : datos)
                    modelo.addRow(fila);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        btnExportar.addActionListener(e -> {
            try {
                exportador.ExportarReportePDF.exportarTabla(tabla, "Resumen_Mensual");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar PDF: " + ex.getMessage());
            }
        });
        arriba.add(btn);
        arriba.add(btnExportar);
        panel.add(arriba, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Crea el panel de top clientes por gasto.
     * Permite mostrar y exportar el reporte a PDF.
     */
    private JPanel crearPanelTopClientes() {
        JPanel panel = crearPanelBase("Top Clientes por Gasto");
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[] { "ID Cliente", "Cliente", "N° Cotizaciones", "Total Estimado" }, 0);
        JTable tabla = crearTablaBonita(modelo);
        JPanel arriba = new JPanel();
        arriba.setBackground(colorFondoPanel);
        arriba.add(new JLabel("Top:"));
        JTextField txtLimite = new JTextField("10", 4);
        txtLimite.setFont(fuenteCampos);
        arriba.add(txtLimite);
        JButton btn = crearBoton("Mostrar");
        JButton btnExportar = crearBoton("Exportar PDF");
        btn.addActionListener(e -> {
            modelo.setRowCount(0);
            try {
                int lim = Integer.parseInt(txtLimite.getText());
                List<String[]> datos = ReportesDB.topClientesPorGasto(lim);
                for (String[] fila : datos)
                    modelo.addRow(fila);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        btnExportar.addActionListener(e -> {
            try {
                exportador.ExportarReportePDF.exportarTabla(tabla, "Top_Clientes_Por_Gasto");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar PDF: " + ex.getMessage());
            }
        });
        arriba.add(btn);
        arriba.add(btnExportar);
        panel.add(arriba, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        btn.doClick();
        return panel;
    }

    /**
     * Crea el panel de verificación de integridad de detalles.
     * Permite mostrar y exportar el reporte a PDF.
     */
    private JPanel crearPanelIntegridadDetalle() {
        JPanel panel = crearPanelBase("Verificación de Integridad de Detalles");
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[] { "ID Detalle", "NCOT", "ID Servicio", "Cantidad" }, 0);
        JTable tabla = crearTablaBonita(modelo);
        JButton btn = crearBoton("Mostrar");
        JButton btnExportar = crearBoton("Exportar PDF");
        btn.addActionListener(e -> {
            modelo.setRowCount(0);
            try {
                List<String[]> datos = ReportesDB.verificarIntegridadDetalle();
                for (String[] fila : datos)
                    modelo.addRow(fila);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        btnExportar.addActionListener(e -> {
            try {
                exportador.ExportarReportePDF.exportarTabla(tabla, "Integridad_Detalle");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar PDF: " + ex.getMessage());
            }
        });
        JPanel abajo = new JPanel();
        abajo.setBackground(colorFondoPanel);
        abajo.add(btn);
        abajo.add(btnExportar);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(abajo, BorderLayout.SOUTH);
        btn.doClick();
        return panel;
    }

    // Utilidades de estilo
    private JPanel crearPanelBase(String titulo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(colorFondoPanel);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(colorBorde, 1, true), titulo));
        return panel;
    }

    private JTable crearTablaBonita(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setFont(fuenteCampos);
        tabla.setRowHeight(28);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        tabla.setBackground(Color.WHITE);
        tabla.setGridColor(colorBorde);
        tabla.setSelectionBackground(new Color(180, 210, 240));
        return tabla;
    }

    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(colorBorde);
        btn.setForeground(Color.WHITE);
        btn.setFont(fuenteCampos);
        btn.setFocusPainted(false);
        return btn;
    }
}

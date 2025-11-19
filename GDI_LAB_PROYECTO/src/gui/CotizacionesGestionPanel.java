package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import dataBase.CotizacionDB;

/**
 * Panel para la gestión de cotizaciones: ver, eliminar, reactivar, modificar, actualizar y exportar PDF.
 * Utiliza CotizacionDB para operaciones con la base de datos.
 */
public class CotizacionesGestionPanel extends JPanel {
    private JTable tablaCotizaciones;
    private DefaultTableModel modeloCotizaciones;
    private JButton btnEliminar, btnReactivar, btnActualizar, btnVerCotizacion, btnExportarPDF;

    private Color colorFondoPanel = new Color(220, 235, 250);
    private Color colorBorde = new Color(100, 160, 220);
    private Font fuenteCampos = new Font("Segoe UI", Font.PLAIN, 16);

    /**
     * Constructor: inicializa el panel, la tabla y los botones.
     */
    public CotizacionesGestionPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(colorBorde, 2, true), "Gestión de Cotizaciones"));
        setBackground(colorFondoPanel);

        modeloCotizaciones = new DefaultTableModel(new Object[] { "NCOT", "Fecha", "Cliente", "Garantía" }, 0);
        tablaCotizaciones = new JTable(modeloCotizaciones);
        tablaCotizaciones.setFont(fuenteCampos);
        tablaCotizaciones.setRowHeight(28);
        tablaCotizaciones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        tablaCotizaciones.setBackground(Color.WHITE);

        cargarCotizaciones();

        btnEliminar = new JButton("Eliminar");
        btnReactivar = new JButton("Reactivar");
        btnActualizar = new JButton("Actualizar");
        btnVerCotizacion = new JButton("Ver Cotización");
        btnExportarPDF = new JButton("Exportar PDF");

        btnEliminar.setBackground(colorBorde);
        btnReactivar.setBackground(colorBorde);
        btnActualizar.setBackground(colorBorde);
        btnVerCotizacion.setBackground(colorBorde);
        btnExportarPDF.setBackground(colorBorde);
        btnEliminar.setForeground(Color.WHITE);
        btnReactivar.setForeground(Color.WHITE);
        btnActualizar.setForeground(Color.WHITE);
        btnVerCotizacion.setForeground(Color.WHITE);
        btnExportarPDF.setForeground(Color.WHITE);
        btnEliminar.setFont(fuenteCampos);
        btnReactivar.setFont(fuenteCampos);
        btnActualizar.setFont(fuenteCampos);
        btnVerCotizacion.setFont(fuenteCampos);
        btnExportarPDF.setFont(fuenteCampos);

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(colorFondoPanel);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnReactivar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnVerCotizacion);
        panelBotones.add(btnExportarPDF);

        add(new JScrollPane(tablaCotizaciones), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        btnEliminar.addActionListener(e -> eliminarCotizacion());
        btnReactivar.addActionListener(e -> mostrarVentanaReactivar());
        btnActualizar.addActionListener(e -> cargarCotizaciones());
        btnVerCotizacion.addActionListener(e -> verCotizacionSeleccionada());
        btnExportarPDF.addActionListener(e -> exportarCotizacionPDF());
    }

    /**
     * Carga las cotizaciones activas en la tabla.
     */
    private void cargarCotizaciones() {
        modeloCotizaciones.setRowCount(0);
        try {
            for (String[] cot : CotizacionDB.listarCotizaciones()) { // Solo activas
                modeloCotizaciones.addRow(cot);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar cotizaciones: " + ex.getMessage());
        }
    }

    /**
     * Muestra una ventana para reactivar cotizaciones desactivadas.
     */
    private void mostrarVentanaReactivar() {
        JFrame frame = new JFrame("Reactivar Cotizaciones");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        JTable tabla = new JTable(new DefaultTableModel(new Object[] { "NCOT", "Fecha", "Cliente", "Garantía" }, 0));
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        try {
            for (String[] cot : CotizacionDB.listarCotizacionesDesactivadas()) {
                modelo.addRow(cot);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar cotizaciones desactivadas: " + ex.getMessage());
        }
        JButton btnReactivarSel = new JButton("Reactivar");
        btnReactivarSel.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                String ncot = modelo.getValueAt(fila, 0).toString();
                try {
                    CotizacionDB.reactivarCotizacion(ncot);
                    modelo.removeRow(fila);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error al reactivar cotización: " + ex.getMessage());
                }
            }
        });
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnReactivarSel);
        frame.add(new JScrollPane(tabla), BorderLayout.CENTER);
        frame.add(panelBotones, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    /**
     * Elimina lógicamente la cotización seleccionada.
     */
    private void eliminarCotizacion() {
        int fila = tablaCotizaciones.getSelectedRow();
        if (fila == -1)
            return;
        String ncot = modeloCotizaciones.getValueAt(fila, 0).toString();
        int res = JOptionPane.showConfirmDialog(this, "¿Eliminar cotización seleccionada?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            try {
                CotizacionDB.eliminarLogicoCotizacion(ncot);
                cargarCotizaciones();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar cotización: " + ex.getMessage());
            }
        }
    }

    /**
     * Reactiva la cotización seleccionada.
     */
    private void reactivarCotizacion() {
        int fila = tablaCotizaciones.getSelectedRow();
        if (fila == -1)
            return;
        String ncot = modeloCotizaciones.getValueAt(fila, 0).toString();
        try {
            CotizacionDB.reactivarCotizacion(ncot);
            cargarCotizaciones();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al reactivar cotización: " + ex.getMessage());
        }
    }

    /**
     * Muestra los detalles de la cotización seleccionada en un cuadro de diálogo.
     */
    private void verCotizacionSeleccionada() {
        int fila = tablaCotizaciones.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cotización para ver.");
            return;
        }
        String ncot = modeloCotizaciones.getValueAt(fila, 0).toString();

        String fecha = modeloCotizaciones.getValueAt(fila, 1).toString();
        String cliente = modeloCotizaciones.getValueAt(fila, 2).toString();
        String garantia = modeloCotizaciones.getValueAt(fila, 3).toString();

        double subtotal = 0;
        double descuento = 0;
        double igv = 0.18;
        double total = 0;
        double base = 0;
        java.util.List<String[]> detalles = null; // <-- Declarar aquí
        try {
            detalles = dataBase.DetalleCotizacionDB.listarLineasCotizacion(ncot);
            for (String[] det : detalles) {
                subtotal += Double.parseDouble(det[5]); // linea_total
            }
            try (Connection conn = dataBase.DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT desct FROM Cotizacion WHERE ncot = ?")) {
                ps.setString(1, ncot);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    descuento = rs.getDouble("desct");
                }
            }
            base = subtotal - descuento;
            double igvCalc = base * igv;
            total = base + igvCalc;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al obtener detalles: " + ex.getMessage());
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Cotización: ").append(ncot).append("\n");
        sb.append("Fecha Emisión: ").append(fecha).append("\n");
        sb.append("Cliente: ").append(cliente).append("\n");
        sb.append("Garantía: ").append(garantia).append("\n");
        sb.append("Descuento: S/ ").append(String.format("%.2f", descuento)).append("\n");
        sb.append("Subtotal: S/ ").append(String.format("%.2f", subtotal)).append("\n");
        sb.append("IGV: S/ ").append(String.format("%.2f", base * igv)).append("\n");
        sb.append("Total: S/ ").append(String.format("%.2f", total)).append("\n\n");
        sb.append("Detalle:\n");
        if (detalles != null) {
            for (String[] det : detalles) {
                sb.append("- ").append(det[2]).append(" x").append(det[4]).append(" = S/").append(det[5]).append("\n");
            }
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        area.setBackground(new Color(230, 240, 255));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(500, 400));
        JOptionPane.showMessageDialog(this, scroll, "Vista de Cotización", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Exporta la cotización seleccionada a PDF.
     */
    private void exportarCotizacionPDF() {
        int fila = tablaCotizaciones.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cotización para exportar.");
            return;
        }
        String ncot = modeloCotizaciones.getValueAt(fila, 0).toString();
        try {
            gui.ExportarCotizacionPDF.exportar(ncot);
            JOptionPane.showMessageDialog(this, "Cotización exportada a PDF correctamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al exportar PDF: " + ex.getMessage());
        }
    }
}

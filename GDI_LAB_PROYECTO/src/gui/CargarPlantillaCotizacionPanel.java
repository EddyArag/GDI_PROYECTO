package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.function.Consumer;
import dataBase.CotizacionDB;

public class CargarPlantillaCotizacionPanel extends JPanel {
    private JTable tablaCotizaciones;
    private DefaultTableModel modeloCotizaciones;
    private JButton btnUsar;
    private JTextField txtBuscarCot;
    private JButton btnBuscarCot;
    private JButton btnOrdenTotalAsc, btnOrdenTotalDesc;

    private Consumer<String> onCotizacionSeleccionada;

    public CargarPlantillaCotizacionPanel(Consumer<String> onCotizacionSeleccionada) {
        this.onCotizacionSeleccionada = onCotizacionSeleccionada;
        setLayout(new BorderLayout());

        modeloCotizaciones = new DefaultTableModel(new Object[] { "NCOT", "Fecha", "Cliente", "Garantía", "Total" }, 0);
        tablaCotizaciones = new JTable(modeloCotizaciones);
        tablaCotizaciones.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tablaCotizaciones.setRowHeight(28);
        tablaCotizaciones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        tablaCotizaciones.setBackground(Color.WHITE);

        txtBuscarCot = new JTextField(14);
        txtBuscarCot.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btnBuscarCot = new JButton("Buscar");
        btnBuscarCot.setBackground(new Color(100, 160, 220));
        btnBuscarCot.setForeground(Color.WHITE);
        btnBuscarCot.setFont(new Font("Segoe UI", Font.BOLD, 16));

        btnOrdenTotalAsc = new JButton("Total ↑");
        btnOrdenTotalDesc = new JButton("Total ↓");
        btnOrdenTotalAsc.setBackground(new Color(100, 160, 220));
        btnOrdenTotalDesc.setBackground(new Color(100, 160, 220));
        btnOrdenTotalAsc.setForeground(Color.WHITE);
        btnOrdenTotalDesc.setForeground(Color.WHITE);
        btnOrdenTotalAsc.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnOrdenTotalDesc.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel panelBusqueda = new JPanel();
        panelBusqueda.setBackground(Color.WHITE);
        panelBusqueda.add(new JLabel("Buscar por NCOT:"));
        panelBusqueda.add(txtBuscarCot);
        panelBusqueda.add(btnBuscarCot);
        panelBusqueda.add(btnOrdenTotalAsc);
        panelBusqueda.add(btnOrdenTotalDesc);

        btnUsar = new JButton("Usar Cotización");
        btnUsar.setBackground(new Color(100, 160, 220));
        btnUsar.setForeground(Color.WHITE);
        btnUsar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnUsar.addActionListener(e -> usarCotizacion());

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnUsar);

        add(panelBusqueda, BorderLayout.NORTH);
        add(new JScrollPane(tablaCotizaciones), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        cargarCotizaciones();

        btnBuscarCot.addActionListener(e -> buscarCotizacionPorNCOT());
        btnOrdenTotalAsc.addActionListener(e -> cargarCotizacionesPorTotalAsc());
        btnOrdenTotalDesc.addActionListener(e -> cargarCotizacionesPorTotalDesc());
    }

    private void cargarCotizaciones() {
        modeloCotizaciones.setRowCount(0);
        try {
            for (String[] cot : dataBase.CotizacionDB.listarCotizacionesPorTotalDesc()) {
                modeloCotizaciones.addRow(new Object[] { cot[0], cot[1], cot[2], cot[3], cot[4] });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar cotizaciones: " + ex.getMessage());
        }
    }

    private void usarCotizacion() {
        int fila = tablaCotizaciones.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cotización para usar.");
            return;
        }
        String ncot = modeloCotizaciones.getValueAt(fila, 0).toString();
        if (onCotizacionSeleccionada != null) {
            onCotizacionSeleccionada.accept(ncot);
        }
    }

    private void cargarCotizacionesPorTotalAsc() {
        modeloCotizaciones.setRowCount(0);
        try {
            for (String[] cot : dataBase.CotizacionDB.listarCotizacionesPorTotalAsc()) {
                modeloCotizaciones.addRow(new Object[] { cot[0], cot[1], cot[2], cot[3], cot[4] });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al ordenar cotizaciones: " + ex.getMessage());
        }
    }

    private void cargarCotizacionesPorTotalDesc() {
        modeloCotizaciones.setRowCount(0);
        try {
            for (String[] cot : dataBase.CotizacionDB.listarCotizacionesPorTotalDesc()) {
                modeloCotizaciones.addRow(new Object[] { cot[0], cot[1], cot[2], cot[3], cot[4] });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al ordenar cotizaciones: " + ex.getMessage());
        }
    }

    /**
     * Busca cotización por número usando FN_RESUMEN_CABECERA_COTIZACION.
     * Muestra código y nombre completo del cliente y el total.
     */
    private void buscarCotizacionPorNCOT() {
        String ncot = txtBuscarCot.getText().trim();
        ncot = dataBase.CotizacionDB.formatearNCOT(ncot);
        modeloCotizaciones.setRowCount(0);
        if (ncot.isEmpty()) {
            cargarCotizaciones();
            return;
        }
        try (Connection conn = dataBase.DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT c.ncot, c.femi, cl.id_cli, TRIM(cl.p_nomb || ' ' || COALESCE(cl.ape_p,'') || ' ' || COALESCE(cl.ape_m,'')) AS nombre_completo, c.gara, r.total "
                                +
                                "FROM Cotizacion c " +
                                "JOIN Cliente cl ON cl.id_cli = c.id_cli " +
                                "JOIN FN_RESUMEN_CABECERA_COTIZACION(c.ncot) r ON r.ncot = c.ncot " +
                                "WHERE c.ncot = ?")) {
            ps.setString(1, ncot);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                modeloCotizaciones.addRow(new Object[] {
                        rs.getString("ncot"),
                        rs.getString("femi"),
                        rs.getString("id_cli") + " - " + rs.getString("nombre_completo"),
                        rs.getString("gara"),
                        rs.getString("total")
                });
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró la cotización.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar cotización: " + ex.getMessage());
        }
    }
}

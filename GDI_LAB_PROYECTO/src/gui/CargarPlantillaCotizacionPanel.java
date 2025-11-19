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

    private Consumer<String> onCotizacionSeleccionada;

    public CargarPlantillaCotizacionPanel(Consumer<String> onCotizacionSeleccionada) {
        this.onCotizacionSeleccionada = onCotizacionSeleccionada;
        setLayout(new BorderLayout());

        modeloCotizaciones = new DefaultTableModel(new Object[] { "NCOT", "Fecha", "Cliente", "Garantía" }, 0);
        tablaCotizaciones = new JTable(modeloCotizaciones);
        tablaCotizaciones.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tablaCotizaciones.setRowHeight(28);
        tablaCotizaciones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        tablaCotizaciones.setBackground(Color.WHITE);

        cargarCotizaciones();

        btnUsar = new JButton("Usar Cotización");
        btnUsar.setBackground(new Color(100, 160, 220));
        btnUsar.setForeground(Color.WHITE);
        btnUsar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnUsar.addActionListener(e -> usarCotizacion());

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnUsar);

        add(new JScrollPane(tablaCotizaciones), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarCotizaciones() {
        modeloCotizaciones.setRowCount(0);
        try {
            for (String[] cot : CotizacionDB.listarCotizaciones()) {
                modeloCotizaciones.addRow(cot);
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
}

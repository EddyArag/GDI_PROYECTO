package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import dataBase.CotizacionDB;

public class CotizacionesGestionPanel extends JPanel {
    private JTable tablaCotizaciones;
    private DefaultTableModel modeloCotizaciones;
    private JButton btnEliminar, btnReactivar, btnModificar;

    public CotizacionesGestionPanel() {
        setLayout(new BorderLayout());

        modeloCotizaciones = new DefaultTableModel(new Object[] { "NCOT", "Fecha", "Cliente", "Garantía" }, 0);
        tablaCotizaciones = new JTable(modeloCotizaciones);
        cargarCotizaciones();

        btnEliminar = new JButton("Eliminar");
        btnReactivar = new JButton("Reactivar");
        btnModificar = new JButton("Modificar");

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnEliminar);
        panelBotones.add(btnReactivar);
        panelBotones.add(btnModificar);

        add(new JScrollPane(tablaCotizaciones), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        btnEliminar.addActionListener(e -> eliminarCotizacion());
        btnReactivar.addActionListener(e -> reactivarCotizacion());
        btnModificar.addActionListener(e -> modificarCotizacion());
    }

    private void cargarCotizaciones() {
        modeloCotizaciones.setRowCount(0);
        try {
            for (String[] cot : CotizacionDB.listarCotizaciones()) {
                modeloCotizaciones.addRow(cot);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar cotizaciones: " + ex.getMessage());
        }
    }

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

    private void modificarCotizacion() {
        int fila = tablaCotizaciones.getSelectedRow();
        if (fila == -1)
            return;
        String ncot = modeloCotizaciones.getValueAt(fila, 0).toString();
        JFrame frame = new JFrame("Modificar Cotización");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLocationRelativeTo(this);
        frame.add(new ModificarCotizacionPanel(ncot));
        frame.setVisible(true);
    }
}

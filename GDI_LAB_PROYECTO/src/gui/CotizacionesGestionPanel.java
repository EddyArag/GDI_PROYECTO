package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import dataBase.CotizacionDB;

public class CotizacionesGestionPanel extends JPanel {
    private JTable tablaCotizaciones;
    private DefaultTableModel modeloCotizaciones;
    private JButton btnEliminar, btnReactivar, btnModificar, btnActualizar;

    public CotizacionesGestionPanel() {
        setLayout(new BorderLayout());

        modeloCotizaciones = new DefaultTableModel(new Object[] { "NCOT", "Fecha", "Cliente", "Garantía" }, 0);
        tablaCotizaciones = new JTable(modeloCotizaciones);
        cargarCotizaciones();

        btnEliminar = new JButton("Eliminar");
        btnReactivar = new JButton("Reactivar");
        btnModificar = new JButton("Modificar");
        btnActualizar = new JButton("Actualizar");

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnEliminar);
        panelBotones.add(btnReactivar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnActualizar);

        add(new JScrollPane(tablaCotizaciones), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        btnEliminar.addActionListener(e -> eliminarCotizacion());
        btnReactivar.addActionListener(e -> mostrarVentanaReactivar());
        btnModificar.addActionListener(e -> modificarCotizacion());
        btnActualizar.addActionListener(e -> cargarCotizaciones());
    }

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

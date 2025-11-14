package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import dataBase.ClienteDB;

public class ClientesPanel extends JPanel {
    private JTable tablaClientes;
    private DefaultTableModel modeloClientes;
    private JButton btnAgregar, btnModificar, btnEliminar, btnReactivar;

    public ClientesPanel() {
        setLayout(new BorderLayout());

        modeloClientes = new DefaultTableModel(new Object[] { "ID", "Nombre", "RUC", "Observaciones" }, 0);
        tablaClientes = new JTable(modeloClientes);
        cargarClientes();

        btnAgregar = new JButton("Agregar");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnReactivar = new JButton("Reactivar");

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnReactivar);

        add(new JScrollPane(tablaClientes), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarCliente());
        btnModificar.addActionListener(e -> modificarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnReactivar.addActionListener(e -> reactivarCliente());
    }

    private void cargarClientes() {
        modeloClientes.setRowCount(0);
        try {
            for (String[] cli : ClienteDB.listarClientes()) {
                modeloClientes.addRow(cli);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + ex.getMessage());
        }
    }

    private void agregarCliente() {
        JTextField nombre = new JTextField();
        JTextField apeP = new JTextField();
        JTextField apeM = new JTextField();
        JTextField ruc = new JTextField();
        JTextField obs = new JTextField();
        Object[] campos = {
                "Nombre:", nombre,
                "Apellido Paterno:", apeP,
                "Apellido Materno:", apeM,
                "RUC:", ruc,
                "Observaciones:", obs
        };
        int res = JOptionPane.showConfirmDialog(this, campos, "Nuevo Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                ClienteDB.insertarCliente(nombre.getText(), apeP.getText(), apeM.getText(), ruc.getText(),
                        obs.getText());
                cargarClientes();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al agregar cliente: " + ex.getMessage());
            }
        }
    }

    private void modificarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1)
            return;
        String id = modeloClientes.getValueAt(fila, 0).toString();
        JTextField nombre = new JTextField(modeloClientes.getValueAt(fila, 1).toString());
        JTextField apeP = new JTextField();
        JTextField apeM = new JTextField();
        JTextField ruc = new JTextField(modeloClientes.getValueAt(fila, 2).toString());
        JTextField obs = new JTextField(modeloClientes.getValueAt(fila, 3).toString());
        Object[] campos = {
                "Nombre:", nombre,
                "Apellido Paterno:", apeP,
                "Apellido Materno:", apeM,
                "RUC:", ruc,
                "Observaciones:", obs
        };
        int res = JOptionPane.showConfirmDialog(this, campos, "Modificar Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                ClienteDB.modificarCliente(Integer.parseInt(id), nombre.getText(), apeP.getText(), apeM.getText(),
                        ruc.getText(), obs.getText());
                cargarClientes();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al modificar cliente: " + ex.getMessage());
            }
        }
    }

    private void eliminarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1)
            return;
        String id = modeloClientes.getValueAt(fila, 0).toString();
        int res = JOptionPane.showConfirmDialog(this, "¿Eliminar cliente seleccionado?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            try {
                ClienteDB.eliminarLogicoCliente(Integer.parseInt(id));
                cargarClientes();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar cliente: " + ex.getMessage());
            }
        }
    }

    private void reactivarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1)
            return;
        String id = modeloClientes.getValueAt(fila, 0).toString();
        try {
            ClienteDB.reactivarCliente(Integer.parseInt(id));
            cargarClientes();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al reactivar cliente: " + ex.getMessage());
        }
    }
}

package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import dataBase.ClienteDB;

/**
 * Panel para la gestión de clientes: agregar, modificar, eliminar, reactivar y actualizar.
 * Utiliza ClienteDB para operaciones con la base de datos.
 */
public class ClientesPanel extends JPanel {
    private JTable tablaClientes;
    private DefaultTableModel modeloClientes;
    private JButton btnAgregar, btnModificar, btnEliminar, btnReactivar, btnActualizar;
    private Color colorFondoPanel = new Color(220, 235, 250);
    private Color colorBorde = new Color(100, 160, 220);
    private Font fuenteCampos = new Font("Segoe UI", Font.PLAIN, 16);

    /**
     * Constructor: inicializa el panel, la tabla y los botones.
     */
    public ClientesPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(colorBorde, 2, true), "Gestión de Clientes"));
        setBackground(colorFondoPanel);

        modeloClientes = new DefaultTableModel(new Object[] { "ID", "Nombre", "RUC", "Observaciones" }, 0);
        tablaClientes = new JTable(modeloClientes);
        tablaClientes.setFont(fuenteCampos);
        tablaClientes.setRowHeight(28);
        tablaClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        tablaClientes.setBackground(Color.WHITE);

        cargarClientes();

        btnAgregar = new JButton("Agregar");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnReactivar = new JButton("Reactivar");
        btnActualizar = new JButton("Actualizar");

        btnAgregar.setBackground(colorBorde);
        btnModificar.setBackground(colorBorde);
        btnEliminar.setBackground(colorBorde);
        btnReactivar.setBackground(colorBorde);
        btnActualizar.setBackground(colorBorde);
        btnAgregar.setForeground(Color.WHITE);
        btnModificar.setForeground(Color.WHITE);
        btnEliminar.setForeground(Color.WHITE);
        btnReactivar.setForeground(Color.WHITE);
        btnActualizar.setForeground(Color.WHITE);
        btnAgregar.setFont(fuenteCampos);
        btnModificar.setFont(fuenteCampos);
        btnEliminar.setFont(fuenteCampos);
        btnReactivar.setFont(fuenteCampos);
        btnActualizar.setFont(fuenteCampos);

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(colorFondoPanel);
        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnReactivar);
        panelBotones.add(btnActualizar);

        add(new JScrollPane(tablaClientes), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarCliente());
        btnModificar.addActionListener(e -> modificarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnActualizar.addActionListener(e -> cargarClientes());
        btnReactivar.addActionListener(e -> mostrarVentanaReactivar());
    }

    /**
     * Carga los clientes activos en la tabla.
     */
    private void cargarClientes() {
        modeloClientes.setRowCount(0);
        try {
            for (String[] cli : ClienteDB.listarClientes()) { // Solo activos
                modeloClientes.addRow(cli);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + ex.getMessage());
        }
    }

    /**
     * Muestra una ventana para reactivar clientes desactivados.
     */
    private void mostrarVentanaReactivar() {
        JFrame frame = new JFrame("Reactivar Clientes");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);

        DefaultTableModel modelo = new DefaultTableModel(new Object[] { "ID", "Nombre", "RUC", "Observaciones" }, 0);
        JTable tabla = new JTable(modelo);

        try {
            for (String[] cli : dataBase.ClienteDB.listarClientesDesactivados()) {
                modelo.addRow(cli);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error al cargar clientes desactivados: " + ex.getMessage());
        }

        JButton btnReactivarSel = new JButton("Reactivar");
        btnReactivarSel.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                int id = Integer.parseInt(modelo.getValueAt(fila, 0).toString());
                try {
                    dataBase.ClienteDB.reactivarCliente(id);
                    modelo.removeRow(fila);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error al reactivar cliente: " + ex.getMessage());
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
     * Muestra un formulario para agregar un nuevo cliente.
     */
    private void agregarCliente() {
        JTextField nombre = new PlaceholderTextField("Ej: Juan");
        JTextField apeP = new PlaceholderTextField("Ej: Pérez");
        JTextField apeM = new PlaceholderTextField("Ej: Gómez");
        JTextField ruc = new PlaceholderTextField("Ej: 20123456789");
        JTextField obs = new PlaceholderTextField("Observaciones...");
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

    /**
     * Muestra un formulario para modificar el cliente seleccionado.
     */
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

    /**
     * Elimina lógicamente el cliente seleccionado.
     */
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

    /**
     * JTextField con placeholder para formularios.
     */
    class PlaceholderTextField extends JTextField {
        private String placeholder;
        public PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
        }
        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !(FocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == this)) {
                g.setColor(Color.GRAY);
                g.drawString(placeholder, 5, getHeight() - 7);
            }
        }
    }
}

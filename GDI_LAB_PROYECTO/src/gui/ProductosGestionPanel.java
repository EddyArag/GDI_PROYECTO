package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import dataBase.ProductoDB;

public class ProductosGestionPanel extends JPanel {
    private JTable tablaProductos;
    private DefaultTableModel modeloProductos;
    private JButton btnAgregar, btnModificar, btnEliminar, btnReactivar, btnActualizar;

    public ProductosGestionPanel() {
        setLayout(new BorderLayout());

        modeloProductos = new DefaultTableModel(new Object[] { "ID", "Descripción", "Precio", "Stock" }, 0);
        tablaProductos = new JTable(modeloProductos);
        cargarProductos();

        btnAgregar = new JButton("Agregar");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnReactivar = new JButton("Reactivar");
        btnActualizar = new JButton("Actualizar");

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnReactivar);
        panelBotones.add(btnActualizar);

        add(new JScrollPane(tablaProductos), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarProducto());
        btnModificar.addActionListener(e -> modificarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnReactivar.addActionListener(e -> mostrarVentanaReactivar());
        btnActualizar.addActionListener(e -> cargarProductos());
    }

    private void cargarProductos() {
        modeloProductos.setRowCount(0);
        try {
            for (String[] prod : ProductoDB.listarProductos()) { // Solo activos
                modeloProductos.addRow(prod);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + ex.getMessage());
        }
    }

    private void mostrarVentanaReactivar() {
        JFrame frame = new JFrame("Reactivar Productos");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        JTable tabla = new JTable(new DefaultTableModel(new Object[] { "ID", "Descripción", "Precio", "Stock" }, 0));
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        try {
            for (String[] prod : ProductoDB.listarProductosDesactivados()) {
                modelo.addRow(prod);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos desactivados: " + ex.getMessage());
        }
        JButton btnReactivarSel = new JButton("Reactivar");
        btnReactivarSel.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                String idServ = modelo.getValueAt(fila, 0).toString();
                try {
                    ProductoDB.reactivarProducto(idServ);
                    modelo.removeRow(fila);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error al reactivar producto: " + ex.getMessage());
                }
            }
        });
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnReactivarSel);
        frame.add(new JScrollPane(tabla), BorderLayout.CENTER);
        frame.add(panelBotones, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void agregarProducto() {
        JTextField idServ = new PlaceholderTextField("Ej: P001");
        JTextField descp = new PlaceholderTextField("Ej: Servicio de mantenimiento");
        JTextField precio = new PlaceholderTextField("Ej: 150.00");
        JTextField stock = new PlaceholderTextField("Ej: 10");
        Object[] campos = {
                "ID:", idServ,
                "Descripción:", descp,
                "Precio Unitario:", precio,
                "Stock:", stock
        };
        int res = JOptionPane.showConfirmDialog(this, campos, "Nuevo Producto", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                ProductoDB.insertarProducto(idServ.getText(), descp.getText(),
                        Double.parseDouble(precio.getText()), Integer.parseInt(stock.getText()));
                cargarProductos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al agregar producto: " + ex.getMessage());
            }
        }
    }

    private void modificarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1)
            return;
        String idServ = modeloProductos.getValueAt(fila, 0).toString();
        JTextField descp = new JTextField(modeloProductos.getValueAt(fila, 1).toString());
        JTextField precio = new JTextField(modeloProductos.getValueAt(fila, 2).toString());
        JTextField stock = new JTextField(modeloProductos.getValueAt(fila, 3).toString());
        Object[] campos = {
                "Descripción:", descp,
                "Precio Unitario:", precio,
                "Stock:", stock
        };
        int res = JOptionPane.showConfirmDialog(this, campos, "Modificar Producto", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                ProductoDB.modificarProducto(idServ, descp.getText(),
                        Double.parseDouble(precio.getText()), Integer.parseInt(stock.getText()));
                cargarProductos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al modificar producto: " + ex.getMessage());
            }
        }
    }

    private void eliminarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1)
            return;
        String idServ = modeloProductos.getValueAt(fila, 0).toString();
        int res = JOptionPane.showConfirmDialog(this, "¿Eliminar producto seleccionado?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            try {
                ProductoDB.eliminarLogicoProducto(idServ);
                cargarProductos();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar producto: " + ex.getMessage());
            }
        }
    }

    // Clase auxiliar para placeholder en JTextField
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

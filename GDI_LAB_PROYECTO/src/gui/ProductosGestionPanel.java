package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import dataBase.ProductoDB;

public class ProductosGestionPanel extends JPanel {
    private JTable tablaProductos;
    private DefaultTableModel modeloProductos;
    private JButton btnAgregar, btnModificar, btnEliminar, btnReactivar;

    public ProductosGestionPanel() {
        setLayout(new BorderLayout());

        modeloProductos = new DefaultTableModel(new Object[] { "ID", "Descripción", "Precio", "Stock" }, 0);
        tablaProductos = new JTable(modeloProductos);
        cargarProductos();

        btnAgregar = new JButton("Agregar");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnReactivar = new JButton("Reactivar");

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnReactivar);

        add(new JScrollPane(tablaProductos), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarProducto());
        btnModificar.addActionListener(e -> modificarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnReactivar.addActionListener(e -> reactivarProducto());
    }

    private void cargarProductos() {
        modeloProductos.setRowCount(0);
        try {
            for (String[] prod : ProductoDB.listarProductos()) {
                modeloProductos.addRow(prod);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + ex.getMessage());
        }
    }

    private void agregarProducto() {
        JTextField idServ = new JTextField();
        JTextField descp = new JTextField();
        JTextField precio = new JTextField();
        JTextField stock = new JTextField();
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

    private void reactivarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1)
            return;
        String idServ = modeloProductos.getValueAt(fila, 0).toString();
        try {
            ProductoDB.reactivarProducto(idServ);
            cargarProductos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al reactivar producto: " + ex.getMessage());
        }
    }
}

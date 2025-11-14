package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import dataBase.ProductoDB;

public class ProductosFrame extends JFrame {
    private JTable tablaProductos;
    private DefaultTableModel modeloProductos;
    private JTextField txtCantidad;
    private JButton btnAgregar;

    public interface ProductoListener {
        void productoSeleccionado(String idServ, String nombre, double precio, int cantidad);
    }

    private ProductoListener listener;

    public ProductosFrame(ProductoListener listener) {
        this.listener = listener;
        setTitle("Seleccionar Producto");
        setSize(600, 400);
        setLocationRelativeTo(null);

        modeloProductos = new DefaultTableModel(new Object[] { "ID", "Descripción", "Precio", "Stock" }, 0);
        tablaProductos = new JTable(modeloProductos);
        cargarProductos();

        txtCantidad = new JTextField(5);
        btnAgregar = new JButton("Agregar a Cotización");

        JPanel panelInferior = new JPanel();
        panelInferior.add(new JLabel("Cantidad:"));
        panelInferior.add(txtCantidad);
        panelInferior.add(btnAgregar);

        add(new JScrollPane(tablaProductos), BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarProducto());
    }

    private void cargarProductos() {
        modeloProductos.setRowCount(0);
        try {
            for (String[] prod : ProductoDB.listarProductos()) {
                modeloProductos.addRow(new Object[] {
                        prod[0], prod[1], prod[2], prod[3]
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + ex.getMessage());
        }
    }

    private void agregarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto.");
            return;
        }
        String idServ = modeloProductos.getValueAt(fila, 0).toString();
        String nombre = modeloProductos.getValueAt(fila, 1).toString();
        double precio = Double.parseDouble(modeloProductos.getValueAt(fila, 2).toString());
        int stock = Integer.parseInt(modeloProductos.getValueAt(fila, 3).toString());
        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText());
            if (cantidad <= 0)
                throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida.");
            return;
        }
        if (cantidad > stock) {
            JOptionPane.showMessageDialog(this, "La cantidad solicitada excede el stock disponible (" + stock + ").",
                    "Error de Stock", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (listener != null) {
            listener.productoSeleccionado(idServ, nombre, precio, cantidad);
        }
        dispose();
    }
}

package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import dataBase.ProductoDB;

/**
 * Ventana para seleccionar productos y agregarlos a la cotización.
 * Permite buscar y ordenar productos por descripción y precio.
 * Utiliza ProductoDB para cargar productos y valida stock.
 */
public class ProductosFrame extends JFrame {
    private JTable tablaProductos;
    private DefaultTableModel modeloProductos;
    private JTextField txtCantidad;
    private JButton btnAgregar;
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JButton btnOrdenPrecioAsc, btnOrdenPrecioDesc;

    public interface ProductoListener {
        void productoSeleccionado(String idServ, String nombre, double precio, int cantidad);
    }

    private ProductoListener listener;

    private Color colorFondoPanel = new Color(220, 235, 250);
    private Color colorBorde = new Color(100, 160, 220);
    private Font fuenteCampos = new Font("Segoe UI", Font.PLAIN, 16);

    /**
     * Constructor: inicializa la ventana y sus controles.
     * 
     * @param listener Listener para recibir el producto seleccionado.
     */
    public ProductosFrame(ProductoListener listener) {
        this.listener = listener;
        setTitle("Seleccionar Producto");
        setSize(600, 400);
        setLocationRelativeTo(null);

        modeloProductos = new DefaultTableModel(new Object[] { "ID", "Descripción", "Precio", "Stock" }, 0);
        tablaProductos = new JTable(modeloProductos);
        tablaProductos.setFont(fuenteCampos);
        tablaProductos.setRowHeight(28);
        tablaProductos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        tablaProductos.setBackground(Color.WHITE);

        cargarProductos();

        txtCantidad = new JTextField(5);
        txtCantidad.setFont(fuenteCampos);

        btnAgregar = new JButton("Agregar a Cotización");
        btnAgregar.setBackground(colorBorde);
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFont(fuenteCampos);

        txtBuscar = new JTextField(18);
        txtBuscar.setFont(fuenteCampos);
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(colorBorde);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFont(fuenteCampos);

        btnOrdenPrecioAsc = new JButton("Precio ↑");
        btnOrdenPrecioDesc = new JButton("Precio ↓");
        btnOrdenPrecioAsc.setBackground(colorBorde);
        btnOrdenPrecioDesc.setBackground(colorBorde);
        btnOrdenPrecioAsc.setForeground(Color.WHITE);
        btnOrdenPrecioDesc.setForeground(Color.WHITE);
        btnOrdenPrecioAsc.setFont(fuenteCampos);
        btnOrdenPrecioDesc.setFont(fuenteCampos);

        JPanel panelBusqueda = new JPanel();
        panelBusqueda.setBackground(colorFondoPanel);
        panelBusqueda.add(new JLabel("Buscar descripción:"));
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnOrdenPrecioAsc);
        panelBusqueda.add(btnOrdenPrecioDesc);

        JPanel panelInferior = new JPanel();
        panelInferior.setBackground(colorFondoPanel);
        panelInferior.add(new JLabel("Cantidad:"));
        panelInferior.add(txtCantidad);
        panelInferior.add(btnAgregar);

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(colorFondoPanel);
        panelPrincipal.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(colorBorde, 2, true), "Productos/Servicios"));

        panelPrincipal.add(panelBusqueda, BorderLayout.NORTH);
        panelPrincipal.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);

        btnAgregar.addActionListener(e -> agregarProducto());
        btnBuscar.addActionListener(e -> buscarProductos());
        btnOrdenPrecioAsc.addActionListener(e -> cargarProductosPorPrecioAsc());
        btnOrdenPrecioDesc.addActionListener(e -> cargarProductosPorPrecioDesc());
    }

    /**
     * Carga los productos activos en la tabla.
     */
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

    /**
     * Busca productos por texto en la descripción.
     */
    private void buscarProductos() {
        String texto = txtBuscar.getText().trim();
        modeloProductos.setRowCount(0);
        if (texto.isEmpty()) {
            cargarProductos();
            return;
        }
        try {
            for (String[] prod : ProductoDB.buscarProductosPorTexto(texto)) {
                modeloProductos.addRow(new Object[] {
                        prod[0], prod[1], prod[2], prod[3]
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar productos: " + ex.getMessage());
        }
    }

    /**
     * Valida y agrega el producto seleccionado con la cantidad indicada.
     */
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

    /**
     * Carga los productos en orden ascendente por precio.
     */
    private void cargarProductosPorPrecioAsc() {
        modeloProductos.setRowCount(0);
        try {
            for (String[] prod : ProductoDB.listarProductosPorPrecioAsc()) {
                modeloProductos.addRow(new Object[] {
                        prod[0], prod[1], prod[2], prod[3]
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al ordenar productos: " + ex.getMessage());
        }
    }

    /**
     * Carga los productos en orden descendente por precio.
     */
    private void cargarProductosPorPrecioDesc() {
        modeloProductos.setRowCount(0);
        try {
            for (String[] prod : ProductoDB.listarProductosPorPrecioDesc()) {
                modeloProductos.addRow(new Object[] {
                        prod[0], prod[1], prod[2], prod[3]
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al ordenar productos: " + ex.getMessage());
        }
    }
}

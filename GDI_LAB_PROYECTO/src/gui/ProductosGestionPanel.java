package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import dataBase.ProductoDB;

/**
 * Panel para la gestión de productos/servicios: agregar, modificar, eliminar,
 * reactivar y actualizar.
 * Permite buscar y ordenar productos por descripción y precio.
 * Utiliza ProductoDB para operaciones con la base de datos.
 */
public class ProductosGestionPanel extends JPanel {
    private JTable tablaProductos;
    private DefaultTableModel modeloProductos;
    private JButton btnAgregar, btnModificar, btnEliminar, btnReactivar, btnActualizar, btnBuscar;
    private JButton btnOrdenPrecioAsc, btnOrdenPrecioDesc;
    private JTextField txtBuscar;

    private Color colorFondoPanel = new Color(220, 235, 250);
    private Color colorBorde = new Color(100, 160, 220);
    private Font fuenteCampos = new Font("Segoe UI", Font.PLAIN, 16);

    /**
     * Constructor: inicializa el panel, la tabla y los botones.
     */
    public ProductosGestionPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(colorBorde, 2, true), "Gestión de Productos/Servicios"));
        setBackground(colorFondoPanel);

        modeloProductos = new DefaultTableModel(new Object[] { "ID", "Descripción", "Precio", "Stock" }, 0);
        tablaProductos = new JTable(modeloProductos);
        tablaProductos.setFont(fuenteCampos);
        tablaProductos.setRowHeight(28);
        tablaProductos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        tablaProductos.setBackground(Color.WHITE);

        cargarProductos();

        btnAgregar = new JButton("Agregar");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnReactivar = new JButton("Reactivar");
        btnActualizar = new JButton("Actualizar");
        btnBuscar = new JButton("Buscar");
        btnOrdenPrecioAsc = new JButton("Precio ↑");
        btnOrdenPrecioDesc = new JButton("Precio ↓");

        btnAgregar.setBackground(colorBorde);
        btnModificar.setBackground(colorBorde);
        btnEliminar.setBackground(colorBorde);
        btnReactivar.setBackground(colorBorde);
        btnActualizar.setBackground(colorBorde);
        btnBuscar.setBackground(colorBorde);
        btnOrdenPrecioAsc.setBackground(colorBorde);
        btnOrdenPrecioDesc.setBackground(colorBorde);

        btnAgregar.setForeground(Color.WHITE);
        btnModificar.setForeground(Color.WHITE);
        btnEliminar.setForeground(Color.WHITE);
        btnReactivar.setForeground(Color.WHITE);
        btnActualizar.setForeground(Color.WHITE);
        btnBuscar.setForeground(Color.WHITE);
        btnOrdenPrecioAsc.setForeground(Color.WHITE);
        btnOrdenPrecioDesc.setForeground(Color.WHITE);

        btnAgregar.setFont(fuenteCampos);
        btnModificar.setFont(fuenteCampos);
        btnEliminar.setFont(fuenteCampos);
        btnReactivar.setFont(fuenteCampos);
        btnActualizar.setFont(fuenteCampos);
        btnBuscar.setFont(fuenteCampos);
        btnOrdenPrecioAsc.setFont(fuenteCampos);
        btnOrdenPrecioDesc.setFont(fuenteCampos);

        txtBuscar = new JTextField(18);
        txtBuscar.setFont(fuenteCampos);

        JPanel panelBusqueda = new JPanel();
        panelBusqueda.setBackground(colorFondoPanel);
        panelBusqueda.add(new JLabel("Buscar descripción:"));
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnOrdenPrecioAsc);
        panelBusqueda.add(btnOrdenPrecioDesc);

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(colorFondoPanel);
        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnReactivar);
        panelBotones.add(btnActualizar);

        add(panelBusqueda, BorderLayout.NORTH);
        add(new JScrollPane(tablaProductos), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarProducto());
        btnModificar.addActionListener(e -> modificarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnReactivar.addActionListener(e -> mostrarVentanaReactivar());
        btnActualizar.addActionListener(e -> cargarProductos());
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
            for (String[] prod : ProductoDB.listarProductos()) { // Solo activos
                modeloProductos.addRow(prod);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + ex.getMessage());
        }
    }

    /**
     * Muestra una ventana para reactivar productos desactivados.
     */
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

    /**
     * Muestra un formulario para agregar un nuevo producto.
     */
    private void agregarProducto() {
        // Pregunta tipo antes de mostrar el formulario
        String[] opciones = { "Producto", "Servicio" };
        int tipo = JOptionPane.showOptionDialog(this, "¿Qué desea agregar?", "Tipo de registro",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (tipo == JOptionPane.CLOSED_OPTION)
            return;

        String prefix = tipo == 0 ? "P" : "S";
        int maxNum = 0;
        try {
            for (String[] prod : ProductoDB.listarProductos()) {
                String id = prod[0];
                if (id != null && id.startsWith(prefix)) {
                    try {
                        int num = Integer.parseInt(id.substring(1));
                        if (num > maxNum)
                            maxNum = num;
                    } catch (Exception ignore) {
                    }
                }
            }
        } catch (Exception ignore) {
        }
        String nextId = String.format("%s%03d", prefix, maxNum + 1);

        JTextField idServ = new JTextField(nextId);
        idServ.setEditable(false);
        JTextField descp = new PlaceholderTextField("Ej: Servicio de mantenimiento");
        JTextField precio = new PlaceholderTextField("Ej: 150.00");
        JTextField stock = new PlaceholderTextField("Ej: 10");
        Object[] campos = {
                "ID:", idServ,
                "Descripción:", descp,
                "Precio Unitario:", precio,
                "Stock:", stock
        };
        while (true) {
            int res = JOptionPane.showConfirmDialog(this, campos, "Nuevo " + opciones[tipo],
                    JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION)
                return;

            // Validación en Java antes de enviar a la BD
            if (descp.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo Descripción es obligatorio.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                continue;
            }
            double precioVal = 0;
            try {
                precioVal = Double.parseDouble(precio.getText().trim());
                if (precioVal <= 0)
                    throw new NumberFormatException();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "El campo Precio debe ser un número mayor a 0.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                continue;
            }
            String stockTxt = stock.getText().trim();
            int stockVal = 0;
            boolean stockAlerta = false;
            boolean stockNegativo = false;
            try {
                double stockDouble = Double.parseDouble(stockTxt);
                if (stockTxt.isEmpty() || stockDouble != Math.floor(stockDouble)) {
                    stockVal = 0;
                    stockAlerta = true;
                } else if (stockDouble < 0) {
                    stockNegativo = true;
                } else if (stockDouble == 0) {
                    stockVal = 0;
                    stockAlerta = true;
                } else {
                    stockVal = (int) stockDouble;
                }
            } catch (Exception ex) {
                stockVal = 0;
                stockAlerta = true;
            }
            if (stockNegativo) {
                int opt = JOptionPane.showConfirmDialog(this,
                        "El stock no puede ser negativo.\n¿Desea cambiar el valor de stock?", "Stock Negativo",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (opt == JOptionPane.YES_OPTION) {
                    continue;
                } else {
                    return;
                }
            }
            if (stockAlerta) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "El stock ingresado es 0 o inválido.\n¿Está seguro de agregar el "
                                + opciones[tipo].toLowerCase() + " con stock 0?",
                        "Confirmar Stock 0", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm != JOptionPane.YES_OPTION) {
                    continue;
                }
            }
            try {
                ProductoDB.insertarProducto(idServ.getText(), descp.getText(), precioVal, stockVal);
                cargarProductos();
            } catch (Exception ex) {
                String msg = ex.getMessage();
                if (msg != null && msg.contains("ERROR DE VALIDACIÓN")) {
                    String mensaje = msg.split("\n")[0];
                    JOptionPane.showMessageDialog(this, mensaje, "Error de Validación", JOptionPane.ERROR_MESSAGE);
                } else if (msg != null && msg.contains("ERROR DE UNICIDAD")) {
                    String mensaje = msg.split("\n")[0];
                    JOptionPane.showMessageDialog(this, mensaje, "Error de Unicidad", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar producto: " + msg);
                }
            }
            break;
        }
    }

    /**
     * Muestra un formulario para modificar el producto seleccionado.
     */
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
        while (true) {
            int res = JOptionPane.showConfirmDialog(this, campos, "Modificar Producto", JOptionPane.OK_CANCEL_OPTION);
            if (res != JOptionPane.OK_OPTION)
                return;

            if (descp.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo Descripción es obligatorio.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                continue;
            }
            double precioVal = 0;
            try {
                precioVal = Double.parseDouble(precio.getText().trim());
                if (precioVal <= 0)
                    throw new NumberFormatException();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "El campo Precio debe ser un número mayor a 0.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                continue;
            }
            String stockTxt = stock.getText().trim();
            int stockVal = 0;
            boolean stockAlerta = false;
            boolean stockNegativo = false;
            try {
                double stockDouble = Double.parseDouble(stockTxt);
                if (stockTxt.isEmpty() || stockDouble != Math.floor(stockDouble)) {
                    stockVal = 0;
                    stockAlerta = true;
                } else if (stockDouble < 0) {
                    stockNegativo = true;
                } else if (stockDouble == 0) {
                    stockVal = 0;
                    stockAlerta = true;
                } else {
                    stockVal = (int) stockDouble;
                }
            } catch (Exception ex) {
                stockVal = 0;
                stockAlerta = true;
            }
            if (stockNegativo) {
                int opt = JOptionPane.showConfirmDialog(this,
                        "El stock no puede ser negativo.\n¿Desea cambiar el valor de stock?", "Stock Negativo",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (opt == JOptionPane.YES_OPTION) {
                    continue;
                } else {
                    return;
                }
            }
            if (stockAlerta) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "El stock ingresado es 0 o inválido.\n¿Está seguro de modificar el producto con stock 0?",
                        "Confirmar Stock 0", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm != JOptionPane.YES_OPTION) {
                    continue;
                }
            }
            try {
                ProductoDB.modificarProducto(idServ, descp.getText(), precioVal, stockVal);
                cargarProductos();
            } catch (Exception ex) {
                String msg = ex.getMessage();
                if (msg != null && msg.contains("ERROR DE VALIDACIÓN")) {
                    String mensaje = msg.split("\n")[0];
                    JOptionPane.showMessageDialog(this, mensaje, "Error de Validación", JOptionPane.ERROR_MESSAGE);
                } else if (msg != null && msg.contains("ERROR DE UNICIDAD")) {
                    String mensaje = msg.split("\n")[0];
                    JOptionPane.showMessageDialog(this, mensaje, "Error de Unicidad", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error al modificar producto: " + msg);
                }
            }
            break;
        }
    }

    /**
     * Elimina lógicamente el producto seleccionado.
     */
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
                modeloProductos.addRow(prod);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar productos: " + ex.getMessage());
        }
    }

    /**
     * Carga los productos en orden ascendente por precio.
     */
    private void cargarProductosPorPrecioAsc() {
        modeloProductos.setRowCount(0);
        try {
            for (String[] prod : ProductoDB.listarProductosPorPrecioAsc()) {
                modeloProductos.addRow(prod);
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
                modeloProductos.addRow(prod);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al ordenar productos: " + ex.getMessage());
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

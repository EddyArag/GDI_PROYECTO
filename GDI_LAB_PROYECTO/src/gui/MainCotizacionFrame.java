package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import dataBase.DatabaseConnection;
import dataBase.CotizacionDB; // Asegurar importación para DetalleCotizacion
import java.math.BigDecimal; // IMPORTACIÓN CLAVE
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class MainCotizacionFrame extends JFrame {
    // Paneles principales
    private JPanel panelCliente;
    private JPanel panelCotizacion;
    private JPanel panelDetalle;
    private JPanel panelResumen;
    private JPanel panelMenuLateral;

    // Componentes de Cliente
    private JComboBox<String> comboClientes;
    private JTextField txtNombre, txtApellidoP, txtApellidoM, txtRUC, txtObs;

    // Componentes de Cotización
    private JTextField txtFecha, txtCond, txtGarantia, txtTentativa, txtValidez;

    // Componentes de Detalle
    private JTable tablaDetalle;
    private DefaultTableModel modeloDetalle;
    private JButton btnAgregarProducto, btnQuitarProducto;

    // Resumen de costos
    private JLabel lblSubtotal, lblDescuento, lblIGV, lblTotal;

    // Paneles de gestión
    private JPanel panelGestionClientes;
    private JPanel panelGestionProductos;
    private JPanel panelGestionCotizaciones;

    private JButton btnGenerarCotizacion;

    public MainCotizacionFrame() {
        setTitle("Generar Cotización");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Inicializar paneles
        panelCliente = crearPanelCliente();
        panelCotizacion = crearPanelCotizacion();
        panelDetalle = crearPanelDetalle();
        panelResumen = crearPanelResumen();
        panelMenuLateral = crearPanelMenuLateral();

        // Inicializar paneles de gestión
        panelGestionClientes = crearPanelGestionClientes();
        panelGestionProductos = crearPanelGestionProductos();
        panelGestionCotizaciones = crearPanelGestionCotizaciones();

        // Estructura principal
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(panelCliente, BorderLayout.NORTH);
        panelCentral.add(panelCotizacion, BorderLayout.CENTER);
        panelCentral.add(panelDetalle, BorderLayout.SOUTH);

        add(panelMenuLateral, BorderLayout.WEST);
        add(panelCentral, BorderLayout.CENTER);
        add(panelResumen, BorderLayout.EAST);

        cargarClientes();

        // Eventos para menú lateral
        JButton btnClientes = (JButton) panelMenuLateral.getComponent(0);
        JButton btnProductos = (JButton) panelMenuLateral.getComponent(2);
        JButton btnCotizaciones = (JButton) panelMenuLateral.getComponent(4);

        btnClientes.addActionListener(e -> new ClientesFrame().setVisible(true));
        btnProductos.addActionListener(e -> mostrarPanelGestion(panelGestionProductos));
        btnCotizaciones.addActionListener(e -> mostrarPanelGestion(panelGestionCotizaciones));
    }

    private void mostrarPanelGestion(JPanel panelGestion) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(this);
        frame.add(panelGestion);
        frame.setVisible(true);
    }

    private JPanel crearPanelCliente() {
        JPanel panel = new JPanel(new GridLayout(2, 5, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));

        comboClientes = new JComboBox<>();
        comboClientes.addActionListener(e -> cargarDatosCliente());

        txtNombre = new JTextField();
        txtApellidoP = new JTextField();
        txtApellidoM = new JTextField();
        txtRUC = new JTextField();
        txtObs = new JTextField();

        panel.add(new JLabel("Seleccionar Cliente:"));
        panel.add(comboClientes);
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Apellido Paterno:"));
        panel.add(txtApellidoP);
        panel.add(new JLabel("Apellido Materno:"));
        panel.add(txtApellidoM);
        panel.add(new JLabel("RUC:"));
        panel.add(txtRUC);
        panel.add(new JLabel("Observaciones:"));
        panel.add(txtObs);

        // Campos no editables (solo para mostrar datos)
        txtNombre.setEditable(false);
        txtApellidoP.setEditable(false);
        txtApellidoM.setEditable(false);
        txtRUC.setEditable(false);
        txtObs.setEditable(false);

        return panel;
    }

    private JPanel crearPanelCotizacion() {
        JPanel panel = new JPanel(new GridLayout(2, 5, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Datos de la Cotización"));

        txtFecha = new JTextField();
        txtCond = new JTextField();
        txtGarantia = new JTextField();
        txtTentativa = new JTextField();
        txtValidez = new JTextField();

        panel.add(new JLabel("Fecha Emisión:"));
        panel.add(txtFecha);
        panel.add(new JLabel("Condiciones:"));
        panel.add(txtCond);
        panel.add(new JLabel("Garantía:"));
        panel.add(txtGarantia);
        panel.add(new JLabel("Entrega Tentativa:"));
        panel.add(txtTentativa);
        panel.add(new JLabel("Validez Oferta:"));
        panel.add(txtValidez);

        // Puedes agregar más campos según tu modelo

        return panel;
    }

    private JPanel crearPanelDetalle() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Detalle de Cotización"));

        // Agrega columna oculta "ID Producto"
        modeloDetalle = new DefaultTableModel(
                new Object[] { "ID Producto", "Producto", "Cantidad", "Precio Unitario", "Subtotal" }, 0);
        tablaDetalle = new JTable(modeloDetalle);
        // Oculta la columna "ID Producto" visualmente
        tablaDetalle.getColumnModel().getColumn(0).setMinWidth(0);
        tablaDetalle.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaDetalle.getColumnModel().getColumn(0).setWidth(0);

        btnAgregarProducto = new JButton("Agregar Producto");
        btnQuitarProducto = new JButton("Quitar Producto");

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnAgregarProducto);
        panelBotones.add(btnQuitarProducto);

        panel.add(new JScrollPane(tablaDetalle), BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);

        btnAgregarProducto.addActionListener(e -> abrirProductosFrame());
        btnQuitarProducto.addActionListener(e -> quitarProductoSeleccionado());

        return panel;
    }

    private void abrirProductosFrame() {
        ProductosFrame frame = new ProductosFrame((idServ, nombre, precio, cantidad) -> {
            agregarProductoADetalle(idServ, nombre, precio, cantidad);
        });
        frame.setVisible(true);
    }

    private void agregarProductoADetalle(String idServ, String nombre, double precio, int cantidad) {
        double subtotal = precio * cantidad;
        modeloDetalle.addRow(new Object[] { idServ, nombre, cantidad, precio, subtotal });
        actualizarResumen();
    }

    private void quitarProductoSeleccionado() {
        int fila = tablaDetalle.getSelectedRow();
        if (fila != -1) {
            modeloDetalle.removeRow(fila);
            actualizarResumen();
        }
    }

    private void actualizarResumen() {
        double subtotal = 0;
        for (int i = 0; i < modeloDetalle.getRowCount(); i++) {
            subtotal += Double.parseDouble(modeloDetalle.getValueAt(i, 4).toString());
        }
        double descuento = 0.0; // Si tienes campo de descuento, úsalo aquí
        double igv = 0.18; // Valor por defecto, puedes obtenerlo de la BD si lo necesitas
        double base = subtotal - descuento;
        double igvCalc = base * igv;
        double total = base + igvCalc;

        lblSubtotal.setText("Subtotal: S/ " + String.format("%.2f", subtotal));
        lblDescuento.setText("Descuento: S/ " + String.format("%.2f", descuento));
        lblIGV.setText("IGV: S/ " + String.format("%.2f", igvCalc));
        lblTotal.setText("Total: S/ " + String.format("%.2f", total));
    }

    private JPanel crearPanelResumen() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Resumen de Costos"));

        lblSubtotal = new JLabel("Subtotal: S/ 0.00");
        lblDescuento = new JLabel("Descuento: S/ 0.00");
        lblIGV = new JLabel("IGV: S/ 0.00");
        lblTotal = new JLabel("Total: S/ 0.00");

        panel.add(lblSubtotal);
        panel.add(lblDescuento);
        panel.add(lblIGV);
        panel.add(lblTotal);

        btnGenerarCotizacion = new JButton("Generar Cotización");
        panel.add(btnGenerarCotizacion);

        btnGenerarCotizacion.addActionListener(e -> generarCotizacion());

        return panel;
    }

    private JPanel crearPanelMenuLateral() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Menú"));

        JButton btnClientes = new JButton("Clientes");
        JButton btnProductos = new JButton("Servicios/Productos");
        JButton btnCotizaciones = new JButton("Cotizaciones");

        panel.add(btnClientes);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnProductos);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnCotizaciones);

        // Eventos para cambiar de vista (implementación posterior)
        // btnClientes.addActionListener(...);
        // btnProductos.addActionListener(...);
        // btnCotizaciones.addActionListener(...);

        return panel;
    }

    private JPanel crearPanelGestionClientes() {
        JPanel panel = new ClientesPanel();
        return panel;
    }

    private JPanel crearPanelGestionProductos() {
        JPanel panel = new ProductosGestionPanel();
        return panel;
    }

    private JPanel crearPanelGestionCotizaciones() {
        JPanel panel = new CotizacionesGestionPanel();
        return panel;
    }

    // Cargar clientes desde la base de datos usando FN_LISTAR_CLIENTES
    private void cargarClientes() {
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id_cli, nombre_completo FROM FN_LISTAR_CLIENTES()")) {
            comboClientes.removeAllItems();
            while (rs.next()) {
                comboClientes.addItem(rs.getInt("id_cli") + " - " + rs.getString("nombre_completo"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + ex.getMessage());
        }
    }

    // Cargar datos del cliente seleccionado
    private void cargarDatosCliente() {
        String seleccionado = (String) comboClientes.getSelectedItem();
        if (seleccionado == null || !seleccionado.contains(" - "))
            return;
        int idCli = Integer.parseInt(seleccionado.split(" - ")[0]);
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM Cliente WHERE ID_CLI = ?")) {
            ps.setInt(1, idCli);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtNombre.setText(rs.getString("P_NOMB"));
                txtApellidoP.setText(rs.getString("APE_P"));
                txtApellidoM.setText(rs.getString("APE_M"));
                txtRUC.setText(rs.getString("RUC"));
                txtObs.setText(rs.getString("OBS"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos del cliente: " + ex.getMessage());
        }
    }

    private void generarCotizacion() {
        String ncot = JOptionPane.showInputDialog(this, "Ingrese el número de cotización:");
        if (ncot == null || ncot.trim().isEmpty())
            return;

        String seleccionado = (String) comboClientes.getSelectedItem();
        if (seleccionado == null || !seleccionado.contains(" - ")) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente.");
            return;
        }
        int idCli = Integer.parseInt(seleccionado.split(" - ")[0]);
        int idEmp = 1; // Puedes obtener el ID de empresa según tu lógica

        // Validación de fechas
        String fechaStr = txtFecha.getText().trim();
        String validezStr = txtValidez.getText().trim();
        if (fechaStr.isEmpty() || validezStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar ambas fechas (emisión y validez) en formato yyyy-MM-dd.",
                    "Error de Fecha", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Date femi, vofer;
        try {
            femi = Date.valueOf(fechaStr);
            vofer = Date.valueOf(validezStr);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha incorrecto. Use yyyy-MM-dd.", "Error de Fecha",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        double desct = 0.0; // Puedes obtenerlo de un campo
        String cond = txtCond.getText();
        String gara = txtGarantia.getText();
        String tent = txtTentativa.getText();

        java.util.List<dataBase.CotizacionDB.DetalleCotizacion> detalles = new java.util.ArrayList<>();
        for (int i = 0; i < modeloDetalle.getRowCount(); i++) {
            String idServ = modeloDetalle.getValueAt(i, 0).toString();
            int cantidad = Integer.parseInt(modeloDetalle.getValueAt(i, 2).toString());
            detalles.add(new dataBase.CotizacionDB.DetalleCotizacion(idServ, cantidad));
        }

        try {
            dataBase.CotizacionDB.crearCotizacionCompleta(ncot, idCli, idEmp, femi, desct, cond, gara, tent, vofer,
                    detalles);
            JOptionPane.showMessageDialog(this, "Cotización generada correctamente.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al generar cotización: " + ex.getMessage());
        }
    }

    // ...puedes agregar métodos para cargar productos, calcular totales, etc...

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainCotizacionFrame().setVisible(true);
        });
    }
}

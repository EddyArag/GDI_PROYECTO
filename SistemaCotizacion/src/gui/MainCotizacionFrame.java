package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import dataBase.DatabaseConnection;
import exportador.ExportarCotizacionPDF;
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
    private JButton btnActualizarClientes; // Botón actualizar clientes

    // Componentes de Cotización
    private JTextField txtFecha, txtCond, txtTentativa, txtValidez;
    private JTextField txtDescuento; // Campo para descuento

    // Componentes de Detalle
    private JTable tablaDetalle;
    private DefaultTableModel modeloDetalle;
    private JButton btnAgregarProducto, btnQuitarProducto;
    private JButton btnUsarCotizacionAnterior; // Nuevo botón

    // Resumen de costos
    private JLabel lblSubtotal, lblDescuento, lblIGV, lblTotal;
    private JLabel lblLogo; // Logo empresa

    // Paneles de gestión
    private JPanel panelGestionClientes;
    private JPanel panelGestionProductos;
    private JPanel panelGestionCotizaciones;
    private JPanel panelGestionEmpresa;
    private JPanel panelGestionBackup; // Nuevo panel
    private JPanel panelGestionReportes; // <--- NUEVO

    private JButton btnGenerarCotizacion;

    // Cambia la variable para que sea persistente en la clase
    private int clienteSeleccionadoIndex = -1;

    // Colores y fuentes para la estética
    private Color colorFondoPanel = new Color(220, 235, 250); // celeste claro
    private Color colorBorde = new Color(100, 160, 220); // celeste más fuerte
    private Font fuenteCampos = new Font("Segoe UI", Font.PLAIN, 16);

    // Reemplaza txtGarantia por areaGarantia
    private JTextArea areaGarantia;

    public MainCotizacionFrame() {
        setTitle("Generar Cotización");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700); // Más ancho para barra horizontal
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Logo empresa (más grande, esquina superior izquierda)
        lblLogo = new JLabel();
        lblLogo.setPreferredSize(new Dimension(180, 120)); // Más grande
        cargarLogoEmpresa();

        JPanel panelLogo = new JPanel(new BorderLayout());
        panelLogo.setBackground(colorFondoPanel);
        panelLogo.add(lblLogo, BorderLayout.WEST);

        // Inicializar paneles
        panelCliente = crearPanelCliente();
        panelCotizacion = crearPanelCotizacion();
        panelDetalle = crearPanelDetalle();
        panelResumen = crearPanelResumen();

        // Inicializar paneles de gestión
        panelGestionClientes = crearPanelGestionClientes();
        panelGestionProductos = crearPanelGestionProductos();
        panelGestionCotizaciones = crearPanelGestionCotizaciones();
        panelGestionEmpresa = crearPanelGestionEmpresa();
        panelGestionBackup = crearPanelGestionBackup();
        panelGestionReportes = crearPanelGestionReportes(); // <--- NUEVO

        // Barra de menú horizontal
        JPanel barraMenu = crearBarraMenuHorizontal();

        // Estructura principal
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(panelLogo, BorderLayout.WEST);
        panelCentral.add(panelCliente, BorderLayout.CENTER);
        panelCentral.add(panelCotizacion, BorderLayout.SOUTH);

        JPanel panelMain = new JPanel(new BorderLayout());
        panelMain.add(panelCentral, BorderLayout.NORTH);
        panelMain.add(panelDetalle, BorderLayout.CENTER);

        add(barraMenu, BorderLayout.NORTH); // Barra horizontal arriba
        add(panelMain, BorderLayout.CENTER);
        add(panelResumen, BorderLayout.EAST);

        cargarClientes();

        // Eventos para barra de menú horizontal
        JButton btnClientes = (JButton) barraMenu.getComponent(0);
        JButton btnProductos = (JButton) barraMenu.getComponent(1);
        JButton btnCotizaciones = (JButton) barraMenu.getComponent(2);
        JButton btnEmpresa = (JButton) barraMenu.getComponent(3);
        JButton btnBackup = (JButton) barraMenu.getComponent(4);
        JButton btnReportes = (JButton) barraMenu.getComponent(5); // <--- NUEVO

        btnClientes.addActionListener(e -> mostrarPanelGestionUnico(ClientesPanel.class, panelGestionClientes));
        btnProductos.addActionListener(e -> mostrarPanelGestion(panelGestionProductos));
        btnCotizaciones.addActionListener(e -> mostrarPanelGestion(panelGestionCotizaciones));
        btnEmpresa.addActionListener(e -> mostrarPanelGestion(panelGestionEmpresa));
        btnBackup.addActionListener(e -> mostrarPanelGestion(panelGestionBackup));
        btnReportes.addActionListener(e -> mostrarPanelGestion(panelGestionReportes)); // <--- NUEVO

        // Actualiza clientes al ganar foco la ventana principal
        this.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                cargarClientes();
            }
        });
    }

    private JPanel crearBarraMenuHorizontal() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 6, 0, 0)); // 6 botones ahora
        panel.setBackground(new Color(200, 220, 245));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, colorBorde));

        String[] nombres = { "Clientes", "Servicios/Productos", "Cotizaciones", "Empresa", "Backup/Restore",
                "Reportes" };
        for (String nombre : nombres) {
            JButton btn = new JButton(nombre);
            btn.setBackground(colorBorde);
            btn.setForeground(Color.WHITE);
            btn.setFont(fuenteCampos);
            btn.setFocusPainted(false);
            // Borde suave y visible para cada botón
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(80, 130, 200), 2, true),
                    BorderFactory.createEmptyBorder(12, 0, 12, 0)));
            panel.add(btn);
        }
        return panel;
    }

    private void cargarLogoEmpresa() {
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT logo FROM Empresa LIMIT 1")) {
            if (rs.next()) {
                byte[] logoBytes = rs.getBytes("logo");
                if (logoBytes != null && logoBytes.length > 0) {
                    ImageIcon icon = new ImageIcon(logoBytes);
                    Image img = icon.getImage().getScaledInstance(180, 120, Image.SCALE_SMOOTH); // Más grande
                    lblLogo.setIcon(new ImageIcon(img));
                }
            }
        } catch (SQLException ex) {
            lblLogo.setText("Sin logo");
        }
    }

    private void mostrarPanelGestion(JPanel panelGestion) {
        // Evita abrir múltiples ventanas del mismo panel
        for (Window window : Window.getWindows()) {
            if (window instanceof JFrame) {
                JFrame frame = (JFrame) window;
                if (frame.isVisible() && frame.getContentPane().getComponentCount() > 0 &&
                        frame.getContentPane().getComponent(0) == panelGestion) {
                    frame.toFront();
                    return;
                }
            }
        }
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(this);
        frame.add(panelGestion);
        frame.setVisible(true);
    }

    // Solo para ClientesPanel: evita abrir dos veces el mismo panel
    private void mostrarPanelGestionUnico(Class<?> panelClass, JPanel panelGestion) {
        for (Window window : Window.getWindows()) {
            if (window instanceof JFrame) {
                JFrame frame = (JFrame) window;
                if (frame.isVisible() && frame.getContentPane().getComponentCount() > 0 &&
                        panelClass.isInstance(frame.getContentPane().getComponent(0))) {
                    frame.toFront();
                    return;
                }
            }
        }
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(this);
        frame.add(panelGestion);
        frame.setVisible(true);
    }

    private JPanel crearPanelCliente() {
        JPanel panel = new JPanel(new GridLayout(2, 5, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(colorBorde, 2, true), "Datos del Cliente"));
        panel.setBackground(colorFondoPanel);

        comboClientes = new JComboBox<>();
        comboClientes.addActionListener(e -> cargarDatosCliente());
        comboClientes.setFont(fuenteCampos);

        txtNombre = crearCampoTexto();
        txtApellidoP = crearCampoTexto();
        txtApellidoM = crearCampoTexto();
        txtRUC = crearCampoTexto();
        txtObs = crearCampoTexto();

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

        for (Component c : panel.getComponents()) {
            if (c instanceof JLabel)
                ((JLabel) c).setFont(fuenteCampos);
            if (c instanceof JTextField)
                ((JTextField) c).setEditable(false);
        }
        return panel;
    }

    // Clase auxiliar para placeholder en JTextField (solo para cotización)
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

    // Clase auxiliar para placeholder en JTextArea
    class PlaceholderTextArea extends JTextArea {
        private String placeholder;

        public PlaceholderTextArea(String placeholder, int rows, int cols) {
            super(rows, cols);
            this.placeholder = placeholder;
            setLineWrap(true);
            setWrapStyleWord(true);
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !(FocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == this)) {
                g.setColor(Color.GRAY);
                g.drawString(placeholder, 5, getFontMetrics(getFont()).getHeight());
            }
        }
    }

    private JPanel crearPanelCotizacion() {
        JPanel panel = new JPanel(new GridLayout(2, 6, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(colorBorde, 2, true), "Datos de la Cotización"));
        panel.setBackground(colorFondoPanel);

        txtFecha = new PlaceholderTextField("AAAA-MM-DD");
        txtCond = new PlaceholderTextField("Ej: Pago contado, transferencia...");
        areaGarantia = new JTextArea(2, 25);
        areaGarantia.setFont(fuenteCampos);
        areaGarantia.setLineWrap(true);
        areaGarantia.setWrapStyleWord(true);
        areaGarantia.setBackground(Color.WHITE);
        areaGarantia.setBorder(BorderFactory.createLineBorder(colorBorde, 1, true));
        areaGarantia.setText("Servicio/Piezas: 6 meses, Equipo completo: 1 año");

        txtTentativa = new PlaceholderTextField("Ej: 7 días hábiles");
        txtValidez = new PlaceholderTextField("AAAA-MM-DD");
        txtDescuento = new PlaceholderTextField("0.00");

        txtCond.setColumns(15);
        txtTentativa.setColumns(15);
        txtValidez.setColumns(10);
        txtDescuento.setColumns(8);

        txtCond.setPreferredSize(new Dimension(180, 28));
        areaGarantia.setPreferredSize(new Dimension(300, 48));
        txtTentativa.setPreferredSize(new Dimension(180, 28));
        txtValidez.setPreferredSize(new Dimension(120, 28));
        txtDescuento.setPreferredSize(new Dimension(100, 28));

        txtFecha.setText(java.time.LocalDate.now().toString());

        panel.add(new JLabel("Fecha Emisión:"));
        panel.add(txtFecha);
        panel.add(new JLabel("Condiciones:"));
        panel.add(txtCond);
        panel.add(new JLabel("Garantía:"));
        panel.add(areaGarantia);
        panel.add(new JLabel("Entrega Tentativa:"));
        panel.add(txtTentativa);
        panel.add(new JLabel("Validez Oferta:"));
        panel.add(txtValidez);
        panel.add(new JLabel("Descuento S/:"));
        panel.add(txtDescuento);

        // Actualiza el resumen cuando cambia el descuento
        txtDescuento.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                actualizarResumen();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                actualizarResumen();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                actualizarResumen();
            }
        });

        for (Component c : panel.getComponents()) {
            if (c instanceof JLabel)
                ((JLabel) c).setFont(fuenteCampos);
        }
        return panel;
    }

    private JPanel crearPanelDetalle() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(colorBorde, 2, true), "Detalle de Cotización"));
        panel.setBackground(colorFondoPanel);

        modeloDetalle = new DefaultTableModel(
                new Object[] { "ID Producto", "Producto", "Cantidad", "Precio Unitario", "Subtotal" }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 2;
            }
        };
        tablaDetalle = new JTable(modeloDetalle);
        tablaDetalle.setFont(fuenteCampos);
        tablaDetalle.setRowHeight(28);
        tablaDetalle.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        tablaDetalle.setBackground(Color.WHITE);

        btnAgregarProducto = new JButton("Agregar Producto");
        btnQuitarProducto = new JButton("Quitar Producto");
        btnUsarCotizacionAnterior = new JButton("Usar Cotización Anterior"); // Mueve aquí
        btnAgregarProducto.setBackground(colorBorde);
        btnQuitarProducto.setBackground(colorBorde);
        btnUsarCotizacionAnterior.setBackground(colorBorde);
        btnAgregarProducto.setForeground(Color.WHITE);
        btnQuitarProducto.setForeground(Color.WHITE);
        btnUsarCotizacionAnterior.setForeground(Color.WHITE);
        btnAgregarProducto.setFont(fuenteCampos);
        btnQuitarProducto.setFont(fuenteCampos);
        btnUsarCotizacionAnterior.setFont(fuenteCampos);

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(colorFondoPanel);
        panelBotones.add(btnAgregarProducto);
        panelBotones.add(btnQuitarProducto);
        panelBotones.add(btnUsarCotizacionAnterior); // Aquí junto a los otros

        panel.add(new JScrollPane(tablaDetalle), BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);

        btnAgregarProducto.addActionListener(e -> abrirProductosFrame());
        btnQuitarProducto.addActionListener(e -> quitarProductoSeleccionado());
        btnUsarCotizacionAnterior.addActionListener(e -> abrirPanelCargarPlantillaCotizacion());

        tablaDetalle.getModel().addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (col == 2 && row >= 0) {
                try {
                    int nuevaCantidad = Integer.parseInt(tablaDetalle.getValueAt(row, 2).toString());
                    int stock = obtenerStockProducto(tablaDetalle.getValueAt(row, 0).toString());
                    if (nuevaCantidad <= 0 || nuevaCantidad > stock) {
                        JOptionPane.showMessageDialog(this, "Cantidad inválida o excede el stock (" + stock + ").");
                        tablaDetalle.setValueAt(1, row, 2);
                    } else {
                        double precio = Double.parseDouble(tablaDetalle.getValueAt(row, 3).toString());
                        tablaDetalle.setValueAt(precio * nuevaCantidad, row, 4);
                        actualizarResumen();
                    }
                } catch (Exception ex) {
                    tablaDetalle.setValueAt(1, row, 2);
                }
            }
        });

        return panel;
    }

    private JPanel crearPanelResumen() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 8, 8));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(colorBorde, 2, true), "Resumen de Costos"));
        panel.setBackground(colorFondoPanel);

        lblSubtotal = new JLabel("Subtotal: S/ 0.00");
        lblDescuento = new JLabel("Descuento: S/ 0.00");
        lblIGV = new JLabel("IGV: S/ 0.00");
        lblTotal = new JLabel("Total: S/ 0.00");

        lblSubtotal.setFont(fuenteCampos);
        lblDescuento.setFont(fuenteCampos);
        lblIGV.setFont(fuenteCampos);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));

        panel.add(lblSubtotal);
        panel.add(lblDescuento);
        panel.add(lblIGV);
        panel.add(lblTotal);

        btnGenerarCotizacion = new JButton("Generar Cotización");
        btnGenerarCotizacion.setBackground(colorBorde);
        btnGenerarCotizacion.setForeground(Color.WHITE);
        btnGenerarCotizacion.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(btnGenerarCotizacion);

        btnGenerarCotizacion.addActionListener(e -> generarCotizacion());

        return panel;
    }

    private JPanel crearPanelMenuLateral() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(colorBorde, 2, true), "Menú"));
        panel.setBackground(new Color(200, 220, 245));

        JButton btnClientes = new JButton("Clientes");
        JButton btnProductos = new JButton("Servicios/Productos");
        JButton btnCotizaciones = new JButton("Cotizaciones");
        JButton btnEmpresa = new JButton("Empresa");
        JButton btnBackup = new JButton("Backup/Restore"); // Nuevo botón

        btnClientes.setBackground(colorBorde);
        btnProductos.setBackground(colorBorde);
        btnCotizaciones.setBackground(colorBorde);
        btnEmpresa.setBackground(colorBorde);
        btnBackup.setBackground(colorBorde);
        btnClientes.setForeground(Color.WHITE);
        btnProductos.setForeground(Color.WHITE);
        btnCotizaciones.setForeground(Color.WHITE);
        btnEmpresa.setForeground(Color.WHITE);
        btnBackup.setForeground(Color.WHITE);
        btnClientes.setFont(fuenteCampos);
        btnProductos.setFont(fuenteCampos);
        btnCotizaciones.setFont(fuenteCampos);
        btnEmpresa.setFont(fuenteCampos);
        btnBackup.setFont(fuenteCampos);

        panel.add(btnClientes);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnProductos);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnCotizaciones);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnEmpresa);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnBackup); // Agrega el botón Backup/Restore abajo

        btnClientes.addActionListener(e -> mostrarPanelGestion(panelGestionClientes));
        btnProductos.addActionListener(e -> mostrarPanelGestion(panelGestionProductos));
        btnCotizaciones.addActionListener(e -> mostrarPanelGestion(panelGestionCotizaciones));
        btnEmpresa.addActionListener(e -> mostrarPanelGestion(panelGestionEmpresa));
        btnBackup.addActionListener(e -> mostrarPanelGestion(panelGestionBackup)); // Evento para Backup/Restore

        return panel;
    }

    // Utilidad para crear campos de texto grandes y estéticos
    private JTextField crearCampoTexto() {
        JTextField txt = new JTextField();
        txt.setFont(fuenteCampos);
        txt.setPreferredSize(new Dimension(300, 32));
        txt.setBackground(Color.WHITE);
        txt.setBorder(BorderFactory.createLineBorder(colorBorde, 1, true));
        return txt;
    }

    private int obtenerStockProducto(String idServ) {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT stock FROM Servicio_Producto WHERE id_serv = ?")) {
            ps.setString(1, idServ);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("stock");
            }
        } catch (SQLException ex) {
            // Si hay error, por defecto 1
        }
        return 1;
    }

    private void abrirProductosFrame() {
        // Guarda el índice seleccionado antes de abrir el frame de productos
        clienteSeleccionadoIndex = comboClientes.getSelectedIndex();
        ProductosFrame frame = new ProductosFrame((idServ, nombre, precio, cantidad) -> {
            agregarProductoADetalle(idServ, nombre, precio, cantidad);
            // Al volver, restaura el cliente seleccionado
            comboClientes.setSelectedIndex(clienteSeleccionadoIndex);
        });
        frame.setVisible(true);
    }

    private void agregarProductoADetalle(String idServ, String nombre, double precio, int cantidad) {
        // Verifica si el producto ya está en el detalle
        int filaExistente = -1;
        int cantidadExistente = 0;
        for (int i = 0; i < modeloDetalle.getRowCount(); i++) {
            if (modeloDetalle.getValueAt(i, 0).toString().equals(idServ)) {
                filaExistente = i;
                cantidadExistente = Integer.parseInt(modeloDetalle.getValueAt(i, 2).toString());
                break;
            }
        }
        int stock = obtenerStockProducto(idServ);
        int nuevaCantidad = cantidad;
        if (filaExistente != -1) {
            nuevaCantidad += cantidadExistente;
            if (nuevaCantidad > stock) {
                JOptionPane.showMessageDialog(this,
                        "La cantidad total (" + nuevaCantidad + ") excede el stock disponible (" + stock + ").",
                        "Error de Stock", JOptionPane.ERROR_MESSAGE);
                return;
            }
            modeloDetalle.setValueAt(nuevaCantidad, filaExistente, 2);
            modeloDetalle.setValueAt(precio, filaExistente, 3);
            modeloDetalle.setValueAt(precio * nuevaCantidad, filaExistente, 4);
        } else {
            if (cantidad > stock) {
                JOptionPane.showMessageDialog(this,
                        "La cantidad solicitada excede el stock disponible (" + stock + ").", "Error de Stock",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            double subtotal = precio * cantidad;
            modeloDetalle.addRow(new Object[] { idServ, nombre, cantidad, precio, subtotal });
        }
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
        double descuento = 0.0;
        try {
            descuento = Double.parseDouble(txtDescuento.getText());
        } catch (Exception ex) {
            descuento = 0.0;
        }
        for (int i = 0; i < modeloDetalle.getRowCount(); i++) {
            subtotal += Double.parseDouble(modeloDetalle.getValueAt(i, 4).toString());
        }
        double igv = 0.18;
        double base = subtotal - descuento;
        double igvCalc = base * igv;
        double total = base + igvCalc;

        lblSubtotal.setText("Subtotal: S/ " + String.format("%.2f", subtotal));
        lblDescuento.setText("Descuento: S/ " + String.format("%.2f", descuento));
        lblIGV.setText("IGV: S/ " + String.format("%.2f", igvCalc));
        lblTotal.setText("Total: S/ " + String.format("%.2f", total));
    }

    // Cargar clientes desde la base de datos usando FN_LISTAR_CLIENTES
    private void cargarClientes() {
        int prevIndex = clienteSeleccionadoIndex;
        comboClientes.removeAllItems();
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id_cli, nombre_completo FROM FN_LISTAR_CLIENTES()")) {
            while (rs.next()) {
                comboClientes.addItem(rs.getInt("id_cli") + " - " + rs.getString("nombre_completo"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + ex.getMessage());
        }
        // Mantén la selección previa si existe
        if (prevIndex >= 0 && prevIndex < comboClientes.getItemCount()) {
            comboClientes.setSelectedIndex(prevIndex);
        } else if (comboClientes.getItemCount() > 0 && comboClientes.getSelectedIndex() == -1) {
            comboClientes.setSelectedIndex(0);
        }
    }

    // Cargar datos del cliente seleccionado
    private void cargarDatosCliente() {
        clienteSeleccionadoIndex = comboClientes.getSelectedIndex();
        String seleccionado = (String) comboClientes.getSelectedItem();
        if (seleccionado == null || !seleccionado.contains(" -")) {
            txtNombre.setText("");
            txtApellidoP.setText("");
            txtApellidoM.setText("");
            txtRUC.setText("");
            txtObs.setText("");
            return;
        }
        int idCli = Integer.parseInt(seleccionado.split(" -")[0]);
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
        String ncot;
        try {
            ncot = dataBase.CotizacionDB.generarNumeroCotizacion();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al generar número de cotización: " + ex.getMessage());
            return;
        }

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
        Date femi = null;
        Date vofer = null;
        try {
            femi = fechaStr.isEmpty() ? null : Date.valueOf(fechaStr);
            vofer = validezStr.isEmpty() ? null : Date.valueOf(validezStr);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha incorrecto. Use yyyy-MM-dd.", "Error de Fecha",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        double desct = 0.0;
        try {
            desct = Double.parseDouble(txtDescuento.getText());
        } catch (Exception ex) {
            desct = 0.0;
        }
        String cond = txtCond.getText().isEmpty() ? null : txtCond.getText();
        String gara = areaGarantia.getText().trim().isEmpty() ? null : areaGarantia.getText().trim();
        String tent = txtTentativa.getText().isEmpty() ? null : txtTentativa.getText();

        java.util.List<dataBase.CotizacionDB.DetalleCotizacion> detalles = new java.util.ArrayList<>();
        for (int i = 0; i < modeloDetalle.getRowCount(); i++) {
            String idServ = modeloDetalle.getValueAt(i, 0).toString();
            int cantidad = Integer.parseInt(modeloDetalle.getValueAt(i, 2).toString());
            detalles.add(new dataBase.CotizacionDB.DetalleCotizacion(idServ, cantidad));
        }

        try {
            CotizacionDB.crearCotizacionCompleta(ncot, idCli, idEmp, femi, desct, cond, gara, tent, vofer, detalles);
            JOptionPane.showMessageDialog(this, "Cotización generada correctamente.");
            int res = JOptionPane.showConfirmDialog(this, "¿Desea exportar la cotización en PDF?", "Exportar PDF",
                    JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                try {
                    exportador.ExportarCotizacionPDF.exportar(ncot);
                    JOptionPane.showMessageDialog(this, "Cotización exportada a PDF correctamente.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al exportar PDF: " + ex.getMessage());
                }
            }
        } catch (SQLException ex) {
            String msg = ex.getMessage();
            if (msg != null && msg.contains("ERROR DE VALIDACIÓN")) {
                String mensaje = msg.split("\n")[0];
                JOptionPane.showMessageDialog(this, mensaje, "Error de Validación", JOptionPane.ERROR_MESSAGE);
            } else if (msg != null && msg.contains("ERROR DE UNICIDAD")) {
                String mensaje = msg.split("\n")[0];
                JOptionPane.showMessageDialog(this, mensaje, "Error de Unicidad", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al generar cotización: " + msg);
            }
        }
    }

    private JPanel crearPanelGestionClientes() {
        // Puedes personalizar el panel según tu lógica
        return new ClientesPanel();
    }

    private JPanel crearPanelGestionProductos() {
        // Puedes personalizar el panel según tu lógica
        return new ProductosGestionPanel();
    }

    private JPanel crearPanelGestionCotizaciones() {
        // Puedes personalizar el panel según tu lógica
        return new CotizacionesGestionPanel();
    }

    private JPanel crearPanelGestionEmpresa() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(colorBorde, 2, true), "Gestión de Empresa"));
        panel.setBackground(colorFondoPanel);

        JLabel lblLogo = new JLabel("Logo:");
        JButton btnCambiarLogo = new JButton("Cambiar Logo");
        JLabel lblDireccion = new JLabel("Dirección:");
        JTextField txtDireccion = new JTextField();
        JButton btnGuardarDireccion = new JButton("Guardar Dirección");
        JLabel lblTelefono = new JLabel("Teléfono 1:");
        JTextField txtTelefono1 = new JTextField();
        JLabel lblTelefono2 = new JLabel("Teléfono 2:");
        JTextField txtTelefono2 = new JTextField();
        JButton btnGuardarTelefonos = new JButton("Guardar Teléfonos");
        JLabel lblMail = new JLabel("Mail:");
        JTextField txtMail = new JTextField();
        JButton btnGuardarMail = new JButton("Guardar Mail");

        // Estética para campos y botones
        Font fontLabel = fuenteCampos;
        Font fontField = fuenteCampos;
        Font fontButton = new Font("Segoe UI", Font.BOLD, 15);

        int anchoCampo = 600;
        int altoCampo = 36;

        for (JLabel lbl : new JLabel[] { lblLogo, lblDireccion, lblTelefono, lblTelefono2, lblMail }) {
            lbl.setFont(fontLabel);
        }
        for (JTextField txt : new JTextField[] { txtDireccion, txtTelefono1, txtTelefono2, txtMail }) {
            txt.setFont(fontField);
            txt.setMaximumSize(new Dimension(anchoCampo, altoCampo));
            txt.setPreferredSize(new Dimension(anchoCampo, altoCampo));
            txt.setMinimumSize(new Dimension(anchoCampo, altoCampo));
            txt.setBorder(BorderFactory.createLineBorder(colorBorde, 1, true));
            txt.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        for (JButton btn : new JButton[] { btnCambiarLogo, btnGuardarDireccion, btnGuardarTelefonos, btnGuardarMail }) {
            btn.setBackground(colorBorde);
            btn.setForeground(Color.WHITE);
            btn.setFont(fontButton);
            btn.setFocusPainted(false);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        // Cargar datos actuales de la empresa (ID_EMP = 1)
        try {
            List<String[]> direcciones = dataBase.EmpresaDB.getDireccionesEmpresa(1);
            if (!direcciones.isEmpty()) {
                txtDireccion.setText(direcciones.get(0)[1]);
            }
            List<String[]> telefonos = dataBase.EmpresaDB.getTelefonosEmpresa(1);
            if (telefonos.size() > 0)
                txtTelefono1.setText(telefonos.get(0)[1]);
            if (telefonos.size() > 1)
                txtTelefono2.setText(telefonos.get(1)[1]);
            List<String[]> mails = dataBase.EmpresaDB.getMailsEmpresa(1);
            if (!mails.isEmpty()) {
                txtMail.setText(mails.get(0)[1]);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel, "Error al cargar datos de empresa: " + ex.getMessage());
        }

        // Layout con espacios y alineación
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblLogo);
        panel.add(Box.createVerticalStrut(4));
        panel.add(btnCambiarLogo);
        panel.add(Box.createVerticalStrut(16));
        panel.add(lblDireccion);
        panel.add(Box.createVerticalStrut(4));
        panel.add(txtDireccion);
        panel.add(Box.createVerticalStrut(4));
        panel.add(btnGuardarDireccion);
        panel.add(Box.createVerticalStrut(16));
        panel.add(lblTelefono);
        panel.add(Box.createVerticalStrut(4));
        panel.add(txtTelefono1);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblTelefono2);
        panel.add(Box.createVerticalStrut(4));
        panel.add(txtTelefono2);
        panel.add(Box.createVerticalStrut(4));
        panel.add(btnGuardarTelefonos);
        panel.add(Box.createVerticalStrut(16));
        panel.add(lblMail);
        panel.add(Box.createVerticalStrut(4));
        panel.add(txtMail);
        panel.add(Box.createVerticalStrut(4));
        panel.add(btnGuardarMail);
        panel.add(Box.createVerticalGlue());

        btnCambiarLogo.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
                java.io.File file = chooser.getSelectedFile();
                try {
                    byte[] logoBytes = java.nio.file.Files.readAllBytes(file.toPath());
                    dataBase.EmpresaDB.modificarLogoEmpresa(1, logoBytes);
                    JOptionPane.showMessageDialog(panel, "Logo actualizado correctamente.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error al actualizar logo: " + ex.getMessage());
                }
            }
        });

        btnGuardarDireccion.addActionListener(e -> {
            String dir = txtDireccion.getText().trim();
            if (!dir.isEmpty()) {
                try (Connection conn = dataBase.DatabaseConnection.getConnection();
                        CallableStatement cs = conn.prepareCall("{ call SP_MODIFICAR_DIR_EMPRESA(?, ?, ?) }")) {
                    List<String[]> direcciones = dataBase.EmpresaDB.getDireccionesEmpresa(1);
                    int idDiremp = !direcciones.isEmpty() ? Integer.parseInt(direcciones.get(0)[0]) : 0;
                    cs.setInt(1, idDiremp);
                    cs.setInt(2, 1);
                    cs.setString(3, dir);
                    cs.execute();
                    JOptionPane.showMessageDialog(panel, "Dirección actualizada correctamente.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error al actualizar dirección: " + ex.getMessage());
                }
            }
        });

        btnGuardarTelefonos.addActionListener(e -> {
            String tel1 = txtTelefono1.getText().trim();
            String tel2 = txtTelefono2.getText().trim();
            try (Connection conn = dataBase.DatabaseConnection.getConnection()) {
                List<String[]> telefonos = dataBase.EmpresaDB.getTelefonosEmpresa(1);
                // Teléfono 1
                if (!tel1.isEmpty() && telefonos.size() > 0) {
                    try (CallableStatement cs = conn.prepareCall("{ call SP_MODIFICAR_TEL_EMPRESA(?, ?, ?) }")) {
                        int idTelemp1 = Integer.parseInt(telefonos.get(0)[0]);
                        cs.setInt(1, idTelemp1);
                        cs.setInt(2, 1);
                        cs.setString(3, tel1);
                        cs.execute();
                    }
                }
                // Teléfono 2
                if (!tel2.isEmpty() && telefonos.size() > 1) {
                    try (CallableStatement cs = conn.prepareCall("{ call SP_MODIFICAR_TEL_EMPRESA(?, ?, ?) }")) {
                        int idTelemp2 = Integer.parseInt(telefonos.get(1)[0]);
                        cs.setInt(1, idTelemp2);
                        cs.setInt(2, 1);
                        cs.setString(3, tel2);
                        cs.execute();
                    }
                }
                JOptionPane.showMessageDialog(panel, "Teléfonos actualizados correctamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error al actualizar teléfonos: " + ex.getMessage());
            }
        });

        btnGuardarMail.addActionListener(e -> {
            String mail = txtMail.getText().trim();
            if (!mail.isEmpty()) {
                try (Connection conn = dataBase.DatabaseConnection.getConnection();
                        CallableStatement cs = conn.prepareCall("{ call SP_MODIFICAR_MAIL_EMPRESA(?, ?, ?) }")) {
                    List<String[]> mails = dataBase.EmpresaDB.getMailsEmpresa(1);
                    int idMailemp = !mails.isEmpty() ? Integer.parseInt(mails.get(0)[0]) : 0;
                    cs.setInt(1, idMailemp);
                    cs.setInt(2, 1);
                    cs.setString(3, mail);
                    cs.execute();
                    JOptionPane.showMessageDialog(panel, "Mail actualizado correctamente.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error al actualizar mail: " + ex.getMessage());
                }
            }
        });

        return panel;
    }

    private JPanel crearPanelGestionBackup() {
        return new BackupRestorePanel();
    }

    private JPanel crearPanelGestionReportes() {
        return new ReportesPanel();
    }

    // Si tienes un método para modificar cotización, asegúrate de incluir el
    // descuento:
    // Ejemplo:
    private void modificarCabeceraCotizacion(String ncot) {
        try {
            BigDecimal desct = new BigDecimal(txtDescuento.getText());
            String cond = txtCond.getText();
            String tent = txtTentativa.getText();
            Date vofer = txtValidez.getText().isEmpty() ? null : Date.valueOf(txtValidez.getText());
            CotizacionDB.modificarCabeceraCotizacion(ncot, desct, cond, tent, vofer);
            // Si tienes otros campos, agrégalos aquí
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al modificar cotización: " + ex.getMessage());
        }
    }

    private void abrirPanelCargarPlantillaCotizacion() {
        JFrame frame = new JFrame("Seleccionar Cotización Anterior");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(this);
        frame.add(new CargarPlantillaCotizacionPanel((ncot) -> {
            cargarDatosDesdeCotizacion(ncot);
            frame.dispose();
        }));
        frame.setVisible(true);
    }

    // Método para cargar datos desde una cotización seleccionada
    private void cargarDatosDesdeCotizacion(String ncot) {
        try (Connection conn = dataBase.DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT desct, vofer FROM Cotizacion WHERE ncot = ?")) {
            ps.setString(1, ncot);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double desct = rs.getDouble("desct");
                java.sql.Date vofer = rs.getDate("vofer");
                txtDescuento.setText(String.format("%.2f", desct));
                // Solo carga validez si no es anterior a hoy
                if (vofer != null && !vofer.before(java.sql.Date.valueOf(java.time.LocalDate.now()))) {
                    txtValidez.setText(vofer.toString());
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos de cotización: " + ex.getMessage());
        }
        // Cargar detalles
        try {
            modeloDetalle.setRowCount(0);
            for (String[] det : dataBase.DetalleCotizacionDB.listarLineasCotizacion(ncot)) {
                modeloDetalle.addRow(new Object[] {
                        det[1], // id_serv
                        det[2], // descp
                        det[4], // cant
                        det[3], // punit
                        det[5] // subtotal
                });
            }
            actualizarResumen();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar detalles: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainCotizacionFrame().setVisible(true);
        });
    }
}

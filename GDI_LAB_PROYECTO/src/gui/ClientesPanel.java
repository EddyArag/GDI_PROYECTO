package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import dataBase.ClienteDB;

/**
 * Panel para la gestión de clientes: agregar, modificar, eliminar, reactivar y
 * actualizar.
 * Permite buscar clientes por nombre o RUC, ver dirección y teléfono, y
 * gestionar sus datos.
 * Utiliza ClienteDB para operaciones con la base de datos.
 */
public class ClientesPanel extends JPanel {
    private JTable tablaClientes;
    private DefaultTableModel modeloClientes;
    private JButton btnAgregar, btnModificar, btnEliminar, btnReactivar, btnActualizar, btnBuscar;
    private JTextField txtBuscar;
    private Color colorFondoPanel = new Color(220, 235, 250);
    private Color colorBorde = new Color(100, 160, 220);
    private Font fuenteCampos = new Font("Segoe UI", Font.PLAIN, 16);

    /**
     * Constructor: inicializa el panel, la tabla y los botones.
     */
    public ClientesPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1100, 650)); // Más ancho y alto
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
        btnBuscar = new JButton("Buscar");

        btnAgregar.setBackground(colorBorde);
        btnModificar.setBackground(colorBorde);
        btnEliminar.setBackground(colorBorde);
        btnReactivar.setBackground(colorBorde);
        btnActualizar.setBackground(colorBorde);
        btnBuscar.setBackground(colorBorde);

        btnAgregar.setForeground(Color.WHITE);
        btnModificar.setForeground(Color.WHITE);
        btnEliminar.setForeground(Color.WHITE);
        btnReactivar.setForeground(Color.WHITE);
        btnActualizar.setForeground(Color.WHITE);
        btnBuscar.setForeground(Color.WHITE);

        btnAgregar.setFont(fuenteCampos);
        btnModificar.setFont(fuenteCampos);
        btnEliminar.setFont(fuenteCampos);
        btnReactivar.setFont(fuenteCampos);
        btnActualizar.setFont(fuenteCampos);
        btnBuscar.setFont(fuenteCampos);

        txtBuscar = new JTextField(18);
        txtBuscar.setFont(fuenteCampos);

        JPanel panelBusqueda = new JPanel();
        panelBusqueda.setBackground(colorFondoPanel);
        panelBusqueda.add(new JLabel("Buscar nombre o RUC:"));
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(colorFondoPanel);
        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnReactivar);
        panelBotones.add(btnActualizar);

        add(panelBusqueda, BorderLayout.NORTH);
        add(new JScrollPane(tablaClientes), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarCliente());
        btnModificar.addActionListener(e -> modificarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnActualizar.addActionListener(e -> cargarClientes());
        btnReactivar.addActionListener(e -> mostrarVentanaReactivar());
        btnBuscar.addActionListener(e -> buscarClientes());
    }

    /**
     * Carga los clientes activos en la tabla, mostrando también dirección y
     * teléfono.
     */
    private void cargarClientes() {
        modeloClientes.setRowCount(0);
        try {
            for (String[] cli : ClienteDB.listarClientes()) { // Solo activos
                String idCli = cli[0];
                String direccion = "";
                String telefono = "";
                try {
                    // Obtener la primera dirección (si existe)
                    java.util.List<String[]> dirs = ClienteDB.getDireccionesCliente(Integer.parseInt(idCli));
                    if (!dirs.isEmpty()) {
                        direccion = dirs.get(0)[1];
                    }
                } catch (Exception ignore) {
                }
                try {
                    // Obtener el primer teléfono (si existe)
                    java.util.List<String[]> tels = ClienteDB.getTelefonosCliente(Integer.parseInt(idCli));
                    if (!tels.isEmpty()) {
                        telefono = tels.get(0)[1];
                    }
                } catch (Exception ignore) {
                }
                // Agrega columnas de dirección y teléfono a la tabla si no existen
                if (modeloClientes.getColumnCount() == 4) {
                    modeloClientes.addColumn("Dirección");
                    modeloClientes.addColumn("Teléfono");
                }
                modeloClientes.addRow(new Object[] {
                        cli[0], cli[1], cli[2], cli[3], direccion, telefono
                });
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
     * Muestra un formulario para agregar un nuevo cliente (persona o empresa).
     */
    private void agregarCliente() {
        String[] opciones = { "Persona", "Empresa" };
        int tipo = JOptionPane.showOptionDialog(this, "¿Qué tipo de cliente desea agregar?", "Tipo de Cliente",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (tipo == JOptionPane.CLOSED_OPTION)
            return;

        JTextField nombre = new PlaceholderTextField("Ej: Juan / Empresa SAC");
        JTextField apeP = new PlaceholderTextField("Ej: Pérez");
        JTextField apeM = new PlaceholderTextField("Ej: Gómez");
        JTextField ruc = new PlaceholderTextField("Ej: 20123456789");
        JTextField direccion = new PlaceholderTextField("Ej: Av. Principal 123");
        JTextField telefono = new PlaceholderTextField("Ej: 987654321");
        JTextField obs = new PlaceholderTextField("Observaciones...");

        Object[] camposPersona = {
                "Nombre:", nombre,
                "Apellido Paterno:", apeP,
                "Apellido Materno:", apeM,
                "RUC/DNI:", ruc,
                "Dirección:", direccion,
                "Teléfono:", telefono,
                "Observaciones:", obs
        };
        Object[] camposEmpresa = {
                "Nombre:", nombre,
                "RUC:", ruc,
                "Dirección:", direccion,
                "Teléfono:", telefono,
                "Observaciones:", obs
        };

        Object[] campos = (tipo == 0) ? camposPersona : camposEmpresa;
        int res = JOptionPane.showConfirmDialog(this, campos, "Nuevo " + opciones[tipo], JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            String nombreVal = nombre.getText().trim();
            String apePVal = tipo == 0 ? apeP.getText().trim() : null;
            String apeMVal = tipo == 0 ? apeM.getText().trim() : null;
            String rucVal = ruc.getText().trim();
            String dirVal = direccion.getText().trim();
            String telVal = telefono.getText().trim();
            String obsVal = obs.getText().trim();

            // Validación
            if (nombreVal.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo Nombre es obligatorio.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (tipo == 0) { // Persona
                if (apePVal.isEmpty() && apeMVal.isEmpty() && rucVal.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Debe ingresar al menos Apellido Paterno, Apellido Materno o RUC/DNI.", "Validación",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else { // Empresa
                if (rucVal.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El campo RUC es obligatorio para empresas.", "Validación",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!rucVal.matches("\\d{11}")) {
                    JOptionPane.showMessageDialog(this,
                            "El RUC de la empresa debe tener exactamente 11 dígitos numéricos.", "Validación",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                apePVal = null;
                apeMVal = null;
            }
            // Insertar cliente
            try {
                Integer idCli = ClienteDB.insertarCliente(
                        nombreVal,
                        apePVal,
                        apeMVal,
                        rucVal.isEmpty() ? null : rucVal,
                        obsVal);
                // Insertar dirección y teléfono si se ingresaron
                if (idCli != null && idCli > 0) {
                    if (!dirVal.isEmpty()) {
                        try (java.sql.Connection conn = dataBase.DatabaseConnection.getConnection();
                                java.sql.CallableStatement cs = conn.prepareCall("CALL SP_AGREGAR_DIR_CLIENTE(?, ?)")) {
                            cs.setInt(1, idCli);
                            cs.setString(2, dirVal);
                            cs.execute();
                        }
                    }
                    if (!telVal.isEmpty()) {
                        try (java.sql.Connection conn = dataBase.DatabaseConnection.getConnection();
                                java.sql.CallableStatement cs = conn.prepareCall("CALL SP_AGREGAR_TEL_CLIENTE(?, ?)")) {
                            cs.setInt(1, idCli);
                            cs.setString(2, telVal);
                            cs.execute();
                        }
                    }
                }
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
        String nombreVal = modeloClientes.getValueAt(fila, 1).toString();
        String rucVal = modeloClientes.getValueAt(fila, 2) != null ? modeloClientes.getValueAt(fila, 2).toString() : "";
        String obsVal = modeloClientes.getValueAt(fila, 3) != null ? modeloClientes.getValueAt(fila, 3).toString() : "";

        // Determinar si es empresa (tiene RUC y no tiene apellidos)
        boolean esEmpresa = false;
        String apePVal = "";
        String apeMVal = "";
        try {
            int idCli = Integer.parseInt(id);
            try (java.sql.Connection conn = dataBase.DatabaseConnection.getConnection();
                    java.sql.PreparedStatement ps = conn
                            .prepareStatement("SELECT ape_p, ape_m FROM Cliente WHERE id_cli = ?")) {
                ps.setInt(1, idCli);
                java.sql.ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    apePVal = rs.getString("ape_p") != null ? rs.getString("ape_p") : "";
                    apeMVal = rs.getString("ape_m") != null ? rs.getString("ape_m") : "";
                    esEmpresa = (apePVal.isEmpty() && apeMVal.isEmpty() && !rucVal.isEmpty());
                }
            }
        } catch (Exception ex) {
            // Si falla, asume persona
        }

        JTextField nombre = new JTextField(nombreVal);
        JTextField ruc = new JTextField(rucVal);
        JTextField obs = new JTextField(obsVal);
        JTextField direccion = new JTextField();
        JTextField telefono = new JTextField();
        JTextField apeP = new JTextField(apePVal);
        JTextField apeM = new JTextField(apeMVal);

        Object[] campos;
        if (esEmpresa) {
            campos = new Object[] {
                    "Nombre:", nombre,
                    "RUC:", ruc,
                    "Dirección:", direccion,
                    "Teléfono:", telefono,
                    "Observaciones:", obs
            };
        } else {
            campos = new Object[] {
                    "Nombre:", nombre,
                    "Apellido Paterno:", apeP,
                    "Apellido Materno:", apeM,
                    "RUC/DNI:", ruc,
                    "Dirección:", direccion,
                    "Teléfono:", telefono,
                    "Observaciones:", obs
            };
        }

        int res = JOptionPane.showConfirmDialog(this, campos, "Modificar Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            String nombreTxt = nombre.getText().trim();
            String apePTxt = esEmpresa ? null : apeP.getText().trim();
            String apeMTxt = esEmpresa ? null : apeM.getText().trim();
            String rucTxt = ruc.getText().trim();
            String dirTxt = direccion.getText().trim();
            String telTxt = telefono.getText().trim();
            String obsTxt = obs.getText().trim();

            // Validación
            if (nombreTxt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo Nombre es obligatorio.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!esEmpresa) {
                if ((apePTxt == null || apePTxt.isEmpty()) && (apeMTxt == null || apeMTxt.isEmpty())
                        && rucTxt.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Debe ingresar al menos Apellido Paterno, Apellido Materno o RUC/DNI.", "Validación",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else {
                if (rucTxt.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El campo RUC es obligatorio para empresas.", "Validación",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            try {
                ClienteDB.modificarCliente(
                        Integer.parseInt(id),
                        nombreTxt,
                        apePTxt,
                        apeMTxt,
                        rucTxt.isEmpty() ? null : rucTxt,
                        obsTxt);
                // Actualizar dirección y teléfono si se ingresaron
                if (!dirTxt.isEmpty()) {
                    try (java.sql.Connection conn = dataBase.DatabaseConnection.getConnection();
                            java.sql.CallableStatement cs = conn.prepareCall("CALL SP_AGREGAR_DIR_CLIENTE(?, ?)")) {
                        cs.setInt(1, Integer.parseInt(id));
                        cs.setString(2, dirTxt);
                        cs.execute();
                    }
                }
                if (!telTxt.isEmpty()) {
                    try (java.sql.Connection conn = dataBase.DatabaseConnection.getConnection();
                            java.sql.CallableStatement cs = conn.prepareCall("CALL SP_AGREGAR_TEL_CLIENTE(?, ?)")) {
                        cs.setInt(1, Integer.parseInt(id));
                        cs.setString(2, telTxt);
                        cs.execute();
                    }
                }
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
     * Busca clientes por nombre o RUC.
     */
    private void buscarClientes() {
        String filtro = txtBuscar.getText().trim();
        modeloClientes.setRowCount(0);
        if (filtro.isEmpty()) {
            cargarClientes();
            return;
        }
        try {
            for (String[] cli : ClienteDB.buscarClientes(filtro)) {
                modeloClientes.addRow(cli);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar clientes: " + ex.getMessage());
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

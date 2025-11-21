package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import dataBase.ClienteDB;

/**
 * Panel para la gestión de clientes: agregar, modificar, eliminar, reactivar y
 * actualizar.
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
            // Validación en Java antes de enviar a la BD
            if (nombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo Nombre es obligatorio.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Si RUC está vacío, Apellido Paterno debe estar lleno (regla de negocio)
            if (ruc.getText().trim().isEmpty() && apeP.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Para personas naturales (sin RUC), el Apellido Paterno es obligatorio.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Validación de RUC/DNI: si no está vacío, debe tener 8 o 11 dígitos y solo
            // números
            String rucTxt = ruc.getText().trim();
            if (!rucTxt.isEmpty()) {
                if (!rucTxt.matches("\\d+")) {
                    JOptionPane.showMessageDialog(this, "El campo RUC/DNI debe contener solo números.", "Validación",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (rucTxt.length() != 8 && rucTxt.length() != 11) {
                    JOptionPane.showMessageDialog(this, "El número de RUC/DNI debe ser de 8 o 11 dígitos.",
                            "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            try {
                ClienteDB.insertarCliente(nombre.getText(), apeP.getText(), apeM.getText(), ruc.getText(),
                        obs.getText());
                cargarClientes();
            } catch (SQLException ex) {
                String msg = ex.getMessage();
                // Solo muestra la primera línea del mensaje (sin el "Where:" ni detalles
                // técnicos)
                if (msg != null && msg.contains("ERROR DE VALIDACIÓN")) {
                    String mensaje = msg.split("\n")[0];
                    JOptionPane.showMessageDialog(this, mensaje, "Error de Validación", JOptionPane.ERROR_MESSAGE);
                } else if (msg != null && msg.contains("ERROR DE UNICIDAD")) {
                    String mensaje = msg.split("\n")[0];
                    JOptionPane.showMessageDialog(this, mensaje, "Error de Unicidad", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar cliente: " + msg);
                }
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
        String id = modeloClientes.getValueAt(fila, 0) != null ? modeloClientes.getValueAt(fila, 0).toString() : "";
        // Obtén los datos completos del cliente desde la BD para los campos
        // individuales
        String nombreVal = "";
        String apePVal = "";
        String apeMVal = "";
        String rucVal = "";
        String obsVal = "";
        try {
            int idCli = Integer.parseInt(id);
            try (java.sql.Connection conn = dataBase.DatabaseConnection.getConnection();
                    java.sql.PreparedStatement ps = conn
                            .prepareStatement("SELECT p_nomb, ape_p, ape_m, ruc, obs FROM Cliente WHERE id_cli = ?")) {
                ps.setInt(1, idCli);
                java.sql.ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    nombreVal = rs.getString("p_nomb") != null ? rs.getString("p_nomb") : "";
                    apePVal = rs.getString("ape_p") != null ? rs.getString("ape_p") : "";
                    apeMVal = rs.getString("ape_m") != null ? rs.getString("ape_m") : "";
                    rucVal = rs.getString("ruc") != null ? rs.getString("ruc") : "";
                    obsVal = rs.getString("obs") != null ? rs.getString("obs") : "";
                }
            }
        } catch (Exception ex) {
            // Si falla, deja los valores vacíos
        }

        JTextField nombre = new JTextField(nombreVal);
        JTextField apeP = new JTextField(apePVal);
        JTextField apeM = new JTextField(apeMVal);
        JTextField ruc = new JTextField(rucVal);
        JTextField obs = new JTextField(obsVal);
        Object[] campos = {
                "Nombre:", nombre,
                "Apellido Paterno:", apeP,
                "Apellido Materno:", apeM,
                "RUC:", ruc,
                "Observaciones:", obs
        };
        int res = JOptionPane.showConfirmDialog(this, campos, "Modificar Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            // Validación en Java antes de enviar a la BD
            if (nombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo Nombre es obligatorio.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (ruc.getText().trim().isEmpty() && apeP.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Para personas naturales (sin RUC), el Apellido Paterno es obligatorio.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            String rucTxt = ruc.getText().trim();
            String rucFinal = null;
            if (!rucTxt.isEmpty()) {
                if (!rucTxt.matches("\\d+")) {
                    JOptionPane.showMessageDialog(this, "El campo RUC/DNI debe contener solo números.", "Validación",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (rucTxt.length() != 8 && rucTxt.length() != 11) {
                    JOptionPane.showMessageDialog(this, "El número de RUC/DNI debe ser de 8 o 11 dígitos.",
                            "Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                rucFinal = rucTxt;
            }
            try {
                ClienteDB.modificarCliente(Integer.parseInt(id), nombre.getText(), apeP.getText(), apeM.getText(),
                        rucFinal, obs.getText());
                cargarClientes();
            } catch (SQLException ex) {
                String msg = ex.getMessage();
                if (msg != null && msg.contains("ERROR DE VALIDACIÓN")) {
                    String mensaje = msg.split("\n")[0];
                    JOptionPane.showMessageDialog(this, mensaje, "Error de Validación", JOptionPane.ERROR_MESSAGE);
                } else if (msg != null && msg.contains("ERROR DE UNICIDAD")) {
                    String mensaje = msg.split("\n")[0];
                    JOptionPane.showMessageDialog(this, mensaje, "Error de Unicidad", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error al modificar cliente: " + msg);
                }
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

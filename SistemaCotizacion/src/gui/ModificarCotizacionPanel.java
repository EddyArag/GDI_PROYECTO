package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.math.BigDecimal;
import dataBase.*;

/**
 * Panel que permite modificar una cotización existente.
 * - Carga la cabecera y el detalle de la cotización indicada por NCOT.
 * - Permite agregar/quitar productos y guardar los cambios.
 *
 * Nota: Se asume la existencia de clases utilitarias (DatabaseConnection, CotizacionDB, DetalleCotizacionDB, etc.).
 */
public class ModificarCotizacionPanel extends JPanel {
    private JComboBox<String> comboClientes;
    private JTextField txtFecha, txtCond, txtGarantia, txtTentativa, txtValidez;
    private JTable tablaDetalle;
    private DefaultTableModel modeloDetalle;
    private JButton btnAgregarProducto, btnQuitarProducto, btnGuardar;

    private String ncot;

    public ModificarCotizacionPanel(String ncot) {
        this.ncot = ncot;
        setLayout(new BorderLayout());

        // Panel cabecera
        JPanel panelCabecera = new JPanel(new GridLayout(2, 5, 5, 5));
        comboClientes = new JComboBox<>();
        txtFecha = new JTextField();
        txtCond = new JTextField();
        txtGarantia = new JTextField();
        txtTentativa = new JTextField();
        txtValidez = new JTextField();

        panelCabecera.add(new JLabel("Cliente:"));
        panelCabecera.add(comboClientes);
        panelCabecera.add(new JLabel("Fecha Emisión:"));
        panelCabecera.add(txtFecha);
        panelCabecera.add(new JLabel("Condiciones:"));
        panelCabecera.add(txtCond);
        panelCabecera.add(new JLabel("Garantía:"));
        panelCabecera.add(txtGarantia);
        panelCabecera.add(new JLabel("Entrega Tentativa:"));
        panelCabecera.add(txtTentativa);
        panelCabecera.add(new JLabel("Validez Oferta:"));
        panelCabecera.add(txtValidez);

        // Panel detalle
        modeloDetalle = new DefaultTableModel(
                new Object[] { "ID Producto", "Producto", "Cantidad", "Precio Unitario", "Subtotal" }, 0);
        tablaDetalle = new JTable(modeloDetalle);
        tablaDetalle.getColumnModel().getColumn(0).setMinWidth(0);
        tablaDetalle.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaDetalle.getColumnModel().getColumn(0).setWidth(0);

        btnAgregarProducto = new JButton("Agregar Producto");
        btnQuitarProducto = new JButton("Quitar Producto");
        btnGuardar = new JButton("Guardar Cambios");

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnAgregarProducto);
        panelBotones.add(btnQuitarProducto);
        panelBotones.add(btnGuardar);

        add(panelCabecera, BorderLayout.NORTH);
        add(new JScrollPane(tablaDetalle), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        cargarClientes();
        cargarDatosCotizacion();
        cargarDetalleCotizacion();

        btnAgregarProducto.addActionListener(e -> abrirProductosFrame());
        btnQuitarProducto.addActionListener(e -> quitarProductoSeleccionado());
        btnGuardar.addActionListener(e -> guardarCambios());
    }

    /**
     * Carga la lista de clientes en el JComboBox.
     * Utiliza una función de la base de datos FN_LISTAR_CLIENTES().
     */
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

    /**
     * Carga los datos de cabecera de la cotización solicitada (fecha, condiciones, garantía, etc.).
     * Selecciona el cliente correspondiente en el combo si existe.
     */
    private void cargarDatosCotizacion() {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM Cotizacion WHERE NCOT = ?")) {
            ps.setString(1, ncot);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtFecha.setText(rs.getString("FEMI"));
                txtCond.setText(rs.getString("COND"));
                txtGarantia.setText(rs.getString("GARA"));
                txtTentativa.setText(rs.getString("TENT"));
                txtValidez.setText(rs.getString("VOFER"));
                int idCli = rs.getInt("ID_CLI");
                for (int i = 0; i < comboClientes.getItemCount(); i++) {
                    if (comboClientes.getItemAt(i).startsWith(idCli + " -")) {
                        comboClientes.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos de cotización: " + ex.getMessage());
        }
    }

    /**
     * Carga el detalle (líneas) de la cotización desde DetalleCotizacionDB y lo coloca en la tabla.
     */
    private void cargarDetalleCotizacion() {
        modeloDetalle.setRowCount(0);
        try {
            for (String[] det : DetalleCotizacionDB.listarLineasCotizacion(ncot)) {
                // det[1]=id_serv, det[2]=descp, det[4]=cant, det[3]=punit, det[5]=subtotal
                modeloDetalle.addRow(new Object[] {
                        det[1], // id_serv
                        det[2], // descp
                        det[4], // cant
                        det[3], // punit
                        det[5] // subtotal
                });
            }
        } catch (SQLException ex) {
            // Mensaje de error claro para el usuario.
            JOptionPane.showMessageDialog(this, "Error al cargar detalle: " + ex.getMessage());
        }
    }

    /**
     * Abre la ventana de selección de productos. El callback añade la línea al modelo de la tabla.
     */
    private void abrirProductosFrame() {
        ProductosFrame frame = new ProductosFrame((idServ, nombre, precio, cantidad) -> {
            double subtotal = precio * cantidad;
            modeloDetalle.addRow(new Object[] { idServ, nombre, cantidad, precio, subtotal });
        });
        frame.setVisible(true);
    }

    /**
     * Quita la fila seleccionada en la tabla de detalle.
     */
    private void quitarProductoSeleccionado() {
        int fila = tablaDetalle.getSelectedRow();
        if (fila != -1) {
            modeloDetalle.removeRow(fila);
        }
    }

    /**
     * Valida los campos y persiste los cambios:
     * - Actualiza cabecera usando CotizacionDB.modificarCabeceraCotizacion
     * - Borra los detalles existentes y vuelve a insertar las líneas desde la tabla
     *
     * Observaciones:
     * - Se usa BigDecimal para descuentos (desct) para evitar problemas de precisión.
     * - Las cadenas NCOT e ID_SERV se formatean con String.format para mantener longitud esperada
     *   por los procedimientos almacenados (ej.: "%-10s", "%-4s").
     */
    private void guardarCambios() {
        String seleccionado = (String) comboClientes.getSelectedItem();
        if (seleccionado == null || !seleccionado.contains(" - ")) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente.");
            return;
        }
        int idCli = Integer.parseInt(seleccionado.split(" - ")[0]);
        String cond = txtCond.getText().isEmpty() ? null : txtCond.getText();
        String tent = txtTentativa.getText().isEmpty() ? null : txtTentativa.getText();
        String voferStr = txtValidez.getText().trim();
        Date vofer = null;
        if (!voferStr.isEmpty()) {
            try {
                vofer = Date.valueOf(voferStr);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Formato de fecha incorrecto. Use yyyy-MM-dd.", "Error de Fecha",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        BigDecimal desct = BigDecimal.valueOf(0.0); // Usa BigDecimal en vez de double

        try {
            CotizacionDB.modificarCabeceraCotizacion(ncot, desct, cond, tent, vofer);
            // Elimina todos los detalles y vuelve a agregarlos
            try (Connection conn = dataBase.DatabaseConnection.getConnection();
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM Cotizacion_Detalle WHERE NCOT = ?")) {
                ps.setString(1, ncot);
                ps.executeUpdate();
            }
            for (int i = 0; i < modeloDetalle.getRowCount(); i++) {
                String idServ = modeloDetalle.getValueAt(i, 0).toString();
                int cantidad = Integer.parseInt(modeloDetalle.getValueAt(i, 2).toString());
                try (Connection conn = dataBase.DatabaseConnection.getConnection();
                        CallableStatement cs = conn.prepareCall("CALL SP_AGREGAR_DETALLE(?, ?, ?)")) {
                    // Formateo para adaptarse a la longitud esperada por el SP.
                    cs.setString(1, String.format("%-10s", ncot));
                    cs.setString(2, String.format("%-4s", idServ));
                    cs.setInt(3, cantidad);
                    cs.execute();
                }
            }
            JOptionPane.showMessageDialog(this, "Cotización modificada correctamente.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al modificar cotización: " + ex.getMessage());
        }
    }
}

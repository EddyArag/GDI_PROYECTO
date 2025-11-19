package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Panel para exportar e importar backups de la base de datos PostgreSQL usando pg_dump y psql.
 */
public class BackupRestorePanel extends JPanel {
    private Color colorFondoPanel = new Color(220, 235, 250);
    private Color colorBorde = new Color(100, 160, 220);
    private Font fuenteCampos = new Font("Segoe UI", Font.PLAIN, 16);

    public BackupRestorePanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(colorBorde, 2, true), "Backup y Restore (PostgreSQL)"));
        setBackground(colorFondoPanel);

        JButton btnExportBackup = new JButton("Exportar Backup");
        JButton btnImportBackup = new JButton("Importar Backup");

        btnExportBackup.setBackground(colorBorde);
        btnImportBackup.setBackground(colorBorde);
        btnExportBackup.setForeground(Color.WHITE);
        btnImportBackup.setForeground(Color.WHITE);
        btnExportBackup.setFont(fuenteCampos);
        btnImportBackup.setFont(fuenteCampos);

        add(Box.createVerticalStrut(20));
        add(btnExportBackup);
        add(Box.createVerticalStrut(10));
        add(btnImportBackup);

        btnExportBackup.addActionListener(this::exportarBackup);
        btnImportBackup.addActionListener(this::importarBackup);
    }

    /**
     * Exporta la base de datos a un archivo usando pg_dump.
     */
    private void exportarBackup(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecciona ubicación para guardar el backup");
        chooser.setSelectedFile(new File("backup_gdi.sql"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                // Ejecuta pg_dump (debe estar en el PATH del sistema)
                String cmd = String.format("pg_dump -U postgres -h localhost -d sistema_cotizacion_gdi -F p -f \"%s\"", file.getAbsolutePath());
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", cmd);
                pb.environment().put("PGPASSWORD", "eddy"); // Cambia por tu contraseña
                Process process = pb.start();
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    JOptionPane.showMessageDialog(this, "Backup exportado correctamente.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al exportar backup. Verifica pg_dump y permisos.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    /**
     * Importa un archivo de backup a la base de datos usando psql.
     */
    private void importarBackup(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecciona el archivo de backup a restaurar");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                // Ejecuta psql para restaurar (debe estar en el PATH del sistema)
                String cmd = String.format("psql -U postgres -h localhost -d sistema_cotizacion_gdi -f \"%s\"", file.getAbsolutePath());
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", cmd);
                pb.environment().put("PGPASSWORD", "eddy"); // Cambia por tu contraseña
                Process process = pb.start();
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    JOptionPane.showMessageDialog(this, "Backup importado correctamente.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al importar backup. Verifica psql y permisos.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}

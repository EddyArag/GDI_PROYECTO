/**
 * Clase principal del sistema de gestión de cotizaciones GDI.
 * Inicia la interfaz gráfica principal del sistema.
 * 
 * Requisitos:
 * - Tener configurada la conexión a la base de datos PostgreSQL.
 * - Ejecutar previamente los scripts SQL para crear tablas, funciones y
 * procedimientos.
 * 
 * Uso:
 * Ejecuta este archivo para iniciar la aplicación.
 */
public class App {
    /**
     * Método principal. Inicia la ventana principal del sistema.
     * 
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new gui.MainCotizacionFrame().setVisible(true);
        });
    }
}

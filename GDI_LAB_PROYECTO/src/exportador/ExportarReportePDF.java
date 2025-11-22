package exportador;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.FileOutputStream;
import java.io.File;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

/**
 * Clase utilitaria para exportar cualquier JTable a PDF usando OpenPDF.
 * Permite seleccionar la ubicación y nombre del archivo PDF.
 */
public class ExportarReportePDF {
    /**
     * Exporta el contenido de una JTable a un archivo PDF con formato tabular.
     * 
     * @param tabla         JTable a exportar.
     * @param nombreReporte Nombre sugerido para el archivo PDF.
     * @throws Exception Si ocurre un error durante la exportación.
     */
    public static void exportarTabla(JTable tabla, String nombreReporte) throws Exception {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(nombreReporte + ".pdf"));
        if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION)
            return;
        File file = chooser.getSelectedFile();

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        // Usa el color de OpenPDF (usa valores RGB directamente)
        Font fontTitulo = new Font(Font.HELVETICA, 18, Font.BOLD, java.awt.Color.BLACK);
        Font fontCabecera = new Font(Font.HELVETICA, 12, Font.BOLD, java.awt.Color.BLACK);
        Font fontCelda = new Font(Font.HELVETICA, 11, Font.NORMAL, java.awt.Color.BLACK);

        Paragraph titulo = new Paragraph("Reporte: " + nombreReporte, fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph(" "));

        PdfPTable pdfTable = new PdfPTable(tabla.getColumnCount());
        pdfTable.setWidthPercentage(100);

        // Encabezados
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            PdfPCell cell = new PdfPCell(new Phrase(tabla.getColumnName(i), fontCabecera));
            cell.setBackgroundColor(new java.awt.Color(200, 220, 245)); // Usa java.awt.Color, OpenPDF lo acepta
            pdfTable.addCell(cell);
        }
        // Filas
        TableModel model = tabla.getModel();
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                Object val = model.getValueAt(row, col);
                PdfPCell cell = new PdfPCell(new Phrase(val != null ? val.toString() : "", fontCelda));
                pdfTable.addCell(cell);
            }
        }
        document.add(pdfTable);
        document.close();
        JOptionPane.showMessageDialog(null, "Reporte exportado a PDF correctamente.");
    }
}

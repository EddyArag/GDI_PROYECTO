package gui;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator; // Agrega esta importación
import java.awt.Color; // Agrega esta importación
import java.io.FileOutputStream;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import dataBase.DatabaseConnection;
import dataBase.EmpresaDB;
import dataBase.ClienteDB;
import dataBase.DetalleCotizacionDB;
import javax.swing.JFileChooser;

public class ExportarCotizacionPDF {
    public static void exportar(String ncot) {
        try {
            // Selección de ubicación y nombre de archivo
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Cotización como PDF");
            fileChooser.setSelectedFile(new java.io.File("Cotizacion_" + ncot.trim() + ".pdf"));
            int userSelection = fileChooser.showSaveDialog(null);
            if (userSelection != JFileChooser.APPROVE_OPTION) {
                return; // Cancelado por el usuario
            }
            String filename = fileChooser.getSelectedFile().getAbsolutePath();

            Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36); // Horizontal, márgenes
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();

            Font fontTitulo = new Font(Font.HELVETICA, 18, Font.BOLD, Color.BLACK);
            Font fontSubtitulo = new Font(Font.HELVETICA, 14, Font.BOLD, Color.BLACK);
            Font fontNormal = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.BLACK);
            Font fontNegrita = new Font(Font.HELVETICA, 12, Font.BOLD, Color.BLACK);
            Font fontPequena = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);

            // 1. CABECERA PRINCIPAL
            PdfPTable cabecera = new PdfPTable(2);
            cabecera.setWidthPercentage(100);
            cabecera.setWidths(new float[]{2.5f, 2.5f});

            // 1.1 Logo e info empresa
            PdfPCell cellLogoInfo = new PdfPCell();
            cellLogoInfo.setBorder(Rectangle.NO_BORDER);
            cellLogoInfo.setPaddingBottom(8);

            // Logo
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT logo FROM Empresa LIMIT 1")) {
                if (rs.next()) {
                    byte[] logoBytes = rs.getBytes("logo");
                    if (logoBytes != null && logoBytes.length > 0) {
                        Image logo = Image.getInstance(logoBytes);
                        logo.scaleAbsolute(90, 45);
                        cellLogoInfo.addElement(logo);
                    }
                }
            } catch (Exception ex) {
                // Si no hay logo, ignora
            }

            // Info empresa
            List<String[]> direcciones = EmpresaDB.getDireccionesEmpresa(1);
            List<String[]> telefonos = EmpresaDB.getTelefonosEmpresa(1);
            List<String[]> mails = EmpresaDB.getMailsEmpresa(1);
            String direccionEmp = direcciones.size() > 0 ? direcciones.get(0)[1] : "";
            String telefonoEmp = telefonos.size() > 0 ? telefonos.get(0)[1] : "";
            String mailEmp = mails.size() > 0 ? mails.get(0)[1] : "";
            String rucEmp = "20456116982"; // Si tienes en tabla, obtén de ahí

            Paragraph infoEmp = new Paragraph();
            infoEmp.setFont(fontPequena);
            infoEmp.add("INNOVASOFT COMPUTERS\n");
            infoEmp.add("www.innovasoft.com.pe\n");
            infoEmp.add(direccionEmp + "\n");
            infoEmp.add("Tel: " + telefonoEmp + "\n");
            infoEmp.add("RUC: " + rucEmp + "\n");
            cellLogoInfo.addElement(infoEmp);

            cabecera.addCell(cellLogoInfo);

            // 1.3 Datos de la cotización
            PdfPCell cellDatosCot = new PdfPCell();
            cellDatosCot.setBorder(Rectangle.NO_BORDER);
            cellDatosCot.setHorizontalAlignment(Element.ALIGN_RIGHT);

            // Obtener datos de cabecera
            String fechaEmi = "";
            String garantia = "";
            String desct = "";
            String vofer = "";
            String cond = "";
            String tent = "";
            double igv = 0.18;
            double subtotal = 0;
            double total = 0;
            int idCli = 0;
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT femi, gara, desct, igv, cond, tent, vofer, id_cli FROM Cotizacion WHERE ncot = ?")) {
                ps.setString(1, ncot);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    fechaEmi = rs.getString("femi");
                    garantia = rs.getString("gara");
                    desct = rs.getString("desct");
                    igv = rs.getDouble("igv");
                    cond = rs.getString("cond");
                    tent = rs.getString("tent");
                    vofer = rs.getString("vofer");
                    idCli = rs.getInt("id_cli");
                }
            }
            // Calcula subtotal y total usando funciones
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT CALCULAR_SUBTOTAL_COTIZACION(?) AS subtotal, CALCULAR_TOTAL_COTIZACION(?) AS total")) {
                ps.setString(1, ncot);
                ps.setString(2, ncot);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    subtotal = rs.getDouble("subtotal");
                    total = rs.getDouble("total");
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", new Locale("es", "ES"));
            String fechaEmiLegible = "";
            try {
                fechaEmiLegible = sdf.format(java.sql.Date.valueOf(fechaEmi));
            } catch (Exception ex) {
                fechaEmiLegible = fechaEmi;
            }

            Paragraph datosCot = new Paragraph();
            datosCot.setAlignment(Element.ALIGN_RIGHT);
            datosCot.setFont(fontSubtitulo);
            datosCot.add("COTIZACION Nro: " + ncot + "\n");
            datosCot.add("Fecha de Emisión: " + fechaEmiLegible + "\n");
            cellDatosCot.addElement(datosCot);

            cabecera.addCell(cellDatosCot);
            document.add(cabecera);

            // Línea de separación
            document.add(new Paragraph(" "));
            document.add(new LineSeparator());

            // 2. DATOS DEL CLIENTE
            String nombreCliente = "";
            String rucCliente = "";
            String direccionCliente = "";
            String telefonoCliente = "";
            String obsCliente = "";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT p_nomb, ape_p, ape_m, ruc, obs FROM Cliente WHERE id_cli = ?")) {
                ps.setInt(1, idCli);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    nombreCliente = (rs.getString("p_nomb") + " " +
                                     (rs.getString("ape_p") != null ? rs.getString("ape_p") : "") + " " +
                                     (rs.getString("ape_m") != null ? rs.getString("ape_m") : "")).trim();
                    rucCliente = rs.getString("ruc") != null ? rs.getString("ruc") : "";
                    obsCliente = rs.getString("obs") != null ? rs.getString("obs") : "";
                }
            }
            List<String[]> dirCli = ClienteDB.getDireccionesCliente(idCli);
            List<String[]> telCli = ClienteDB.getTelefonosCliente(idCli);
            direccionCliente = dirCli.size() > 0 ? dirCli.get(0)[1] : "";
            telefonoCliente = telCli.size() > 0 ? telCli.get(0)[1] : "";

            PdfPTable datosCliente = new PdfPTable(2);
            datosCliente.setWidthPercentage(100);
            datosCliente.setWidths(new float[]{1.2f, 4f});
            datosCliente.addCell(celdaLabel("Señores :", fontNegrita));
            datosCliente.addCell(celdaDato(nombreCliente, fontNormal));
            datosCliente.addCell(celdaLabel("RUC :", fontNegrita));
            datosCliente.addCell(celdaDato(rucCliente, fontNormal));
            datosCliente.addCell(celdaLabel("Dirección :", fontNegrita));
            datosCliente.addCell(celdaDato(direccionCliente, fontNormal));
            datosCliente.addCell(celdaLabel("Teléfono :", fontNegrita));
            datosCliente.addCell(celdaDato(telefonoCliente, fontNormal));
            datosCliente.addCell(celdaLabel("Notas :", fontNegrita));
            datosCliente.addCell(celdaDato(obsCliente, fontNormal));
            document.add(datosCliente);

            document.add(new Paragraph(" "));
            document.add(new LineSeparator());

            // 3. ENCABEZADOS DE LA TABLA DE DETALLES
            PdfPTable tablaDetalle = new PdfPTable(5);
            tablaDetalle.setWidthPercentage(100);
            tablaDetalle.setWidths(new float[]{1.2f, 1f, 4f, 1.5f, 1.5f});
            tablaDetalle.addCell(celdaTabla("Cod.", fontNegrita));
            tablaDetalle.addCell(celdaTabla("Cant.", fontNegrita));
            tablaDetalle.addCell(celdaTabla("Descripción del servicio", fontNegrita));
            tablaDetalle.addCell(celdaTabla("Precio Unit.", fontNegrita));
            tablaDetalle.addCell(celdaTabla("Importe", fontNegrita));

            // 4. TABLA DE DETALLES DE PRODUCTOS/SERVICIOS
            List<String[]> lineas = DetalleCotizacionDB.listarLineasCotizacion(ncot);
            DecimalFormat df = new DecimalFormat("S/ #,##0.00");
            for (String[] linea : lineas) {
                tablaDetalle.addCell(celdaTabla(linea[1], fontNormal)); // Cod.
                tablaDetalle.addCell(celdaTabla(linea[4], fontNormal, Element.ALIGN_RIGHT)); // Cant.
                tablaDetalle.addCell(celdaTabla(linea[2], fontNormal)); // Descripción
                tablaDetalle.addCell(celdaTabla(df.format(Double.parseDouble(linea[3])), fontNormal, Element.ALIGN_RIGHT)); // Precio Unit.
                tablaDetalle.addCell(celdaTabla(df.format(Double.parseDouble(linea[5])), fontNormal, Element.ALIGN_RIGHT)); // Importe
            }
            document.add(tablaDetalle);

            document.add(new Paragraph(" "));

            // 5. SECCIÓN DE TOTALES EN LETRAS Y RESUMEN NUMÉRICO
            PdfPTable totales = new PdfPTable(2);
            totales.setWidthPercentage(100);
            totales.setWidths(new float[]{2.5f, 2.5f});

            // 5.1 Total en letras
            PdfPCell cellLetras = new PdfPCell();
            cellLetras.setBorder(Rectangle.NO_BORDER);
            cellLetras.setVerticalAlignment(Element.ALIGN_TOP);
            cellLetras.addElement(new Paragraph("TOTAL EN LETRAS:", fontNegrita));
            cellLetras.addElement(new Paragraph(numToWords(total) + " Soles", fontNegrita));
            totales.addCell(cellLetras);

            // 5.2 Resumen de costos
            PdfPCell cellResumen = new PdfPCell();
            cellResumen.setBorder(Rectangle.NO_BORDER);
            cellResumen.setVerticalAlignment(Element.ALIGN_TOP);

            PdfPTable resumen = new PdfPTable(2);
            resumen.setWidthPercentage(80);
            resumen.setHorizontalAlignment(Element.ALIGN_RIGHT);
            resumen.addCell(celdaLabel("SUBTOTAL :", fontNormal));
            resumen.addCell(celdaDato(df.format(subtotal), fontNormal, Element.ALIGN_RIGHT));
            if (Double.parseDouble(desct) > 0) {
                resumen.addCell(celdaLabel("DESCUENTO :", fontNormal));
                resumen.addCell(celdaDato(df.format(Double.parseDouble(desct)), fontNormal, Element.ALIGN_RIGHT));
            }
            resumen.addCell(celdaLabel("IGV " + (int)(igv*100) + "% :", fontNormal));
            double igvCalc = (subtotal - Double.parseDouble(desct)) * igv;
            resumen.addCell(celdaDato(df.format(igvCalc), fontNormal, Element.ALIGN_RIGHT));
            resumen.addCell(celdaLabel("TOTAL :", fontNegrita));
            resumen.addCell(celdaDato(df.format(total), fontNegrita, Element.ALIGN_RIGHT));
            cellResumen.addElement(resumen);
            totales.addCell(cellResumen);

            document.add(totales);

            document.add(new Paragraph(" "));

            // 6. SECCIÓN DE CONDICIONES Y VALIDEZ
            PdfPTable condiciones = new PdfPTable(2);
            condiciones.setWidthPercentage(80);
            condiciones.setWidths(new float[]{2f, 4f});
            condiciones.addCell(celdaLabel("Condiciones :", fontNegrita));
            condiciones.addCell(celdaDato(cond == null ? "" : cond, fontNormal));
            condiciones.addCell(celdaLabel("Forma de Pago :", fontNegrita));
            condiciones.addCell(celdaDato("", fontNormal)); // Campo fijo o vacío
            condiciones.addCell(celdaLabel("Tiempo de Entrega :", fontNegrita));
            condiciones.addCell(celdaDato(tent == null ? "" : tent, fontNormal));
            condiciones.addCell(celdaLabel("Validez de la Oferta :", fontNegrita));
            String voferLegible = "";
            try {
                voferLegible = vofer != null && !vofer.isEmpty() ? sdf.format(java.sql.Date.valueOf(vofer)) : "";
            } catch (Exception ex) {
                voferLegible = vofer != null ? vofer : "";
            }
            condiciones.addCell(celdaDato(voferLegible, fontNormal));
            condiciones.addCell(celdaLabel("Garantía :", fontNegrita));
            condiciones.addCell(celdaDato(garantia == null ? "" : garantia, fontNormal));
            document.add(condiciones);

            document.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Utilidades para celdas
    private static PdfPCell celdaLabel(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private static PdfPCell celdaDato(String text, Font font) {
        return celdaDato(text, font, Element.ALIGN_LEFT);
    }

    private static PdfPCell celdaDato(String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "" : text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private static PdfPCell celdaTabla(String text, Font font) {
        return celdaTabla(text, font, Element.ALIGN_CENTER);
    }

    private static PdfPCell celdaTabla(String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "" : text, font));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
        return cell;
    }

    // Utilidad para convertir número a letras (simplificado)
    private static String numToWords(double value) {
        // Puedes usar una librería o función más avanzada si lo deseas
        long parteEntera = (long) value;
        long parteDecimal = Math.round((value - parteEntera) * 100);
        String letras = NumeroALetras.convertir(parteEntera).toUpperCase();
        return letras + " CON " + String.format("%02d", parteDecimal) + "/100";
    }
}

// Puedes crear una clase utilitaria NumeroALetras si no tienes una.
// Ejemplo básico:
class NumeroALetras {
    public static String convertir(long numero) {
        // Implementación simplificada, puedes usar una librería si lo deseas.
        // Aquí solo retorna el número como texto.
        return String.valueOf(numero);
    }
}

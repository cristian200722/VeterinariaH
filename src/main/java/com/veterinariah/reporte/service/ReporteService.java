package com.veterinariah.reporte.service;

import com.veterinariah.cliente.entity.Cliente;
import com.veterinariah.cliente.repository.ClienteRepository;
import com.veterinariah.mascota.entity.Mascota;
import com.veterinariah.mascota.repository.MascotaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {

    private static final ZoneId ZONA_HORARIA = ZoneId.of("America/Bogota");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter ID_FECHA = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final float ANCHO_PAGINA = 792;
    private static final float ALTO_PAGINA = 612;
    private static final float MARGEN = 32;
    private static final float ALTO_FILA = 24;
    private static final Charset WIN_ANSI = Charset.forName("windows-1252");

    private final ClienteRepository clienteRepository;
    private final MascotaRepository mascotaRepository;

    public byte[] generarClientesPdf() { return generarPdf(datosClientes()); }
    public byte[] generarClientesExcel() { return generarExcel(datosClientes()); }
    public byte[] generarMascotasPdf() { return generarPdf(datosMascotas()); }
    public byte[] generarMascotasExcel() { return generarExcel(datosMascotas()); }

    private DatosReporte datosClientes() {
        ZonedDateTime ahora = ZonedDateTime.now(ZONA_HORARIA);
        List<List<String>> filas = clienteRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream().map(this::filaCliente).toList();
        return new DatosReporte("Reporte de clientes", "RPT-CLI-" + ahora.format(ID_FECHA), ahora.format(FECHA),
                List.of("ID", "Cliente", "Teléfono", "Correo", "Dirección"),
                List.of(42f, 145f, 95f, 175f, 235f), filas);
    }

    private List<String> filaCliente(Cliente cliente) {
        return List.of(texto(cliente.getId()), limpiar(cliente.getNombre()) + " " + limpiar(cliente.getApellido()),
                opcional(cliente.getTelefono()), opcional(cliente.getEmail()), opcional(cliente.getDireccion()));
    }

    private DatosReporte datosMascotas() {
        ZonedDateTime ahora = ZonedDateTime.now(ZONA_HORARIA);
        List<List<String>> filas = mascotaRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream().map(this::filaMascota).toList();
        return new DatosReporte("Reporte de mascotas", "RPT-MAS-" + ahora.format(ID_FECHA), ahora.format(FECHA),
                List.of("ID", "Mascota", "Dueño", "Especie", "Raza", "Peso (kg)", "Nacimiento"),
                List.of(38f, 95f, 142f, 78f, 92f, 68f, 105f), filas);
    }

    private List<String> filaMascota(Mascota mascota) {
        String dueno = mascota.getCliente().getNombre() + " " + mascota.getCliente().getApellido();
        String peso = mascota.getPeso() == null ? "No registrado" : mascota.getPeso().stripTrailingZeros().toPlainString();
        String nacimiento = mascota.getFechaNacimiento() == null ? "No registrada"
                : mascota.getFechaNacimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return List.of(texto(mascota.getId()), limpiar(mascota.getNombre()), limpiar(dueno),
                limpiar(mascota.getEspecie()), opcional(mascota.getRaza()), peso, nacimiento);
    }

    private byte[] generarPdf(DatosReporte datos) {
        try (PDDocument documento = new PDDocument(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            List<List<String>> filas = datos.filas().isEmpty() ? List.of(filaVacia(datos.encabezados().size())) : datos.filas();
            PDPageContentStream contenido = null;
            float y = 0;
            for (int i = 0; i < filas.size(); i++) {
                if (contenido == null || y < MARGEN + ALTO_FILA) {
                    if (contenido != null) contenido.close();
                    PDPage pagina = new PDPage(new PDRectangle(ANCHO_PAGINA, ALTO_PAGINA));
                    documento.addPage(pagina);
                    contenido = new PDPageContentStream(documento, pagina);
                    y = dibujarEncabezadoPdf(contenido, datos, pagina);
                }
                List<String> fila = filas.get(i);
                if (datos.filas().isEmpty()) {
                    fila = new ArrayList<>(fila);
                    fila.set(0, "Sin registros para este reporte");
                }
                dibujarFilaPdf(contenido, datos, fila, y);
                y -= ALTO_FILA;
            }
            if (contenido != null) contenido.close();
            documento.save(salida);
            return salida.toByteArray();
        } catch (IOException excepcion) {
            throw new UncheckedIOException("No se pudo generar el reporte PDF", excepcion);
        }
    }

    private float dibujarEncabezadoPdf(PDPageContentStream contenido, DatosReporte datos, PDPage pagina) throws IOException {
        PDType1Font negrita = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDType1Font normal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        float yTitulo = pagina.getMediaBox().getHeight() - 42;
        dibujarTexto(contenido, datos.titulo(), MARGEN, yTitulo, negrita, 18, new Color(20, 54, 82));
        dibujarTexto(contenido, "Generado: " + datos.generadoEn(), MARGEN, yTitulo - 23, normal, 9, Color.DARK_GRAY);
        dibujarTexto(contenido, "Identificación: " + datos.identificador(), MARGEN, yTitulo - 38, normal, 9, Color.DARK_GRAY);
        float yTabla = yTitulo - 58;
        float x = MARGEN;
        for (int columna = 0; columna < datos.encabezados().size(); columna++) {
            dibujarCeldaPdf(contenido, x, yTabla, datos.anchos().get(columna), ALTO_FILA,
                    datos.encabezados().get(columna), true);
            x += datos.anchos().get(columna);
        }
        return yTabla - ALTO_FILA;
    }

    private void dibujarFilaPdf(PDPageContentStream contenido, DatosReporte datos, List<String> fila, float y) throws IOException {
        float x = MARGEN;
        for (int columna = 0; columna < datos.encabezados().size(); columna++) {
            dibujarCeldaPdf(contenido, x, y, datos.anchos().get(columna), ALTO_FILA,
                    columna < fila.size() ? fila.get(columna) : "", false);
            x += datos.anchos().get(columna);
        }
    }

    private void dibujarCeldaPdf(PDPageContentStream contenido, float x, float y, float ancho, float alto,
                                 String valor, boolean encabezado) throws IOException {
        contenido.setNonStrokingColor(encabezado ? new Color(26, 75, 110) : Color.WHITE);
        contenido.addRect(x, y - alto, ancho, alto);
        contenido.fill();
        contenido.setStrokingColor(new Color(205, 215, 224));
        contenido.addRect(x, y - alto, ancho, alto);
        contenido.stroke();
        PDType1Font fuente = new PDType1Font(encabezado ? Standard14Fonts.FontName.HELVETICA_BOLD
                : Standard14Fonts.FontName.HELVETICA);
        float tamano = encabezado ? 8 : 7.5f;
        contenido.setNonStrokingColor(encabezado ? Color.WHITE : Color.BLACK);
        contenido.beginText();
        contenido.setFont(fuente, tamano);
        contenido.newLineAtOffset(x + 4, y - alto / 2 - 2.5f);
        contenido.showText(ajustarTextoPdf(valor, fuente, tamano, ancho - 8));
        contenido.endText();
    }

    private void dibujarTexto(PDPageContentStream contenido, String texto, float x, float y,
                              PDType1Font fuente, float tamano, Color color) throws IOException {
        contenido.beginText();
        contenido.setNonStrokingColor(color);
        contenido.setFont(fuente, tamano);
        contenido.newLineAtOffset(x, y);
        contenido.showText(textoPdf(texto));
        contenido.endText();
    }

    private String ajustarTextoPdf(String texto, PDType1Font fuente, float tamano, float ancho) throws IOException {
        String original = textoPdf(texto);
        String resultado = original;
        while (resultado.length() > 1 && fuente.getStringWidth(resultado) * tamano / 1000 > ancho) {
            resultado = resultado.substring(0, resultado.length() - 1);
        }
        if (!resultado.equals(original)) {
            while (resultado.length() > 1 && fuente.getStringWidth(resultado + "...") * tamano / 1000 > ancho) {
                resultado = resultado.substring(0, resultado.length() - 1);
            }
            resultado += "...";
        }
        return resultado;
    }

    private String textoPdf(String valor) {
        String texto = valor == null ? "" : valor.replaceAll("[\\r\\n\\t]+", " ");
        StringBuilder salida = new StringBuilder(texto.length());
        for (int i = 0; i < texto.length(); i++) {
            char caracter = texto.charAt(i);
            salida.append(WIN_ANSI.newEncoder().canEncode(caracter) ? caracter : '?');
        }
        return salida.toString();
    }

    private byte[] generarExcel(DatosReporte datos) {
        try (Workbook libro = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Sheet hoja = libro.createSheet(datos.titulo().replace("Reporte de ", ""));
            Font fuenteTitulo = libro.createFont();
            fuenteTitulo.setBold(true);
            fuenteTitulo.setFontHeightInPoints((short) 16);
            fuenteTitulo.setColor(IndexedColors.WHITE.getIndex());
            CellStyle estiloTitulo = libro.createCellStyle();
            estiloTitulo.setFont(fuenteTitulo);
            estiloTitulo.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            estiloTitulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font fuenteColumnas = libro.createFont();
            fuenteColumnas.setBold(true);
            fuenteColumnas.setColor(IndexedColors.WHITE.getIndex());
            CellStyle estiloColumnas = libro.createCellStyle();
            estiloColumnas.setFont(fuenteColumnas);
            estiloColumnas.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            estiloColumnas.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row titulo = hoja.createRow(0);
            Cell celdaTitulo = titulo.createCell(0);
            celdaTitulo.setCellValue(datos.titulo());
            celdaTitulo.setCellStyle(estiloTitulo);
            hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, datos.encabezados().size() - 1));
            hoja.createRow(1).createCell(0).setCellValue("Generado: " + datos.generadoEn());
            hoja.createRow(2).createCell(0).setCellValue("Identificación: " + datos.identificador());

            int filaEncabezado = 4;
            Row encabezado = hoja.createRow(filaEncabezado);
            for (int columna = 0; columna < datos.encabezados().size(); columna++) {
                Cell celda = encabezado.createCell(columna);
                celda.setCellValue(datos.encabezados().get(columna));
                celda.setCellStyle(estiloColumnas);
            }
            int siguienteFila = filaEncabezado + 1;
            if (datos.filas().isEmpty()) {
                hoja.createRow(siguienteFila).createCell(0).setCellValue("Sin registros para este reporte");
            } else {
                for (List<String> valores : datos.filas()) {
                    Row fila = hoja.createRow(siguienteFila++);
                    for (int columna = 0; columna < valores.size(); columna++) {
                        fila.createCell(columna).setCellValue(valores.get(columna));
                    }
                }
            }
            hoja.createFreezePane(0, filaEncabezado + 1);
            hoja.setAutoFilter(new CellRangeAddress(filaEncabezado, Math.max(filaEncabezado, siguienteFila - 1),
                    0, datos.encabezados().size() - 1));
            for (int columna = 0; columna < datos.encabezados().size(); columna++) {
                hoja.autoSizeColumn(columna);
                if (hoja.getColumnWidth(columna) > 50 * 256) hoja.setColumnWidth(columna, 50 * 256);
            }
            libro.write(salida);
            return salida.toByteArray();
        } catch (IOException excepcion) {
            throw new UncheckedIOException("No se pudo generar el reporte Excel", excepcion);
        }
    }

    private List<String> filaVacia(int columnas) {
        List<String> fila = new ArrayList<>(columnas);
        for (int i = 0; i < columnas; i++) fila.add("");
        return fila;
    }

    private String texto(Object valor) { return valor == null ? "" : valor.toString(); }
    private String opcional(String valor) { return valor == null || valor.isBlank() ? "No registrado" : limpiar(valor); }
    private String limpiar(String valor) { return valor == null ? "" : valor.trim(); }

    private record DatosReporte(String titulo, String identificador, String generadoEn,
                                List<String> encabezados, List<Float> anchos,
                                List<List<String>> filas) { }
}

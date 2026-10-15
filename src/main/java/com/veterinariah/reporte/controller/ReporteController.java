package com.veterinariah.reporte.controller;

import com.veterinariah.reporte.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private static final MediaType EXCEL = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final ReporteService reporteService;

    @GetMapping(value = "/clientes/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> descargarClientesPdf() {
        return archivo(reporteService.generarClientesPdf(), MediaType.APPLICATION_PDF, "reporte-clientes.pdf");
    }

    @GetMapping(value = "/clientes/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> descargarClientesExcel() {
        return archivo(reporteService.generarClientesExcel(), EXCEL, "reporte-clientes.xlsx");
    }

    @GetMapping(value = "/mascotas/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> descargarMascotasPdf() {
        return archivo(reporteService.generarMascotasPdf(), MediaType.APPLICATION_PDF, "reporte-mascotas.pdf");
    }

    @GetMapping(value = "/mascotas/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> descargarMascotasExcel() {
        return archivo(reporteService.generarMascotasExcel(), EXCEL, "reporte-mascotas.xlsx");
    }

    private ResponseEntity<byte[]> archivo(byte[] contenido, MediaType tipo, String nombre) {
        return ResponseEntity.ok()
                .contentType(tipo)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .body(contenido);
    }
}

package com.veterinariah.mascota.controller;

import com.veterinariah.mascota.dto.MascotaCrearDto;
import com.veterinariah.mascota.dto.MascotaRespuestaDto;
import com.veterinariah.mascota.service.MascotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Punto de entrada HTTP del módulo de mascotas.
 *
 * <p>Recibe las solicitudes que hace el frontend, valida el JSON con
 * {@code @Valid} y delega el trabajo al servicio.</p>
 */
@RestController
@RequestMapping("/api/v1/mascotas")
@RequiredArgsConstructor
public class MascotaController {

    private final MascotaService mascotaService;

    /** POST /api/v1/mascotas: crea una mascota y devuelve HTTP 201. */
    @PostMapping
    public ResponseEntity<MascotaRespuestaDto> crear(@Valid @RequestBody MascotaCrearDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mascotaService.crear(dto));
    }

    /** GET /api/v1/mascotas?clienteId=1: el parámetro clienteId es opcional. */
    @GetMapping
    public ResponseEntity<List<MascotaRespuestaDto>> listar(
            @RequestParam(required = false) Long clienteId) {
        return ResponseEntity.ok(mascotaService.listar(clienteId));
    }

    /** GET /api/v1/mascotas/{id}: consulta una mascota por su identificador. */
    @GetMapping("/{id}")
    public ResponseEntity<MascotaRespuestaDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.obtenerPorId(id));
    }

    /** PUT /api/v1/mascotas/{id}: reemplaza los datos de una mascota existente. */
    @PutMapping("/{id}")
    public ResponseEntity<MascotaRespuestaDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody MascotaCrearDto dto) {
        return ResponseEntity.ok(mascotaService.actualizar(id, dto));
    }

    /** DELETE /api/v1/mascotas/{id}: elimina y devuelve HTTP 204 sin cuerpo. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mascotaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

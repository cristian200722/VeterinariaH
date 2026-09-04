package com.veterinariah.mascota.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos que el backend devuelve al frontend después de consultar, crear o
 * modificar una mascota. Incluye {@code clienteId}, no el objeto Cliente
 * completo, para mantener la respuesta simple y evitar ciclos JSON.
 */
@Getter
@AllArgsConstructor
public class MascotaRespuestaDto {
    private Long id;
    private Long clienteId;
    private String nombre;
    private String especie;
    private String raza;
    private BigDecimal peso;
    private String tipoPelo;
    private LocalDate fechaNacimiento;
}

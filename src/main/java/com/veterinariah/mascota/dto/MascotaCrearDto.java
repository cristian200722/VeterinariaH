package com.veterinariah.mascota.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos que el frontend envía al crear o actualizar una mascota.
 *
 * <p>Este DTO evita recibir directamente la entidad de base de datos y permite
 * validar la entrada antes de ejecutar la lógica de negocio.</p>
 */
@Getter
@Setter
@NoArgsConstructor
public class MascotaCrearDto {

    /** Identificador del cliente dueño de la mascota. */
    @NotNull(message = "El cliente es obligatorio")
    @Positive(message = "El id del cliente debe ser mayor que cero")
    private Long clienteId;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;

    @NotBlank(message = "La especie no puede estar vacía")
    @Size(max = 100, message = "La especie no puede tener más de 100 caracteres")
    private String especie;

    @Size(max = 100, message = "La raza no puede tener más de 100 caracteres")
    private String raza;

    @Positive(message = "El peso debe ser mayor que cero")
    private BigDecimal peso;

    @Size(max = 100, message = "El tipo de pelo no puede tener más de 100 caracteres")
    private String tipoPelo;

    @PastOrPresent(message = "La fecha de nacimiento no puede estar en el futuro")
    private LocalDate fechaNacimiento;
}

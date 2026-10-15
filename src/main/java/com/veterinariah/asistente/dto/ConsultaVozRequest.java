package com.veterinariah.asistente.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConsultaVozRequest(@NotBlank @Size(max = 300) String pregunta) {
}

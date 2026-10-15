package com.veterinariah.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SolicitarRecuperacionDto(
        @NotBlank @Email @Size(max = 150) String correo
) {}

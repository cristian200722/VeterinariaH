package com.veterinariah.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

public record RestablecerContrasenaDto(
        @NotBlank @Email @Size(max = 150) String correo,
        @NotBlank @Pattern(regexp = "\\d{6}", message = "El código debe tener 6 dígitos") String codigo,
        @NotBlank @Size(min = 10, max = 72, message = "La contraseña debe tener entre 10 y 72 caracteres") String nuevaContrasena
) {}

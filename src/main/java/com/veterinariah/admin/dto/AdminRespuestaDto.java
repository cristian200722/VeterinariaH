package com.veterinariah.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminRespuestaDto {

    private Long id;
    private String nombre;
    private String correo;
}
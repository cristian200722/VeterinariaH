package com.veterinariah.admin.mapper;

import com.veterinariah.admin.dto.AdminCrearDto;
import com.veterinariah.admin.dto.AdminRespuestaDto;
import com.veterinariah.admin.entity.Admin;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {

    public Admin toEntity(AdminCrearDto dto) {
        Admin admin = new Admin();
        aplicarCambios(admin, dto);
        return admin;
    }

    public void aplicarCambios(Admin admin, AdminCrearDto dto) {
        admin.setNombre(dto.getNombre().trim());
        admin.setCorreo(dto.getCorreo().trim().toLowerCase());
        admin.setPassword(dto.getPassword());
    }

    public AdminRespuestaDto toDto(Admin admin) {
        return AdminRespuestaDto.builder()
                .id(admin.getId())
                .nombre(admin.getNombre())
                .correo(admin.getCorreo())
                .build();
    }
}
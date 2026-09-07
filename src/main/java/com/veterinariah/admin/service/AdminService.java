package com.veterinariah.admin.service;

import com.veterinariah.admin.dto.AdminCrearDto;
import com.veterinariah.admin.dto.AdminRespuestaDto;
import com.veterinariah.admin.entity.Admin;
import com.veterinariah.admin.mapper.AdminMapper;
import com.veterinariah.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminRespuestaDto crear(AdminCrearDto dto) {

        if (adminRepository.findByCorreo(dto.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        Admin admin = adminMapper.toEntity(dto);

        // Encriptamos la contraseña antes de guardarla
        admin.setPassword(
                passwordEncoder.encode(dto.getPassword())
        );

        Admin adminGuardado = adminRepository.save(admin);

        return adminMapper.toDto(adminGuardado);
    }
}
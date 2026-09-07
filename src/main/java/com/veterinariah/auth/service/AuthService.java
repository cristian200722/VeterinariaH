package com.veterinariah.auth.service;

import com.veterinariah.admin.entity.Admin;
import com.veterinariah.admin.repository.AdminRepository;
import com.veterinariah.auth.dto.LoginRequestDto;
import com.veterinariah.auth.dto.LoginResponseDto;
import com.veterinariah.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponseDto login(LoginRequestDto dto) {

        Admin admin = adminRepository
                .findByCorreo(dto.correo().trim().toLowerCase())
                .orElseThrow(() ->
                        new RuntimeException("Correo o contraseña incorrectos")
                );

        if (!passwordEncoder.matches(dto.password(), admin.getPassword())) {
            throw new RuntimeException("Correo o contraseña incorrectos");
        }

        String token = jwtService.generarToken(admin.getCorreo());

        return new LoginResponseDto(token);
    }
}
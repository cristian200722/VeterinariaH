package com.veterinariah.auth.controller;

import com.veterinariah.auth.dto.LoginRequestDto;
import com.veterinariah.auth.dto.LoginResponseDto;
import com.veterinariah.auth.service.AuthService;
import com.veterinariah.auth.dto.RestablecerContrasenaDto;
import com.veterinariah.auth.dto.SolicitarRecuperacionDto;
import com.veterinariah.auth.service.RecuperacionContrasenaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RecuperacionContrasenaService recuperacionService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto dto
    ) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<Void> solicitarRecuperacion(
            @Valid @RequestBody SolicitarRecuperacionDto dto
    ) {
        recuperacionService.solicitar(dto);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/restablecer-contrasena")
    public ResponseEntity<Void> restablecerContrasena(
            @Valid @RequestBody RestablecerContrasenaDto dto
    ) {
        recuperacionService.restablecer(dto);
        return ResponseEntity.noContent().build();
    }
}

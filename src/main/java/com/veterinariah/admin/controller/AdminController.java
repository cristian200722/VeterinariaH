package com.veterinariah.admin.controller;

import com.veterinariah.admin.dto.AdminCrearDto;
import com.veterinariah.admin.dto.AdminRespuestaDto;
import com.veterinariah.admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<AdminRespuestaDto> crear(
            @Valid @RequestBody AdminCrearDto dto
    ) {
        AdminRespuestaDto respuesta = adminService.crear(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }
}
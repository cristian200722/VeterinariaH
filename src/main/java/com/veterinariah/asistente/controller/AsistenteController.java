package com.veterinariah.asistente.controller;

import com.veterinariah.admin.entity.Admin;
import com.veterinariah.asistente.dto.ConsultaVozRequest;
import com.veterinariah.asistente.dto.RespuestaVozResponse;
import com.veterinariah.asistente.service.AsistenteVozService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/asistente")
@RequiredArgsConstructor
public class AsistenteController {

    private final AsistenteVozService asistenteVozService;

    @PostMapping("/consulta-voz")
    public RespuestaVozResponse consultar(
            @Valid @RequestBody ConsultaVozRequest request,
            @AuthenticationPrincipal Admin admin
    ) {
        return asistenteVozService.consultar(request.pregunta(), admin.getId());
    }
}

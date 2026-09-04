package com.veterinariah.mascota.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinariah.mascota.dto.MascotaCrearDto;
import com.veterinariah.mascota.dto.MascotaRespuestaDto;
import com.veterinariah.mascota.service.MascotaService;
import com.veterinariah.shared.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MascotaController.class)
class MascotaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MascotaService mascotaService;

    @Autowired
    private ObjectMapper objectMapper;

    private MascotaCrearDto dtoValido;
    private MascotaRespuestaDto respuesta;

    @BeforeEach
    void setUp() {
        dtoValido = new MascotaCrearDto();
        dtoValido.setClienteId(1L);
        dtoValido.setNombre("Luna");
        dtoValido.setEspecie("Perro");
        dtoValido.setRaza("Labrador");
        dtoValido.setPeso(new BigDecimal("18.50"));
        dtoValido.setFechaNacimiento(LocalDate.of(2022, 5, 20));

        respuesta = new MascotaRespuestaDto(
                10L, 1L, "Luna", "Perro", "Labrador",
                new BigDecimal("18.50"), null, LocalDate.of(2022, 5, 20)
        );
    }

    @Test
    void crear_datosValidos_retorna201() throws Exception {
        when(mascotaService.crear(any())).thenReturn(respuesta);

        mockMvc.perform(post("/api/v1/mascotas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoValido)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.clienteId").value(1))
                .andExpect(jsonPath("$.nombre").value("Luna"));
    }

    @Test
    void crear_sinCliente_retorna400() throws Exception {
        dtoValido.setClienteId(null);

        mockMvc.perform(post("/api/v1/mascotas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoValido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.clienteId").exists());
    }

    @Test
    void crear_fechaFutura_retorna400() throws Exception {
        dtoValido.setFechaNacimiento(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/v1/mascotas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoValido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fechaNacimiento").exists());
    }

    @Test
    void crear_clienteInexistente_retorna404() throws Exception {
        when(mascotaService.crear(any()))
                .thenThrow(new RecursoNoEncontradoException("No existe un cliente con id 1"));

        mockMvc.perform(post("/api/v1/mascotas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoValido)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No existe un cliente con id 1"));
    }

    @Test
    void listar_sinFiltro_retornaLista() throws Exception {
        when(mascotaService.listar(isNull())).thenReturn(List.of(respuesta));

        mockMvc.perform(get("/api/v1/mascotas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }

    @Test
    void listar_porCliente_retornaListaFiltrada() throws Exception {
        when(mascotaService.listar(1L)).thenReturn(List.of(respuesta));

        mockMvc.perform(get("/api/v1/mascotas").param("clienteId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clienteId").value(1));
    }

    @Test
    void obtenerPorId_noExistente_retorna404() throws Exception {
        when(mascotaService.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("No existe una mascota con id 99"));

        mockMvc.perform(get("/api/v1/mascotas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void eliminar_existente_retorna204() throws Exception {
        mockMvc.perform(delete("/api/v1/mascotas/10"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_noExistente_retorna404() throws Exception {
        doThrow(new RecursoNoEncontradoException("No existe una mascota con id 99"))
                .when(mascotaService).eliminar(99L);

        mockMvc.perform(delete("/api/v1/mascotas/99"))
                .andExpect(status().isNotFound());
    }
}

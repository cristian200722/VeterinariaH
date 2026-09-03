package com.veterinariah.cliente.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinariah.cliente.dto.ClienteCrearDto;
import com.veterinariah.cliente.dto.ClienteRespuestaDto;
import com.veterinariah.cliente.service.ClienteService;
import com.veterinariah.shared.exception.RecursoNoEncontradoException;
import com.veterinariah.shared.exception.RecursoYaExistenteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    private ClienteCrearDto dtoValido;
    private ClienteRespuestaDto respuesta;

    @BeforeEach
    void setUp() {
        dtoValido = new ClienteCrearDto();
        dtoValido.setNombre("Juan");
        dtoValido.setApellido("Pérez");
        dtoValido.setEmail("juan@ejemplo.com");
        dtoValido.setTelefono("3001234567");

        respuesta = new ClienteRespuestaDto(1L, "Juan", "Pérez", "3001234567", "juan@ejemplo.com", null);
    }

    @Test
    void crear_datosValidos_retorna201() throws Exception {
        when(clienteService.crear(any())).thenReturn(respuesta);

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoValido)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("juan@ejemplo.com"));
    }

    @Test
    void crear_sinNombre_retorna400() throws Exception {
        dtoValido.setNombre("");

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoValido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").exists());
    }

    @Test
    void crear_emailDuplicado_retorna409() throws Exception {
        when(clienteService.crear(any())).thenThrow(new RecursoYaExistenteException("El email ya está registrado"));

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoValido)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("El email ya está registrado"));
    }

    @Test
    void listar_retornaLista() throws Exception {
        when(clienteService.listar()).thenReturn(List.of(respuesta));

        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void obtenerPorId_existente_retorna200() throws Exception {
        when(clienteService.obtenerPorId(1L)).thenReturn(respuesta);

        mockMvc.perform(get("/api/v1/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void obtenerPorId_noExistente_retorna404() throws Exception {
        when(clienteService.obtenerPorId(99L)).thenThrow(new RecursoNoEncontradoException("No existe un cliente con id 99"));

        mockMvc.perform(get("/api/v1/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void eliminar_existente_retorna204() throws Exception {
        mockMvc.perform(delete("/api/v1/clientes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_noExistente_retorna404() throws Exception {
        doThrow(new RecursoNoEncontradoException("No existe un cliente con id 99"))
                .when(clienteService).eliminar(99L);

        mockMvc.perform(delete("/api/v1/clientes/99"))
                .andExpect(status().isNotFound());
    }
}

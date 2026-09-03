package com.veterinariah.cliente.service;

import com.veterinariah.cliente.dto.ClienteCrearDto;
import com.veterinariah.cliente.dto.ClienteRespuestaDto;
import com.veterinariah.cliente.entity.Cliente;
import com.veterinariah.cliente.mapper.ClienteMapper;
import com.veterinariah.cliente.repository.ClienteRepository;
import com.veterinariah.shared.exception.RecursoNoEncontradoException;
import com.veterinariah.shared.exception.RecursoYaExistenteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteService clienteService;

    private ClienteCrearDto dto;
    private Cliente clienteGuardado;
    private ClienteRespuestaDto respuestaEsperada;

    @BeforeEach
    void setUp() {
        dto = new ClienteCrearDto();
        dto.setNombre("Juan");
        dto.setApellido("Pérez");
        dto.setEmail("JUAN@EJEMPLO.COM");
        dto.setTelefono("3001234567");
        dto.setDireccion("Calle 1 #2-3");

        clienteGuardado = new Cliente();
        clienteGuardado.setId(1L);
        clienteGuardado.setNombre("Juan");
        clienteGuardado.setApellido("Pérez");
        clienteGuardado.setEmail("juan@ejemplo.com");
        clienteGuardado.setTelefono("3001234567");
        clienteGuardado.setDireccion("Calle 1 #2-3");

        respuestaEsperada = new ClienteRespuestaDto(1L, "Juan", "Pérez", "3001234567", "juan@ejemplo.com", "Calle 1 #2-3");
    }

    @Test
    void crear_exitoso_retornaDto() {
        when(clienteRepository.existsByEmail("juan@ejemplo.com")).thenReturn(false);
        when(clienteMapper.toEntity(dto)).thenReturn(clienteGuardado);
        when(clienteRepository.save(clienteGuardado)).thenReturn(clienteGuardado);
        when(clienteMapper.toDto(clienteGuardado)).thenReturn(respuestaEsperada);

        ClienteRespuestaDto resultado = clienteService.crear(dto);

        assertThat(resultado).isEqualTo(respuestaEsperada);
        verify(clienteRepository).save(clienteGuardado);
    }

    @Test
    void crear_emailDuplicado_lanzaExcepcion() {
        when(clienteRepository.existsByEmail("juan@ejemplo.com")).thenReturn(true);

        assertThatThrownBy(() -> clienteService.crear(dto))
                .isInstanceOf(RecursoYaExistenteException.class)
                .hasMessageContaining("email");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    void listar_retornaListaDeClientes() {
        when(clienteRepository.findAll()).thenReturn(List.of(clienteGuardado));
        when(clienteMapper.toDto(clienteGuardado)).thenReturn(respuestaEsperada);

        List<ClienteRespuestaDto> resultado = clienteService.listar();

        assertThat(resultado).hasSize(1).containsExactly(respuestaEsperada);
    }

    @Test
    void obtenerPorId_existente_retornaDto() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteGuardado));
        when(clienteMapper.toDto(clienteGuardado)).thenReturn(respuestaEsperada);

        ClienteRespuestaDto resultado = clienteService.obtenerPorId(1L);

        assertThat(resultado).isEqualTo(respuestaEsperada);
    }

    @Test
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void actualizar_exitoso_retornaDto() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteGuardado));
        when(clienteRepository.existsByEmailAndIdNot("juan@ejemplo.com", 1L)).thenReturn(false);
        when(clienteRepository.save(clienteGuardado)).thenReturn(clienteGuardado);
        when(clienteMapper.toDto(clienteGuardado)).thenReturn(respuestaEsperada);

        ClienteRespuestaDto resultado = clienteService.actualizar(1L, dto);

        assertThat(resultado).isEqualTo(respuestaEsperada);
        verify(clienteMapper).aplicarCambios(clienteGuardado, dto);
    }

    @Test
    void actualizar_emailDuplicado_lanzaExcepcion() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteGuardado));
        when(clienteRepository.existsByEmailAndIdNot("juan@ejemplo.com", 1L)).thenReturn(true);

        assertThatThrownBy(() -> clienteService.actualizar(1L, dto))
                .isInstanceOf(RecursoYaExistenteException.class);

        verify(clienteRepository, never()).save(any());
    }

    @Test
    void eliminar_existente_eliminaCorrectamente() {
        when(clienteRepository.existsById(1L)).thenReturn(true);

        clienteService.eliminar(1L);

        verify(clienteRepository).deleteById(1L);
    }

    @Test
    void eliminar_noExistente_lanzaExcepcion() {
        when(clienteRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> clienteService.eliminar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(clienteRepository, never()).deleteById(any());
    }
}

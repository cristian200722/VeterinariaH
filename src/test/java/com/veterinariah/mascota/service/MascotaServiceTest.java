package com.veterinariah.mascota.service;

import com.veterinariah.cliente.entity.Cliente;
import com.veterinariah.cliente.repository.ClienteRepository;
import com.veterinariah.mascota.dto.MascotaCrearDto;
import com.veterinariah.mascota.dto.MascotaRespuestaDto;
import com.veterinariah.mascota.entity.Mascota;
import com.veterinariah.mascota.mapper.MascotaMapper;
import com.veterinariah.mascota.repository.MascotaRepository;
import com.veterinariah.shared.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MascotaServiceTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private MascotaMapper mascotaMapper;

    @InjectMocks
    private MascotaService mascotaService;

    private MascotaCrearDto dto;
    private Cliente cliente;
    private Mascota mascotaGuardada;
    private MascotaRespuestaDto respuestaEsperada;

    @BeforeEach
    void setUp() {
        dto = new MascotaCrearDto();
        dto.setClienteId(1L);
        dto.setNombre("Luna");
        dto.setEspecie("Perro");
        dto.setRaza("Labrador");
        dto.setPeso(new BigDecimal("18.50"));
        dto.setTipoPelo("Corto");
        dto.setFechaNacimiento(LocalDate.of(2022, 5, 20));

        cliente = new Cliente();
        cliente.setId(1L);

        mascotaGuardada = new Mascota();
        mascotaGuardada.setId(10L);
        mascotaGuardada.setCliente(cliente);
        mascotaGuardada.setNombre("Luna");

        respuestaEsperada = new MascotaRespuestaDto(
                10L, 1L, "Luna", "Perro", "Labrador",
                new BigDecimal("18.50"), "Corto", LocalDate.of(2022, 5, 20)
        );
    }

    @Test
    void crear_conClienteExistente_retornaDto() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(mascotaMapper.toEntity(dto, cliente)).thenReturn(mascotaGuardada);
        when(mascotaRepository.save(mascotaGuardada)).thenReturn(mascotaGuardada);
        when(mascotaMapper.toDto(mascotaGuardada)).thenReturn(respuestaEsperada);

        MascotaRespuestaDto resultado = mascotaService.crear(dto);

        assertThat(resultado).isEqualTo(respuestaEsperada);
        verify(mascotaRepository).save(mascotaGuardada);
    }

    @Test
    void crear_conClienteInexistente_lanzaExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());
        dto.setClienteId(99L);

        assertThatThrownBy(() -> mascotaService.crear(dto))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("cliente");

        verify(mascotaRepository, never()).save(any());
    }

    @Test
    void listar_sinFiltro_retornaTodasLasMascotas() {
        when(mascotaRepository.findAll()).thenReturn(List.of(mascotaGuardada));
        when(mascotaMapper.toDto(mascotaGuardada)).thenReturn(respuestaEsperada);

        List<MascotaRespuestaDto> resultado = mascotaService.listar(null);

        assertThat(resultado).containsExactly(respuestaEsperada);
        verify(mascotaRepository).findAll();
    }

    @Test
    void listar_porCliente_retornaMascotasDelCliente() {
        when(mascotaRepository.findByClienteIdOrderByNombreAsc(1L)).thenReturn(List.of(mascotaGuardada));
        when(mascotaMapper.toDto(mascotaGuardada)).thenReturn(respuestaEsperada);

        List<MascotaRespuestaDto> resultado = mascotaService.listar(1L);

        assertThat(resultado).containsExactly(respuestaEsperada);
        verify(mascotaRepository).findByClienteIdOrderByNombreAsc(1L);
    }

    @Test
    void obtenerPorId_existente_retornaDto() {
        when(mascotaRepository.findById(10L)).thenReturn(Optional.of(mascotaGuardada));
        when(mascotaMapper.toDto(mascotaGuardada)).thenReturn(respuestaEsperada);

        MascotaRespuestaDto resultado = mascotaService.obtenerPorId(10L);

        assertThat(resultado).isEqualTo(respuestaEsperada);
    }

    @Test
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mascotaService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void actualizar_conClienteExistente_retornaDto() {
        when(mascotaRepository.findById(10L)).thenReturn(Optional.of(mascotaGuardada));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(mascotaRepository.save(mascotaGuardada)).thenReturn(mascotaGuardada);
        when(mascotaMapper.toDto(mascotaGuardada)).thenReturn(respuestaEsperada);

        MascotaRespuestaDto resultado = mascotaService.actualizar(10L, dto);

        assertThat(resultado).isEqualTo(respuestaEsperada);
        verify(mascotaMapper).aplicarCambios(mascotaGuardada, dto, cliente);
    }

    @Test
    void actualizar_mascotaInexistente_lanzaExcepcion() {
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mascotaService.actualizar(99L, dto))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(mascotaRepository, never()).save(any());
    }

    @Test
    void eliminar_existente_eliminaCorrectamente() {
        when(mascotaRepository.existsById(10L)).thenReturn(true);

        mascotaService.eliminar(10L);

        verify(mascotaRepository).deleteById(10L);
    }

    @Test
    void eliminar_noExistente_lanzaExcepcion() {
        when(mascotaRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> mascotaService.eliminar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(mascotaRepository, never()).deleteById(any());
    }
}

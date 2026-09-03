package com.veterinariah.cliente.service;

import com.veterinariah.cliente.dto.ClienteCrearDto;
import com.veterinariah.cliente.dto.ClienteRespuestaDto;
import com.veterinariah.cliente.entity.Cliente;
import com.veterinariah.cliente.mapper.ClienteMapper;
import com.veterinariah.cliente.repository.ClienteRepository;
import com.veterinariah.shared.exception.RecursoNoEncontradoException;
import com.veterinariah.shared.exception.RecursoYaExistenteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Transactional
    public ClienteRespuestaDto crear(ClienteCrearDto dto) {
        String email = dto.getEmail().trim().toLowerCase();
        if (clienteRepository.existsByEmail(email)) {
            throw new RecursoYaExistenteException("El email ya está registrado");
        }
        Cliente cliente = clienteMapper.toEntity(dto);
        return clienteMapper.toDto(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public List<ClienteRespuestaDto> listar() {
        return clienteRepository.findAll().stream()
                .map(clienteMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteRespuestaDto obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .map(clienteMapper::toDto)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un cliente con id " + id));
    }

    @Transactional
    public ClienteRespuestaDto actualizar(Long id, ClienteCrearDto dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un cliente con id " + id));

        String email = dto.getEmail().trim().toLowerCase();
        if (clienteRepository.existsByEmailAndIdNot(email, id)) {
            throw new RecursoYaExistenteException("El email ya está registrado");
        }

        clienteMapper.aplicarCambios(cliente, dto);
        return clienteMapper.toDto(clienteRepository.save(cliente));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("No existe un cliente con id " + id);
        }
        clienteRepository.deleteById(id);
    }
}

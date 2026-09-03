package com.veterinariah.cliente.mapper;

import com.veterinariah.cliente.dto.ClienteCrearDto;
import com.veterinariah.cliente.dto.ClienteRespuestaDto;
import com.veterinariah.cliente.entity.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteCrearDto dto) {
        Cliente cliente = new Cliente();
        aplicarCambios(cliente, dto);
        return cliente;
    }

    public void aplicarCambios(Cliente cliente, ClienteCrearDto dto) {
        cliente.setNombre(dto.getNombre().trim());
        cliente.setApellido(dto.getApellido().trim());
        cliente.setTelefono(dto.getTelefono() != null ? dto.getTelefono().trim() : null);
        cliente.setEmail(dto.getEmail().trim().toLowerCase());
        cliente.setDireccion(dto.getDireccion() != null ? dto.getDireccion().trim() : null);
    }

    public ClienteRespuestaDto toDto(Cliente cliente) {
        return new ClienteRespuestaDto(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getTelefono(),
                cliente.getEmail(),
                cliente.getDireccion()
        );
    }
}

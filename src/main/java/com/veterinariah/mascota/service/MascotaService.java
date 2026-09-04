package com.veterinariah.mascota.service;

import com.veterinariah.cliente.entity.Cliente;
import com.veterinariah.cliente.repository.ClienteRepository;
import com.veterinariah.mascota.dto.MascotaCrearDto;
import com.veterinariah.mascota.dto.MascotaRespuestaDto;
import com.veterinariah.mascota.entity.Mascota;
import com.veterinariah.mascota.mapper.MascotaMapper;
import com.veterinariah.mascota.repository.MascotaRepository;
import com.veterinariah.shared.exception.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Contiene la lógica de negocio del CRUD de mascotas.
 *
 * <p>El controlador no consulta directamente la base de datos: delega en esta
 * clase para validar que el cliente dueño exista y para manejar los casos en
 * que no se encuentra una mascota.</p>
 */
@Service
@RequiredArgsConstructor
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final ClienteRepository clienteRepository;
    private final MascotaMapper mascotaMapper;

    /** Crea una mascota después de comprobar que su dueño existe. */
    @Transactional
    public MascotaRespuestaDto crear(MascotaCrearDto dto) {
        // Evita que la clave foránea cliente_id apunte a un cliente inexistente.
        Cliente cliente = buscarCliente(dto.getClienteId());
        Mascota mascota = mascotaMapper.toEntity(dto, cliente);
        return mascotaMapper.toDto(mascotaRepository.save(mascota));
    }

    /**
     * Lista todas las mascotas o, si llega un clienteId, solo las mascotas de
     * ese cliente. El filtro es opcional en la URL.
     */
    @Transactional(readOnly = true)
    public List<MascotaRespuestaDto> listar(Long clienteId) {
        List<Mascota> mascotas = clienteId == null
                ? mascotaRepository.findAll()
                : mascotaRepository.findByClienteIdOrderByNombreAsc(clienteId);

        return mascotas.stream()
                .map(mascotaMapper::toDto)
                .toList();
    }

    /** Busca una mascota concreta o responde con un error 404 controlado. */
    @Transactional(readOnly = true)
    public MascotaRespuestaDto obtenerPorId(Long id) {
        return mascotaRepository.findById(id)
                .map(mascotaMapper::toDto)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una mascota con id " + id));
    }

    /** Actualiza los datos y permite cambiar el dueño si se envía otro clienteId válido. */
    @Transactional
    public MascotaRespuestaDto actualizar(Long id, MascotaCrearDto dto) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una mascota con id " + id));

        Cliente cliente = buscarCliente(dto.getClienteId());
        mascotaMapper.aplicarCambios(mascota, dto, cliente);
        return mascotaMapper.toDto(mascotaRepository.save(mascota));
    }

    /** Elimina una mascota solo si existe; de lo contrario devuelve 404. */
    @Transactional
    public void eliminar(Long id) {
        if (!mascotaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("No existe una mascota con id " + id);
        }
        mascotaRepository.deleteById(id);
    }

    /** Obtiene el cliente dueño o detiene la operación con un mensaje claro. */
    private Cliente buscarCliente(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un cliente con id " + clienteId));
    }
}

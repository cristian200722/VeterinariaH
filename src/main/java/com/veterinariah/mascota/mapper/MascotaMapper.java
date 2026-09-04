package com.veterinariah.mascota.mapper;

import com.veterinariah.cliente.entity.Cliente;
import com.veterinariah.mascota.dto.MascotaCrearDto;
import com.veterinariah.mascota.dto.MascotaRespuestaDto;
import com.veterinariah.mascota.entity.Mascota;
import org.springframework.stereotype.Component;

/**
 * Convierte los datos entre las capas de la aplicación.
 *
 * <p>El controlador recibe un {@link MascotaCrearDto}, el servicio trabaja con
 * {@link Mascota} y finalmente se devuelve un {@link MascotaRespuestaDto}.</p>
 */
@Component
public class MascotaMapper {

    /** Crea una entidad nueva a partir de los datos validados y su dueño. */
    public Mascota toEntity(MascotaCrearDto dto, Cliente cliente) {
        Mascota mascota = new Mascota();
        aplicarCambios(mascota, dto, cliente);
        return mascota;
    }

    /**
     * Copia los valores del DTO a una mascota. Se reutiliza tanto al crear como
     * al actualizar para que ambos procesos apliquen las mismas reglas.
     */
    public void aplicarCambios(Mascota mascota, MascotaCrearDto dto, Cliente cliente) {
        mascota.setCliente(cliente);
        mascota.setNombre(dto.getNombre().trim());
        mascota.setEspecie(dto.getEspecie().trim());
        mascota.setRaza(textoOpcional(dto.getRaza()));
        mascota.setPeso(dto.getPeso());
        mascota.setTipoPelo(textoOpcional(dto.getTipoPelo()));
        mascota.setFechaNacimiento(dto.getFechaNacimiento());
    }

    /** Prepara una respuesta segura y sencilla para convertirla a JSON. */
    public MascotaRespuestaDto toDto(Mascota mascota) {
        return new MascotaRespuestaDto(
                mascota.getId(),
                mascota.getCliente().getId(),
                mascota.getNombre(),
                mascota.getEspecie(),
                mascota.getRaza(),
                mascota.getPeso(),
                mascota.getTipoPelo(),
                mascota.getFechaNacimiento()
        );
    }

    /** Convierte textos vacíos en {@code null} para no guardar cadenas inútiles. */
    private String textoOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}

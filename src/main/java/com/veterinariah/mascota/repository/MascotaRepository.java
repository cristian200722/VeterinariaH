package com.veterinariah.mascota.repository;

import com.veterinariah.mascota.entity.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Capa de acceso a datos de mascotas. JpaRepository proporciona los métodos
 * básicos como save, findAll, findById y deleteById sin escribir SQL manual.
 */
public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    /** Spring Data genera la consulta para obtener las mascotas de un cliente ordenadas por nombre. */
    List<Mascota> findByClienteIdOrderByNombreAsc(Long clienteId);
}

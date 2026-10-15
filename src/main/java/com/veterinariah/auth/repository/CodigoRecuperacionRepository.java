package com.veterinariah.auth.repository;

import com.veterinariah.auth.entity.CodigoRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodigoRecuperacionRepository extends JpaRepository<CodigoRecuperacion, Long> {

    Optional<CodigoRecuperacion> findFirstByAdmin_IdOrderByCreadoEnDesc(Long adminId);
}

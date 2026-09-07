package com.veterinariah.admin.repository;

import com.veterinariah.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByCorreo(String correo);

}

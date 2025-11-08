package com.trabajopp1.backendpp1.repository;

import com.trabajopp1.backendpp1.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

//Repositorio para la entidad principal (Usuario)
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // Necesitas encontrar un usuario por su correo para el login
    Optional<Usuario> findByCorreo(String correo);
}

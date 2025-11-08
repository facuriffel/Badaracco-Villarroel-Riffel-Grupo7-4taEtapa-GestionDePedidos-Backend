package com.trabajopp1.backendpp1.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.trabajopp1.backendpp1.entity.Plato;

//Repositorio para la tabla Plato
public interface PlatoRepository extends JpaRepository<Plato, Integer> {
    // Si quieres encontrar platos por categoría (ej. "Verduras", "Carnes")
    List<Plato> findByCategoria(String categoria);
}

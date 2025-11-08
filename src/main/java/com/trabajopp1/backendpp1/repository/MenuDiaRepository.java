package com.trabajopp1.backendpp1.repository;

import com.trabajopp1.backendpp1.entity.MenuDia;
import org.springframework.data.jpa.repository.JpaRepository;

// Esta interfaz hereda automáticamente métodos CRUD básicos (save, findById, findAll, delete)
public interface MenuDiaRepository extends JpaRepository<MenuDia, Integer> {
    
    // Aquí puedes añadir métodos de búsqueda personalizados si los necesitas,
    // como buscar un menú por su fecha, pero JpaRepository ya te da lo básico.
}

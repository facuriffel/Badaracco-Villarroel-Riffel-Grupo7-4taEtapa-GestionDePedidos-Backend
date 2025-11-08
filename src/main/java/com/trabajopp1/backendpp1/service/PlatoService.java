package com.trabajopp1.backendpp1.service;

import com.trabajopp1.backendpp1.dto.PlatoCreacionDTO;
import com.trabajopp1.backendpp1.entity.Plato;
import com.trabajopp1.backendpp1.repository.PlatoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlatoService {

    private final PlatoRepository platoRepository;
    
    // ------------------------------------------------------------------
    // 1. CREAR PLATO (CREATE)
    // ------------------------------------------------------------------
    /**
     * Crea un nuevo plato en el sistema a partir de un DTO.
     */
    public PlatoCreacionDTO crearPlato(PlatoCreacionDTO dto) {
        // Mapear DTO a Entity
        Plato nuevoPlato = new Plato();
        // Asumiendo que el DTO ya pasó la validación (Bean Validation en el Controller)
        nuevoPlato.setNombre(dto.getNombre());
        nuevoPlato.setDescripcion(dto.getDescripcion());
        nuevoPlato.setCategoria(dto.getCategoria());
        nuevoPlato.setImagen(dto.getImagen());
        
        Plato platoGuardado = platoRepository.save(nuevoPlato);
        
        // Devolver el DTO con el ID generado
        dto.setId(platoGuardado.getId());
        return dto;
    }

    // ------------------------------------------------------------------
    // 2. LISTAR TODOS (READ ALL)
    // ------------------------------------------------------------------
    /**
     * Obtiene la lista completa de platos.
     */
    public List<Plato> obtenerTodosLosPlatos() {
        return platoRepository.findAll();
    }
    
    // ------------------------------------------------------------------
    // 3. ACTUALIZAR PLATO (UPDATE)
    // ------------------------------------------------------------------
    /**
     * Actualiza un plato existente por su ID.
     */
    public PlatoCreacionDTO actualizarPlato(Integer idPlato, PlatoCreacionDTO dto) {
        
        Plato platoExistente = platoRepository.findById(idPlato)
            .orElseThrow(() -> new EntityNotFoundException("Plato no encontrado con ID: " + idPlato));
        
        // Actualizar campos
        platoExistente.setNombre(dto.getNombre());
        platoExistente.setDescripcion(dto.getDescripcion());
        platoExistente.setCategoria(dto.getCategoria());
        platoExistente.setImagen(dto.getImagen());
        
        platoRepository.save(platoExistente);
        
        // El DTO ya tiene el ID, lo devolvemos para confirmación
        return dto;
    }

    // ------------------------------------------------------------------
    // 4. ELIMINAR PLATO (DELETE)
    // ------------------------------------------------------------------
    /**
     * Elimina un plato por su ID.
     * IMPORTANTE: En un sistema real, antes de eliminar, deberías verificar 
     * si el plato está asociado a menús activos o pedidos históricos.
     */
    public void eliminarPlato(Integer idPlato) {
        Plato platoExistente = platoRepository.findById(idPlato)
            .orElseThrow(() -> new EntityNotFoundException("Plato no encontrado con ID: " + idPlato));

        platoRepository.delete(platoExistente);
    }
}
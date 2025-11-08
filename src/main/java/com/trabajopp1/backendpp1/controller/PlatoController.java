package com.trabajopp1.backendpp1.controller;

import com.trabajopp1.backendpp1.dto.PlatoCreacionDTO;
import com.trabajopp1.backendpp1.entity.Plato;
import com.trabajopp1.backendpp1.service.PlatoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/platos") // Rutas protegidas para el rol Cocina/Admin
@RequiredArgsConstructor
public class PlatoController {
    
    private final PlatoService platoService;


    // POST /api/admin/platos (Crear)
    @PostMapping
    public ResponseEntity<PlatoCreacionDTO> crearPlato(@Valid @RequestBody PlatoCreacionDTO dto) {
        PlatoCreacionDTO platoCreado = platoService.crearPlato(dto);
        return new ResponseEntity<>(platoCreado, HttpStatus.CREATED); 
    }

    // GET /api/admin/platos (Listar todo el catálogo)
    @GetMapping
    public ResponseEntity<List<Plato>> listarTodos() {
        return ResponseEntity.ok(platoService.obtenerTodosLosPlatos());
    }
    
    // PUT /api/admin/platos/{id} (Modificar)
    @PutMapping("/{id}")
    public ResponseEntity<PlatoCreacionDTO> actualizarPlato(
        @PathVariable Integer id,
        @Valid @RequestBody PlatoCreacionDTO dto) 
    {
        PlatoCreacionDTO platoActualizado = platoService.actualizarPlato(id, dto);
        return ResponseEntity.ok(platoActualizado);
    }

    // DELETE /api/admin/platos/{id} (Eliminar)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarPlato(@PathVariable Integer id) {
        platoService.eliminarPlato(id);
    }
}

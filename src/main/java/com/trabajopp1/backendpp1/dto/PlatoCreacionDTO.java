package com.trabajopp1.backendpp1.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

// DTO para CREAR/MODIFICAR un Plato (Input Cocina)
@Data
public class PlatoCreacionDTO {
    // Si se usa para modificación, se incluye el id
    private Integer id; 

    @NotBlank(message = "El nombre del plato es obligatorio")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
    
    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;
    
    @Pattern(regexp = "^(http|https).*$", message = "Debe ser una URL de imagen válida")
    private String imagen; // URL de la foto
}



package com.trabajopp1.backendpp1.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

// DTO para especificar un plato y su stock dentro de un menú
@Data
public class PlatoStockDTO {
    @NotNull
    private Integer idPlato; // El ID del plato del catálogo
    
    @Min(value = 1, message = "El stock debe ser al menos 1")
    private Integer stockDisponible;
}

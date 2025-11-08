package com.trabajopp1.backendpp1.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MenuDiaCreacionDTO {
    
    @NotNull(message = "La fecha del menú es obligatoria")
    private LocalDate fecha;

    @NotBlank(message = "La descripción del menú es obligatoria")
    private String descripcion;
    
    // true si ya está listo para que los empleados lo vean
    private Boolean publicado = false; 

    @NotEmpty(message = "El menú debe contener al menos un plato")
    private List<PlatoStockDTO> items; // La lista de platos y su stock
}

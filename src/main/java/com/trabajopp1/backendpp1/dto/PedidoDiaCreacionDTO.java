package com.trabajopp1.backendpp1.dto;

import lombok.Data;
import java.time.LocalDate;
import jakarta.validation.constraints.*;

//DTO para que el EMPLEADO realice un pedido (Input)
@Data
public class PedidoDiaCreacionDTO {
    
    @NotNull(message = "Se requiere el ID del menú-plato")
    private Integer idMenuPlato; // El ID de la tabla MenuPlato
    
    @NotNull(message = "La fecha de entrega es obligatoria")
    private LocalDate fechaEntrega;
    
    // NOTA: El ID del usuario y el estado del pedido se manejan en la capa Service
}

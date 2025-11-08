package com.trabajopp1.backendpp1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Este DTO define la estructura de la lista de producción para la cocina
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResumenCocinaDTO {
    
    // El nombre del plato (ej: Lasagna Clásica)
    private String nombrePlato;
    
    // La categoría (ej: Principal)
    private String categoriaPlato;
    
    // El total contado de pedidos para ese plato en la fecha
    private Long totalPedidos; 
}
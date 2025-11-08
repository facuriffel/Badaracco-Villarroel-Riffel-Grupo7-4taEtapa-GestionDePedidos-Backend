package com.trabajopp1.backendpp1.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PedidoDiaDetalleDTO {
    private Integer idPedidoDia; // PK para modificar/eliminar
    private String nombrePlato;
    private String categoriaPlato;
    private LocalDate fechaEntrega;
    private String urlImagenPlato;
    // Podrías añadir el estado del pedido, pero lo simplificamos aquí
}

package com.trabajopp1.backendpp1.dto;

import lombok.Data;

@Data
public class PedidoEstadoDTO {
    private Integer id;
    private String estado; // Para devolver PENDIENTE o CONFIRMADO como String
}

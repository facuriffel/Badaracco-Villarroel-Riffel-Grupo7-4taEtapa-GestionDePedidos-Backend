package com.trabajopp1.backendpp1.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ProduccionDiaDTO {

    private LocalDate fechaEntrega;
    private String nombrePlato;
    private String urlImagenPlato; // 🚨 NUEVO CAMPO
    private Long cantidadTotal;

    // 🚨 CONSTRUCTOR ACTUALIZADO
    public ProduccionDiaDTO(LocalDate fechaEntrega, String nombrePlato, String urlImagenPlato, Long cantidadTotal) {
        this.fechaEntrega = fechaEntrega;
        this.nombrePlato = nombrePlato;
        this.urlImagenPlato = urlImagenPlato;
        this.cantidadTotal = cantidadTotal;
    }
}
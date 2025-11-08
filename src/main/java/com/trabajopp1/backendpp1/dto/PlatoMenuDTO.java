package com.trabajopp1.backendpp1.dto;

import java.time.LocalDate;

import lombok.Data;

// DTO para mostrar el MENÚ DIARIO (Output Empleado/Cocina)
// Combina info de Plato y MenuPlato (Stock)
@Data
public class PlatoMenuDTO {
    private Integer idMenuPlato; // PK de MenuPlato (se usa para el pedido)
    private Integer idPlato; // PK de Plato
    private String nombre;
    private String descripcion;
    private String categoria;
    private String urlImagen;
    private Integer stockDisponible;
    private LocalDate fechaMenu;
}

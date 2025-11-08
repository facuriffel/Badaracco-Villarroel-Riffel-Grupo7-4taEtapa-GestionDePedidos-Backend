package com.trabajopp1.backendpp1.dto;

import lombok.Data;

@Data
public class UsuarioPerfilDTO {
    private Integer id;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String direccion;
    private Boolean esUsuarioRestaurante; 
}
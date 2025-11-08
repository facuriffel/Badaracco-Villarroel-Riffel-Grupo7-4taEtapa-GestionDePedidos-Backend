package com.trabajopp1.backendpp1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// AuthRequestDTO.java (Para recibir credenciales de login)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDTO {
    private String correo;
    private String contrasena;
}

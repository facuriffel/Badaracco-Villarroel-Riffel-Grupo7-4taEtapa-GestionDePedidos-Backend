package com.trabajopp1.backendpp1.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class UsuarioRegistroDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Email(message = "Debe ser un correo electrónico válido")
    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;
    
    // Estos campos no tienen @NotBlank porque son opcionales en el registro
    private String telefono;
    private String direccion;
    private String codigoSecreto;
    
    // El rol (esUsuarioRestaurante) se asigna a FALSE en el AuthService, no se recibe aquí.
}
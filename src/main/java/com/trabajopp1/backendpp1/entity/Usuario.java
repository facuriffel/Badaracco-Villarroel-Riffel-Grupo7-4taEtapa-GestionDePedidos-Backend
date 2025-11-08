package com.trabajopp1.backendpp1.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oid_usuario") // PK: Convención oid_nombre (o sea es la principal)
    private Integer id; 

    @Column(name = "nombre", length = 255)
    private String nombre;

    @Column(name = "apellido", length = 255)
    private String apellido;

    @Column(name = "correo", length = 255, unique = true)
    private String correo; 

    @Column(name = "contrasena", length = 255)
    private String contrasena;

    @Column(name = "telefono", length = 255)
    private String telefono;

    @Column(name = "direccion", length = 255)
    private String direccion;

    @Column(name = "es_usuario_restaurante")
    private Boolean esUsuarioRestaurante = false;

    @Column(name = "activo")
    private Boolean activo = true;
}


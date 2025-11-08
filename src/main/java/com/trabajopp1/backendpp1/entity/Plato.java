package com.trabajopp1.backendpp1.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plato")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oid_plato") 
    private Integer id; 

    @Column(name = "nombre", length = 255)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "categoria", length = 100) 
    private String categoria;

    @Column(name = "imagen", length = 255) 
    private String imagen;
}
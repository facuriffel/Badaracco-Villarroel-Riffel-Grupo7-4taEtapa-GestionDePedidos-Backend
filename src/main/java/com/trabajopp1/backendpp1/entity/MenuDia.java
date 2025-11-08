package com.trabajopp1.backendpp1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "menu_dia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuDia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oid_menu_dia") 
    private Integer id; 

    @Column(name = "fecha")
    private LocalDate fecha; 

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "publicado")
    private Boolean publicado = false;

    // Relación N:1 con Usuario (FK: id_usuario_creador)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_creador", nullable = false) // FK: id_nombre
    @ToString.Exclude
    private Usuario creador; 
}
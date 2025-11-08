package com.trabajopp1.backendpp1.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "menu_plato")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuPlato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oid_menu_plato") // PK: Convención oid_nombre
    private Integer id; // PK artificial

    // FK: id_plato // Relación N:1 con Plato (id_plato FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plato", nullable = false) 
    @ToString.Exclude
    private Plato plato; 

    // FK: id_menu_dia // Relación N:1 con MenuDia (id_menu_dia FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_menu_dia", nullable = false)
    @ToString.Exclude
    private MenuDia menuDia; 

    @Column(name = "stock_disponible")
    private Integer stockDisponible;
}

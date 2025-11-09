package com.trabajopp1.backendpp1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "pedido_dia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oid_pedido_dia")
    private Integer id; // PK

    @Column(name = "fecha_entrega")
    private LocalDate fechaEntrega; // DATE

    // Relación N:1 con Pedido (id_pedido FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    @ToString.Exclude
    private Pedido pedido; // FK

    
    // Relación N:1 con MenuPlato (id_menu_plato FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_menu_plato", nullable = false)
    @ToString.Exclude
    private MenuPlato menuPlato; 

}
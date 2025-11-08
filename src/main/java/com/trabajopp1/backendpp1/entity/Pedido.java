package com.trabajopp1.backendpp1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oid_pedido")
    private Integer id; 

    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 15) 
    private EstadoPedido estado;

    @Column(name = "cantidad_personas")
    private Integer cantidadPersonas;

    // FK: id_usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false) // FK: id_nombre
    @ToString.Exclude
    private Usuario usuario; 

    public enum EstadoPedido {
        PENDIENTE,
        CONFIRMADO,
        CANCELADO
    }
}

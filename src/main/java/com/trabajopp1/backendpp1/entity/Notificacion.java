package com.trabajopp1.backendpp1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oid_notificacion")
    private Integer id; 

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio; 

    @Column(name = "asunto", length = 255)
    private String asunto;

    @Column(name = "mensaje", columnDefinition = "TEXT")
    private String mensaje; 

    // FK: id_usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false) // FK: id_nombre
    @ToString.Exclude
    private Usuario usuario;
}
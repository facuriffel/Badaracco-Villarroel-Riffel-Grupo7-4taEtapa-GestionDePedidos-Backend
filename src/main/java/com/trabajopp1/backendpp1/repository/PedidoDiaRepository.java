package com.trabajopp1.backendpp1.repository;

import com.trabajopp1.backendpp1.dto.PedidoResumenCocinaDTO;
import com.trabajopp1.backendpp1.entity.PedidoDia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PedidoDiaRepository extends JpaRepository<PedidoDia, Integer> {

    // 1. Consulta para la regla de negocio: SOLO UN PLATO POR DÍA por usuario
    // Busca si ya existe un PedidoDia para un usuario específico en una fecha de entrega.
    @Query("SELECT pd FROM PedidoDia pd JOIN pd.pedido p WHERE p.usuario.id = :idUsuario AND pd.fechaEntrega = :fechaEntrega")
    Optional<PedidoDia> findByUsuarioAndFechaEntrega(@Param("idUsuario") Integer idUsuario, @Param("fechaEntrega") LocalDate fechaEntrega);

    // 2. Consulta para listar los pedidos realizados por un empleado
    // Busca todos los PedidoDia relacionados con el ID del Pedido principal del usuario.
    List<PedidoDia> findByPedidoUsuarioIdOrderByFechaEntregaAsc(Integer idUsuario);

    // 3. Consulta de agregación para el RESUMEN DE COCINA
    // Agrupa los pedidos por el nombre del plato y cuenta el total, devolviendo el DTO de resumen.
    @Query("SELECT new com.trabajopp1.backendpp1.dto.PedidoResumenCocinaDTO(" +
           "p.nombre, p.categoria, COUNT(pd)) " +
           "FROM PedidoDia pd " +
           "JOIN pd.menuPlato mp " +
           "JOIN mp.plato p " +
           "WHERE pd.fechaEntrega = :fecha " +
           "GROUP BY p.nombre, p.categoria")
    List<PedidoResumenCocinaDTO> obtenerResumenPedidosPorFecha(@Param("fecha") LocalDate fecha);

    // Método necesario para la eliminación en cascada manual
    List<PedidoDia> findByMenuPlato_Id(Integer id);

}

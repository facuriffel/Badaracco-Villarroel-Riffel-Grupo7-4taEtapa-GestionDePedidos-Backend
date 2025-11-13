package com.trabajopp1.backendpp1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.trabajopp1.backendpp1.dto.ProduccionDiaDTO;
import com.trabajopp1.backendpp1.entity.Pedido;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    // Para obtener el pedido principal del usuario
    Optional<Pedido> findByUsuario_Id(Integer idUsuario);



    @Query("SELECT new com.trabajopp1.backendpp1.dto.ProduccionDiaDTO("
    + "pd.fechaEntrega, pd.menuPlato.plato.nombre, pd.menuPlato.plato.imagen, COUNT(pd)) " // 🚨 URL IMAGEN AÑADIDA
    + "FROM PedidoDia pd JOIN pd.pedido p " 
    + "WHERE p.estado = 'CONFIRMADO' AND pd.fechaEntrega BETWEEN :fechaInicio AND :fechaFin "
    + "GROUP BY pd.fechaEntrega, pd.menuPlato.plato.nombre, pd.menuPlato.plato.imagen " // 🚨 URL IMAGEN AÑADIDA
    + "ORDER BY pd.fechaEntrega ASC, pd.menuPlato.plato.nombre ASC")
List<ProduccionDiaDTO> obtenerReporteDeProduccionRango(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);
}

package com.trabajopp1.backendpp1.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.trabajopp1.backendpp1.entity.MenuPlato;
import com.trabajopp1.backendpp1.entity.PedidoDia;


// 3. Repositorio para la tabla intermedia MenuPlato (Crucial para el Menú Diario)
@Repository
public interface MenuPlatoRepository extends JpaRepository<MenuPlato, Integer> {
    
    // Consulta para obtener los platos disponibles para una fecha específica (la página principal)
    // Busca MenuPlato uniendo con MenuDia por la fecha
    @Query("SELECT mp FROM MenuPlato mp JOIN mp.menuDia md " +
           "WHERE md.fecha = :fecha " +
           "AND md.publicado = TRUE " + // Filtro 1 (Publicado)
           "AND mp.stockDisponible > 0") //Filtro 2 (Stock)
    List<MenuPlato> findPlatosPublicadosYConStockPorFecha(@Param("fecha") LocalDate fecha);
    
    // Consulta para obtener la lista de platos que tiene un menú específico
    List<MenuPlato> findByMenuDia_Id(Integer idMenuDia);

    // Necesario para devolver el stock al cancelar un pedido
    Optional<MenuPlato> findByMenuDia_IdAndPlato_Id(Integer idMenuDia, Integer idPlato);

}

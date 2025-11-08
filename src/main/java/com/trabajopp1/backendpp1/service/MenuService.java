package com.trabajopp1.backendpp1.service;

import com.trabajopp1.backendpp1.dto.MenuDiaCreacionDTO;
import com.trabajopp1.backendpp1.dto.PlatoMenuDTO;
import com.trabajopp1.backendpp1.dto.PlatoStockDTO;
import com.trabajopp1.backendpp1.entity.*;
import com.trabajopp1.backendpp1.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuDiaRepository menuDiaRepository;
    private final MenuPlatoRepository menuPlatoRepository;
    private final PlatoRepository platoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoDiaRepository pedidoDiaRepository; // Necesario para borrar dependencias

    // --- 1. CREAR OFERTA DE MENÚ DIARIO ---
    @Transactional
    public MenuDia crearMenuDiario(MenuDiaCreacionDTO dto, Integer idUsuarioCreador) {
        
        Usuario creador = usuarioRepository.findById(idUsuarioCreador)
            .orElseThrow(() -> new EntityNotFoundException("Usuario creador no encontrado."));
        
        // Simulación de verificación de rol (debe ser admin/cocina)
        if (!creador.getEsUsuarioRestaurante()) {
            throw new RuntimeException("El usuario no tiene permiso para crear menús.");
        }
        
        MenuDia menuDia = new MenuDia();
        menuDia.setFecha(dto.getFecha());
        menuDia.setDescripcion(dto.getDescripcion());
        menuDia.setPublicado(dto.getPublicado());
        menuDia.setCreador(creador);
        
        MenuDia menuDiaGuardado = menuDiaRepository.save(menuDia);

        for (PlatoStockDTO item : dto.getItems()) {
            Plato plato = platoRepository.findById(item.getIdPlato())
                .orElseThrow(() -> new EntityNotFoundException("Plato no encontrado: " + item.getIdPlato()));

            MenuPlato menuPlato = new MenuPlato();
            menuPlato.setMenuDia(menuDiaGuardado);
            menuPlato.setPlato(plato);
            menuPlato.setStockDisponible(item.getStockDisponible());
            
            menuPlatoRepository.save(menuPlato);
        }
        
        return menuDiaGuardado;
    }
    
    // --- 2. OBTENER MENÚ PARA EL DÍA (Usado por Empleado) ---
    public List<PlatoMenuDTO> obtenerMenuParaDia(LocalDate fecha) {
    
    // 1.  Llama a la nueva consulta optimizada
    List<MenuPlato> menuPlatos = menuPlatoRepository.findPlatosPublicadosYConStockPorFecha(fecha);
    
    // 2.  ELIMINAMOS EL FILTRO .filter()
    return menuPlatos.stream()
        .map(mp -> {
            Plato plato = mp.getPlato();
            
            PlatoMenuDTO dto = new PlatoMenuDTO();
            dto.setIdMenuPlato(mp.getId());
            dto.setIdPlato(plato.getId());
            dto.setNombre(plato.getNombre());
            dto.setDescripcion(plato.getDescripcion());
            dto.setCategoria(plato.getCategoria());
            dto.setUrlImagen(plato.getImagen());
            dto.setStockDisponible(mp.getStockDisponible());
            dto.setFechaMenu(mp.getMenuDia().getFecha());
            return dto;
        })
        .collect(Collectors.toList());
}
    
    // --- 3. ACTUALIZAR OFERTA DE MENÚ DIARIO ---
    @Transactional
    public MenuDia actualizarMenuDiario(Integer idMenuDia, MenuDiaCreacionDTO dto, Integer idUsuarioModificador) {
        
        MenuDia menuExistente = menuDiaRepository.findById(idMenuDia)
            .orElseThrow(() -> new EntityNotFoundException("Menú del día no encontrado con ID: " + idMenuDia));

        // Actualizar campos de MenuDia
        menuExistente.setFecha(dto.getFecha());
        menuExistente.setDescripcion(dto.getDescripcion());
        menuExistente.setPublicado(dto.getPublicado());
        menuDiaRepository.save(menuExistente);

        // ⚠️ Nota: En una actualización, si ya hay pedidos en los platos a eliminar, esto puede fallar.
        // Asumimos que no habrá pedidos activos en la ventana de actualización.
        List<MenuPlato> platosAntiguos = menuPlatoRepository.findByMenuDia_Id(idMenuDia);
        menuPlatoRepository.deleteAll(platosAntiguos);

        for (PlatoStockDTO item : dto.getItems()) {
            Plato plato = platoRepository.findById(item.getIdPlato())
                .orElseThrow(() -> new EntityNotFoundException("Plato no encontrado: " + item.getIdPlato()));

            MenuPlato menuPlato = new MenuPlato();
            menuPlato.setMenuDia(menuExistente);
            menuPlato.setPlato(plato);
            menuPlato.setStockDisponible(item.getStockDisponible());
            menuPlatoRepository.save(menuPlato);
        }
        
        return menuExistente;
    }
    
    // --- 4. ELIMINAR OFERTA DE MENÚ DIARIO COMPLETO ---
    // 🚨 Este método está diseñado para eliminar TODO un menú (MenuDia)
    @Transactional
    public void eliminarMenuDiario(Integer idMenuDia) {
        if (!menuDiaRepository.existsById(idMenuDia)) {
            throw new EntityNotFoundException("Menú del día no encontrado con ID: " + idMenuDia);
        }
        
        // 1. Encontrar los ítems de MenuPlato (Stock) de este menú
        List<MenuPlato> platosAsociados = menuPlatoRepository.findByMenuDia_Id(idMenuDia);

        // 2. BORRAR PEDIDOS DEPENDIENTES (PedidoDia)
        for (MenuPlato mp : platosAsociados) {
            // Requiere findByMenuPlato_Id(Integer id) en PedidoDiaRepository
            List<PedidoDia> pedidosDia = pedidoDiaRepository.findByMenuPlato_Id(mp.getId());
            if (!pedidosDia.isEmpty()) {
                pedidoDiaRepository.deleteAll(pedidosDia);
            }
        }
        
        // 3. Borrar los ítems de stock (MenuPlato)
        menuPlatoRepository.deleteAll(platosAsociados);
        
        // 4. Borrar el MenuDia principal
        menuDiaRepository.deleteById(idMenuDia);
    }
    
    // --- 5. 🆕 ELIMINAR UN SOLO ÍTEM DE STOCK (MenuPlato) ---
    // 🚨 ESTE ES EL NUEVO MÉTODO QUE NECESITAS PARA ELIMINAR UNA TARJETA
    @Transactional
    public void eliminarMenuPlatoItem(Integer idMenuPlato) {
        
        MenuPlato menuPlato = menuPlatoRepository.findById(idMenuPlato)
            .orElseThrow(() -> new EntityNotFoundException("Ítem de oferta (MenuPlato) no encontrado con ID: " + idMenuPlato));
        
        // 1. Encontrar y eliminar PedidosDia dependientes (la cascada)
        List<PedidoDia> pedidosDia = pedidoDiaRepository.findByMenuPlato_Id(idMenuPlato);
        if (!pedidosDia.isEmpty()) {
            pedidoDiaRepository.deleteAll(pedidosDia);
        }
        
        // 2. Eliminar el ítem de stock
        menuPlatoRepository.delete(menuPlato);
    }
}
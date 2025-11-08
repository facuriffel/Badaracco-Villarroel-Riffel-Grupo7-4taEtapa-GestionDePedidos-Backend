package com.trabajopp1.backendpp1.controller;

import com.trabajopp1.backendpp1.dto.MenuDiaCreacionDTO;
import com.trabajopp1.backendpp1.dto.PlatoMenuDTO;
import com.trabajopp1.backendpp1.dto.PedidoResumenCocinaDTO;
import com.trabajopp1.backendpp1.entity.MenuDia;
import com.trabajopp1.backendpp1.service.MenuService;
import com.trabajopp1.backendpp1.service.PedidoService;
import com.trabajopp1.backendpp1.service.UsuarioService; // 🚨 NUEVA INYECCIÓN

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor // Se actualizará automáticamente para incluir UsuarioService
public class MenuController {
    
    private final MenuService menuService;
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService; //INYECTADO PARA OBTENER EL ID


    // --- MÉTODO AUXILIAR PARA OBTENER EL ID DEL TOKEN ---
    private Integer getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("Usuario no autenticado o sesión expirada.");
        }

        // El principal es el correo electrónico (username) del usuario autenticado
        String userEmail = authentication.getName();
        
        // Obtenemos el ID numérico de la base de datos
        return usuarioService.getIdByCorreo(userEmail);
    }

    // --- LECTURA (Ruta para Empleados y Cocina) ---
    // GET /api/menu?fecha=YYYY-MM-DD (PÚBLICA - Solo requiere token)
    @GetMapping
    public ResponseEntity<List<PlatoMenuDTO>> obtenerMenuPorDia(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) 
    {
        List<PlatoMenuDTO> menu = menuService.obtenerMenuParaDia(fecha);
        
        if (menu.isEmpty()) {
            return ResponseEntity.noContent().build(); 
        }
        
        return ResponseEntity.ok(menu);
    }

    // --- GESTIÓN (Rutas protegidas para Rol Cocina/Admin) ---
    
    // POST /api/menu/admin (Crear Oferta Diaria)
    @PostMapping("/admin")
    public ResponseEntity<MenuDia> crearMenuDiario(@Valid @RequestBody MenuDiaCreacionDTO dto) {
        Integer userId = getAuthenticatedUserId(); // ⬅️ ID REAL
        MenuDia menuCreado = menuService.crearMenuDiario(dto, userId);
        return new ResponseEntity<>(menuCreado, HttpStatus.CREATED);
    }

    // PUT /api/menu/admin/{id} (Actualizar Oferta Diaria - Usa ID de MenuDia)
    @PutMapping("/admin/{id}")
    public ResponseEntity<MenuDia> actualizarMenuDiario(
        @PathVariable Integer id,
        @Valid @RequestBody MenuDiaCreacionDTO dto)
    {
        Integer userId = getAuthenticatedUserId(); // ID REAL
        MenuDia menuActualizado = menuService.actualizarMenuDiario(id, dto, userId);
        return ResponseEntity.ok(menuActualizado);
    }
    
    // DELETE /api/menu/admin/{id} (Eliminar Oferta Diaria COMPLETA - Usa ID de MenuDia)
    // No requiere el ID del usuario, solo el rol (protegido por SecurityConfig)
    @DeleteMapping("/admin/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarMenuDiario(@PathVariable Integer id) {
        menuService.eliminarMenuDiario(id);
    }
    
    // DELETE /api/menu/admin/item/{idMenuPlato} (Eliminar UN SOLO ÍTEM - Usa ID de MenuPlato)
    // No requiere el ID del usuario, solo el rol (protegido por SecurityConfig)
    @DeleteMapping("/admin/item/{idMenuPlato}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarItemMenuPlato(@PathVariable Integer idMenuPlato) {
        menuService.eliminarMenuPlatoItem(idMenuPlato);
    }

    // ENDPOINT DE RESUMEN (Para Rol Cocina/Admin)
    // GET /api/menu/admin/resumen?fecha=YYYY-MM-DD
    // Protegido por hasRole("ADMIN") en SecurityConfig
    @GetMapping("/admin/resumen")
    public ResponseEntity<List<PedidoResumenCocinaDTO>> obtenerResumenPedidos(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) 
    {
        List<PedidoResumenCocinaDTO> resumen = pedidoService.obtenerResumenPedidosParaCocina(fecha);
        
        if (resumen.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(resumen);
    }
}
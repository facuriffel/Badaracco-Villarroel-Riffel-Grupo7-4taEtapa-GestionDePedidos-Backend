package com.trabajopp1.backendpp1.controller;

import com.trabajopp1.backendpp1.dto.UsuarioPerfilDTO;
import com.trabajopp1.backendpp1.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/perfil")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // --- MÉTODO AUXILIAR PARA OBTENER EL ID DEL TOKEN (Compartido por todos los Controllers) ---
    // (Asume que ya tenemos el método getIdByCorreo(String) en UsuarioService)
    private Integer getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // 1. Verificar si el usuario está realmente autenticado (no es anónimo)
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("Usuario no autenticado o sesión expirada.");
        }

        // 2. El principal es el correo electrónico (String) o el objeto UserDetails
        String userEmail = authentication.getName();
        
        // 3. Obtener el ID numérico del usuario de la DB
        return usuarioService.getIdByCorreo(userEmail);
    }

    // GET /api/perfil/me
    @GetMapping("/me")
    public ResponseEntity<UsuarioPerfilDTO> obtenerPerfil() {
        Integer userId = getAuthenticatedUserId(); //  Obtenemos el ID del token
        try {
            UsuarioPerfilDTO perfil = usuarioService.obtenerPerfilUsuario(userId);
            return ResponseEntity.ok(perfil);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }


    // PUT /api/perfil/me
    @PutMapping("/me")
    public ResponseEntity<UsuarioPerfilDTO> actualizarPerfil(@RequestBody UsuarioPerfilDTO dto) {
        Integer userId = getAuthenticatedUserId(); // Obtenemos el ID del token
        try {
            UsuarioPerfilDTO perfilActualizado = usuarioService.actualizarPerfil(userId, dto);
            return ResponseEntity.ok(perfilActualizado);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
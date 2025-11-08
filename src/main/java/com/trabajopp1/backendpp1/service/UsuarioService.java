package com.trabajopp1.backendpp1.service;

import com.trabajopp1.backendpp1.dto.UsuarioPerfilDTO;
import com.trabajopp1.backendpp1.entity.Usuario;
import com.trabajopp1.backendpp1.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioPerfilDTO obtenerPerfilUsuario(Integer idUsuario) {
        // 1. Buscar la entidad
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + idUsuario));

        // 2. Mapear a DTO
        UsuarioPerfilDTO dto = new UsuarioPerfilDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setCorreo(usuario.getCorreo());
        dto.setTelefono(usuario.getTelefono());
        dto.setDireccion(usuario.getDireccion());
        dto.setEsUsuarioRestaurante(usuario.getEsUsuarioRestaurante());

        return dto;
    }

    @Transactional
    public UsuarioPerfilDTO actualizarPerfil(Integer idUsuario, UsuarioPerfilDTO dto) {
        // 1. Buscar la entidad existente
        Usuario usuarioExistente = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + idUsuario));

        // 2. Aplicar solo los campos modificables
        usuarioExistente.setNombre(dto.getNombre());
        usuarioExistente.setApellido(dto.getApellido());
        usuarioExistente.setCorreo(dto.getCorreo());
        usuarioExistente.setTelefono(dto.getTelefono());
        usuarioExistente.setDireccion(dto.getDireccion());

        // El rol (esUsuarioRestaurante) NO debe actualizarse desde el perfil de
        // usuario.

        // 3. Guardar en DB
        Usuario usuarioGuardado = usuarioRepository.save(usuarioExistente);

        // 4. Retornar el DTO mapeado (se puede reutilizar el DTO de entrada)
        return obtenerPerfilUsuario(usuarioGuardado.getId());
    }

    public Integer getIdByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con correo: " + correo))
                .getId();
    }

}

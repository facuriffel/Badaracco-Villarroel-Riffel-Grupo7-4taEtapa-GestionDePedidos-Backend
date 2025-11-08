// AuthService.java (Paquete service)

package com.trabajopp1.backendpp1.service;

import com.trabajopp1.backendpp1.dto.AuthRequestDTO;
import com.trabajopp1.backendpp1.dto.AuthResponseDTO;
import com.trabajopp1.backendpp1.entity.Usuario;
import com.trabajopp1.backendpp1.repository.UsuarioRepository;
import com.trabajopp1.backendpp1.security.JwtTokenService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService; // A crear después
    private static final String CODIGO_ADMIN_SECRETO = "COCINA2025";

    // 1. Registro de Usuario (Por defecto, Empleado)
    @Transactional
    public Usuario registrar(Usuario usuario, String codigoSecreto) { //  Ahora recibe el código
        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está en uso.");
        }

        // Cifrar la contraseña
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

        // LÓGICA DE ASIGNACIÓN DE ROL:
        boolean esAdmin = CODIGO_ADMIN_SECRETO.equals(codigoSecreto);

        usuario.setEsUsuarioRestaurante(esAdmin); // Asigna TRUE si el código coincide
        usuario.setActivo(true);

        return usuarioRepository.save(usuario);
    }

    // 2. Autenticación y Generación de Token (Login)
    public AuthResponseDTO login(AuthRequestDTO request) {

        // Ya que el Controller usó el AuthenticationManager para validar,
        // aquí solo necesitamos obtener el usuario para generar el token.

        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Error: Usuario no existe después de la validación."));

        // Generar JWT
        String token = jwtTokenService.generarToken(usuario);
        String rol = usuario.getEsUsuarioRestaurante() ? "ADMIN" : "EMPLEADO";

        return new AuthResponseDTO(token, rol);
    }
}
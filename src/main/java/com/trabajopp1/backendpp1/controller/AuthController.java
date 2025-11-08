package com.trabajopp1.backendpp1.controller;

import com.trabajopp1.backendpp1.dto.AuthRequestDTO;
import com.trabajopp1.backendpp1.dto.AuthResponseDTO;
import com.trabajopp1.backendpp1.dto.UsuarioRegistroDTO;
import com.trabajopp1.backendpp1.entity.Usuario;
import com.trabajopp1.backendpp1.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException; // 🚨 IMPORTAR ESTO
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth") 
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager; 

    @PostMapping("/register")
    public ResponseEntity<Usuario> registrarUsuario(@Valid @RequestBody UsuarioRegistroDTO dto) {
        
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(dto.getNombre());
        nuevoUsuario.setApellido(dto.getApellido());
        nuevoUsuario.setCorreo(dto.getCorreo());
        nuevoUsuario.setContrasena(dto.getContrasena());
        nuevoUsuario.setTelefono(dto.getTelefono());
        nuevoUsuario.setDireccion(dto.getDireccion());

        // Llamamos al servicio pasando la entidad y el código secreto
        Usuario usuarioGuardado = authService.registrar(nuevoUsuario, dto.getCodigoSecreto());
        
        usuarioGuardado.setContrasena(null); 
        
        return new ResponseEntity<>(usuarioGuardado, HttpStatus.CREATED);
    }

    // 2. POST /api/auth/login
    @PostMapping("/login")
    @CrossOrigin(origins = "*")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        
        try {
            // PASO 1: Llama al AuthenticationManager. Si las credenciales son inválidas,
            // lanza AuthenticationException, que es capturada abajo.
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getCorreo(),
                    request.getContrasena()
                )
            );
            
            // PASO 2: Si la autenticación es exitosa, llama al AuthService para generar el token.
            AuthResponseDTO response = authService.login(request);
            
            return ResponseEntity.ok(response);
            
        } catch (AuthenticationException e) {
            // MANEJO DEL FALLO DE AUTENTICACIÓN
            // Esto devolverá 401 Unauthorized cuando las credenciales no coincidan.
            // Esto elimina el confuso 403 Forbidden.
            return new ResponseEntity(
                "Credenciales inválidas. Por favor, verifica tu correo y contraseña.",
                HttpStatus.UNAUTHORIZED
            );
        }
    }
}
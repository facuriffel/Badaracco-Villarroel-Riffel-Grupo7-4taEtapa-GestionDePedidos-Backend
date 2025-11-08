// UserDetailsServiceImpl.java (Implementa la interfaz de Spring Security)

package com.trabajopp1.backendpp1.service;

import com.trabajopp1.backendpp1.entity.Usuario;
import com.trabajopp1.backendpp1.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + username));
        
        // Convertir el rol booleano a una autoridad (ROLE_ADMIN o ROLE_EMPLEADO)
        Collection<? extends GrantedAuthority> authorities = getAuthorities(usuario);

        // Retornar un objeto UserDetails de Spring Security
        return new User(
                usuario.getCorreo(), 
                usuario.getContrasena(),
                authorities
        );
    }
    
    // Método auxiliar para mapear el rol booleano a una autoridad
    private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        String rol = usuario.getEsUsuarioRestaurante() ? "ROLE_ADMIN" : "ROLE_EMPLEADO";
        return List.of(new SimpleGrantedAuthority(rol));
    }
}
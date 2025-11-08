package com.trabajopp1.backendpp1.config;

import com.trabajopp1.backendpp1.security.JwtAuthenticationFilter;
import com.trabajopp1.backendpp1.service.UserDetailsServiceImpl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity // Permite usar @PreAuthorize en los Services/Controllers
public class SecurityConfig {

    // Inyección de dependencias necesarias para los filtros
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    // 1. Define el Bean para cifrar/descifrar contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. CADENA DE FILTROS DE SEGURIDAD: Define qué rutas proteger y cómo usar JWT
    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> {}) 
        .csrf(AbstractHttpConfigurer::disable)
        
        .authorizeHttpRequests(auth -> auth
            
            // --- 1. RUTAS PÚBLICAS (Login y Menú) ---
            .requestMatchers("/api/auth/**").permitAll() 
            .requestMatchers(HttpMethod.GET, "/api/menu").permitAll() 

            // Permite que CUALQUIER rol autenticado acceda a su perfil
            .requestMatchers("/api/perfil/**").authenticated()

            // --- 2.  RUTAS DE ADMIN (Las más específicas van PRIMERO) ---
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/menu/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/pedidos/admin/**").hasRole("ADMIN") //  ESTA REGLA VA PRIMERO

            // --- 3. RUTAS DE EMPLEADO (La regla más general de /pedidos va DESPUÉS) ---
            .requestMatchers("/api/pedidos/**").hasRole("EMPLEADO") 
            

            // --- 4. CUALQUIER OTRA RUTA ---
            .anyRequest().authenticated()
        )
        
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        
        // Manejo de errores 401/403
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((req, res, authEx) -> {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
                res.setContentType("application/json");
                res.getWriter().write("{\"error\":\"Token invalido o expirado\"}");
            })
            .accessDeniedHandler((req, res, accessDeniedEx) -> {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN); 
                res.setContentType("application/json");
                res.getWriter().write("{\"error\":\"Acceso denegado. No tienes permisos.\"}");
            })
        );

    return http.build();
}

    // 7. Proveedor de Autenticación (Conecta el UserDetailsService y
    // PasswordEncoder)
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // 8. Authentication Manager (Necesario para el proceso de Login en AuthService)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
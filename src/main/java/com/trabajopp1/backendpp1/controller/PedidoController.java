package com.trabajopp1.backendpp1.controller;

import com.trabajopp1.backendpp1.dto.PedidoDiaCreacionDTO;
import com.trabajopp1.backendpp1.dto.PedidoDiaDetalleDTO;
import com.trabajopp1.backendpp1.dto.PedidoEstadoDTO;
import com.trabajopp1.backendpp1.dto.ProduccionDiaDTO;
import com.trabajopp1.backendpp1.entity.Pedido;
import com.trabajopp1.backendpp1.service.PedidoService;
import com.trabajopp1.backendpp1.service.UsuarioService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor // Se actualizará automáticamente para incluir UsuarioService
public class PedidoController {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService; // INYECTADO PARA OBTENER EL ID

    // --- MÉTODO AUXILIAR PARA OBTENER EL ID DEL TOKEN ---
    private Integer getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("Usuario no autenticado o sesión expirada.");
        }

        // El principal es el correo electrónico (username) del usuario autenticado
        String userEmail = authentication.getName();

        // Obtenemos el ID numérico de la base de datos
        return usuarioService.getIdByCorreo(userEmail);
    }

    // POST /api/pedidos (Añadir plato al pedido)
    @PostMapping
    public ResponseEntity<Void> agregarPlato(@Valid @RequestBody PedidoDiaCreacionDTO dto) {
        Integer userId = getAuthenticatedUserId(); // ID REAL
        pedidoService.agregarPlatoAPedido(dto, userId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // GET /api/pedidos (Lista de pedidos del empleado)
    @GetMapping
    public ResponseEntity<List<PedidoDiaDetalleDTO>> listarPedidos() {
        Integer userId = getAuthenticatedUserId(); // ID REAL
        List<PedidoDiaDetalleDTO> pedidos = pedidoService.listarPedidosPorUsuario(userId);
        return ResponseEntity.ok(pedidos);
    }

    // DELETE /api/pedidos/{idPedidoDia} (Eliminar plato del pedido)
    @DeleteMapping("/{idPedidoDia}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarPlatoDePedido(@PathVariable Integer idPedidoDia) {
    Integer userId = getAuthenticatedUserId(); // ID REAL del token
    pedidoService.eliminarPlatoDePedido(idPedidoDia, userId);
}

    // PUT /api/pedidos/confirmar
    @PutMapping("/confirmar")
    public ResponseEntity<Void> confirmarPedido() {
        Integer userId = getAuthenticatedUserId(); // ID REAL
        pedidoService.confirmarPedido(userId);
        return ResponseEntity.ok().build();
    }

    // GET /api/pedidos/estado
    @GetMapping("/estado")
    public ResponseEntity<PedidoEstadoDTO> obtenerEstadoPedido() {

        // Ahora llama a getAuthenticatedUserId()
        Integer userId = getAuthenticatedUserId();

        Pedido pedido = pedidoService.obtenerPedidoPrincipalPorUsuario(userId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido principal no encontrado."));

        PedidoEstadoDTO dto = new PedidoEstadoDTO();
        dto.setId(pedido.getId());
        dto.setEstado(pedido.getEstado().name());

        return ResponseEntity.ok(dto);
    }

    // ENDPOINT: GET /api/pedidos/admin/reporte-semanal
    // Protegido por hasRole("ADMIN") en SecurityConfig
    @GetMapping("/admin/reporte-semanal")
    public ResponseEntity<List<ProduccionDiaDTO>> obtenerReporteSemanal() {
        List<ProduccionDiaDTO> reporte = pedidoService.generarReporteDeProduccionSemanal();

        if (reporte.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reporte);
    }

}
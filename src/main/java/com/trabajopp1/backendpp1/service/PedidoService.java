package com.trabajopp1.backendpp1.service;

import com.trabajopp1.backendpp1.dto.PedidoDiaCreacionDTO;
import com.trabajopp1.backendpp1.dto.PedidoDiaDetalleDTO;
import com.trabajopp1.backendpp1.dto.PedidoResumenCocinaDTO;
import com.trabajopp1.backendpp1.dto.ProduccionDiaDTO;
import com.trabajopp1.backendpp1.entity.*;
import com.trabajopp1.backendpp1.exception.PedidoExistenteException;
import com.trabajopp1.backendpp1.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoDiaRepository pedidoDiaRepository;
    private final MenuPlatoRepository menuPlatoRepository;
    private final UsuarioRepository usuarioRepository;

    // --- Lógica del Pedido Principal (Simplificada) ---
    private Pedido obtenerOcrearPedidoPrincipal(Integer idUsuario) {

        Optional<Pedido> pedidoActivo = pedidoRepository.findByUsuario_Id(idUsuario);

        if (pedidoActivo.isPresent()) {
            return pedidoActivo.get();
        } else {
            Usuario usuario = usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado."));

            Pedido nuevoPedido = new Pedido();
            nuevoPedido.setUsuario(usuario);
            nuevoPedido.setFechaPedido(LocalDateTime.now());
            nuevoPedido.setEstado(Pedido.EstadoPedido.PENDIENTE); // Estado inicial
            nuevoPedido.setCantidadPersonas(1);
            return pedidoRepository.save(nuevoPedido);
        }
    }

    // ------------------------------------------------------------------
    // 1. AÑADIR PLATO AL PEDIDO (CREATE) - Flujo Empleado
    // ------------------------------------------------------------------
    @Transactional
    public void agregarPlatoAPedido(PedidoDiaCreacionDTO dto, Integer idUsuario) {
        Pedido pedidoPrincipal = obtenerOcrearPedidoPrincipal(idUsuario);

        boolean yaTienePedido = pedidoDiaRepository.findByUsuarioAndFechaEntrega(idUsuario, dto.getFechaEntrega())
                .isPresent();
        if (yaTienePedido) {
    throw new ResponseStatusException(
        HttpStatus.CONFLICT, 
        "No se puede seleccionar más de un plato para el mismo día."
    );
}

        MenuPlato menuPlato = menuPlatoRepository.findById(dto.getIdMenuPlato())
                .orElseThrow(() -> new EntityNotFoundException("La oferta de plato/stock no existe."));

        if (menuPlato.getStockDisponible() <= 0) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Plato agotado para esta fecha.");
        }

        PedidoDia pedidoDia = new PedidoDia();
        pedidoDia.setPedido(pedidoPrincipal);
        pedidoDia.setMenuPlato(menuPlato);
        pedidoDia.setFechaEntrega(dto.getFechaEntrega());
        pedidoDiaRepository.save(pedidoDia);

        menuPlato.setStockDisponible(menuPlato.getStockDisponible() - 1);
        menuPlatoRepository.save(menuPlato);
    }

    // ------------------------------------------------------------------
    // 2. ELIMINAR PLATO DEL PEDIDO (DELETE) - Flujo Empleado
    // ------------------------------------------------------------------
    @Transactional
public void eliminarPlatoDePedido(Integer idPedidoDia, Integer idUsuario) {
    PedidoDia pedidoDia = pedidoDiaRepository.findById(idPedidoDia)
            .orElseThrow(() -> new EntityNotFoundException("Pedido de día no encontrado."));

    // Verificación de propiedad (seguridad)
    if (!pedidoDia.getPedido().getUsuario().getId().equals(idUsuario)) {
        throw new AccessDeniedException("No tienes permiso para eliminar este pedido.");
    }

    MenuPlato menuPlato = pedidoDia.getMenuPlato();
    menuPlato.setStockDisponible(menuPlato.getStockDisponible() + 1);
    menuPlatoRepository.save(menuPlato);

    pedidoDiaRepository.delete(pedidoDia);
}

    // ------------------------------------------------------------------
    // 3. LISTAR PEDIDOS DEL EMPLEADO (READ) - Flujo Empleado
    // ------------------------------------------------------------------
    public List<PedidoDiaDetalleDTO> listarPedidosPorUsuario(Integer idUsuario) {

        List<PedidoDia> pedidos = pedidoDiaRepository.findByPedidoUsuarioIdOrderByFechaEntregaAsc(idUsuario);

        return pedidos.stream()
                .map(pd -> {
                    // Camino CORREGIDO: PedidoDia -> MenuPlato -> Plato
                    Plato plato = pd.getMenuPlato().getPlato();

                    PedidoDiaDetalleDTO dto = new PedidoDiaDetalleDTO();
                    dto.setIdPedidoDia(pd.getId());
                    dto.setNombrePlato(plato.getNombre());
                    dto.setCategoriaPlato(plato.getCategoria());
                    dto.setFechaEntrega(pd.getFechaEntrega());
                    dto.setUrlImagenPlato(plato.getImagen());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // 4. RESUMEN DE PEDIDOS PARA LA COCINA (READ) - Flujo Cocina
    // ------------------------------------------------------------------
    public List<PedidoResumenCocinaDTO> obtenerResumenPedidosParaCocina(LocalDate fecha) {

        // Llama al método del repositorio que ejecuta la consulta JPQL con GROUP BY
        return pedidoDiaRepository.obtenerResumenPedidosPorFecha(fecha);
    }

    // ------------------------------------------------------------------
    // 5. CONFIRMAR / MODIFICAR PEDIDO (Usado por Empleado)
    // ------------------------------------------------------------------
    /**
     * Confirma el Pedido principal o re-confirma después de una modificación.
     * Esta acción debe ser idempotente.
     */
    @Transactional
    public Pedido confirmarPedido(Integer idUsuario) {

        // 1. Obtener el Pedido principal del usuario
        Pedido pedidoPrincipal = pedidoRepository.findByUsuario_Id(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró un pedido activo para el usuario."));

        // 2. Cambiar el estado SÓLO si está PENDIENTE. Si ya está CONFIRMADO, no hace
        // nada,
        // simplemente devuelve el objeto para permitir la modificación continua.
        if (pedidoPrincipal.getEstado() == Pedido.EstadoPedido.PENDIENTE) {
            pedidoPrincipal.setEstado(Pedido.EstadoPedido.CONFIRMADO);
            return pedidoRepository.save(pedidoPrincipal);
        }

        // Si ya está CONFIRMADO, se devuelve sin lanzar error.
        return pedidoPrincipal;
    }

    /**
     * Obtiene el Pedido principal del usuario para verificar su estado.
     */
    public Optional<Pedido> obtenerPedidoPrincipalPorUsuario(Integer idUsuario) {
        return pedidoRepository.findByUsuario_Id(idUsuario);
    }

    // Define el rango de fechas para el reporte semanal
    public List<ProduccionDiaDTO> generarReporteDeProduccionSemanal() {
        LocalDate hoy = LocalDate.now();
        // Encuentra el próximo lunes (similar a la lógica de JS)
        int daysUntilNextMonday = hoy.getDayOfWeek().getValue() == 1 ? 7 : 8 - hoy.getDayOfWeek().getValue();
        LocalDate inicioSemana = hoy.plusDays(daysUntilNextMonday);
        LocalDate finSemana = inicioSemana.plusDays(4); // El viernes (Lunes + 4 días)

        // Llamamos al repositorio con el rango de la próxima semana
        return pedidoRepository.obtenerReporteDeProduccionRango(inicioSemana, finSemana);
    }

}
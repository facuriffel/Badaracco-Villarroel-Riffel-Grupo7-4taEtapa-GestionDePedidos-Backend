package com.trabajopp1.backendpp1.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // Esto hace que Spring devuelva 409 automáticamente
public class PedidoExistenteException extends RuntimeException {
    public PedidoExistenteException(String mensaje) {
        super(mensaje);
    }
}
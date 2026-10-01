package com.delamo.library.domain.exception;

public class ReservaNoPermitidaException extends ExcepcionDominio {
    public ReservaNoPermitidaException(String motivo) {
        super("Reserva no permitida: " + motivo);
    }
}
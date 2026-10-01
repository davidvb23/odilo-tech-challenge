package com.delamo.library.domain.exception;

public class BloqueadoPorMultasException extends ExcepcionDominio {
    public BloqueadoPorMultasException(String socioId, Double pendiente) {
        super("El socio " + socioId + " está bloqueado: tiene " + pendiente + " € pendientes");
    }
}
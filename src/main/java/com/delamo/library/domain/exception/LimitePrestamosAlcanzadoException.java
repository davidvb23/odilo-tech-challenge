package com.delamo.library.domain.exception;

public class LimitePrestamosAlcanzadoException extends ExcepcionDominio {
    public LimitePrestamosAlcanzadoException(String socioId, int limite) {
        super("El socio " + socioId + " ya tiene " + limite + " préstamos activos (límite de su nivel)");
    }
}

package com.delamo.library.domain.exception;

public class SinEjemplarDisponibleException extends ExcepcionDominio {
    public SinEjemplarDisponibleException(String tituloId) {
        super("No hay ejemplares disponibles del título " + tituloId );
    }
}

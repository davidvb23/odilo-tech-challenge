package com.delamo.library.domain.policy;

import java.time.Duration;

public record PoliticaBiblioteca(Double multaPorDia, Double umbralBloqueo, Duration ventanaRecogida) {
    public  PoliticaBiblioteca {

            if (multaPorDia == null || multaPorDia < 0) {
                throw new IllegalArgumentException("La multa por dia no puede ser nula ni negativa");
            }
            if (umbralBloqueo == null || umbralBloqueo < 0) {
                throw new IllegalArgumentException("El umbral de bloqueo no puede ser nulo ni negativo");
            }
            if (ventanaRecogida == null || ventanaRecogida.isNegative() || ventanaRecogida.isZero()) {
                throw new IllegalArgumentException("La ventana de recogida debe ser positiva");
            }

    }
}

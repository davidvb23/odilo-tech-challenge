package com.delamo.library.domain.policy;

public record PoliticaNivel(int diasPrestamo, int maxPrestamosSimultaneos, Integer maxRenovaciones) {
    public PoliticaNivel {
        if (diasPrestamo <= 0) {
            throw new IllegalArgumentException("Los dias de prestamo n ser > 0");
        }
        if (maxPrestamosSimultaneos <= 0) {
            throw new IllegalArgumentException("El maximo de prestamos simultaneos deben ser > 0");
        }
        if (maxRenovaciones != null && maxRenovaciones < 0) {
            throw new IllegalArgumentException("Las renovaciones no pueden ser negativas");
        }
    }

    public boolean permiteRenovar(int renovacionesHechas) {
        return maxRenovaciones == null || renovacionesHechas < maxRenovaciones;
    }
}

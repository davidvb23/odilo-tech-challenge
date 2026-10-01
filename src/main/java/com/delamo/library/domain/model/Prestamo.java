package com.delamo.library.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Prestamo {

    private final String id;
    private final String ejemplarId;
    private final String tituloId;
    private final String socioId;
    private final Instant prestado;
    private LocalDate fechaVencimiento;
    private int renovacionesHechas;
    private Instant devuelto;

    public Prestamo(String id, String ejemplarId, String tituloId, String socioId,
                    Instant prestado, LocalDate fechaVencimiento) {
        this.id = id;
        this.ejemplarId = ejemplarId;
        this.tituloId = tituloId;
        this.socioId = socioId;
        this.prestado = prestado;
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getId() {
        return id;
    }
    public String getEjemplarId() {
        return ejemplarId;
    }
    public String getTituloId() {
        return tituloId;
    }
    public String getSocioId() {
        return socioId;
    }
    public Instant getPrestado() {
        return prestado;
    }
    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }
    public int getRenovacionesHechas() {
        return renovacionesHechas;
    }
    public Instant getDevuelto() {
        return devuelto;
    }

    public boolean estaActivo() {
        return devuelto == null;
    }


    public long diasDeRetraso(LocalDate hoy) {
        return Math.max(0, ChronoUnit.DAYS.between(fechaVencimiento, hoy));
    }


    public boolean estaVencido(LocalDate hoy) {
        return estaActivo() && hoy.isAfter(fechaVencimiento);
    }

    public void renovar(int diasExtra) {
        if (!estaActivo()) {
            throw new IllegalStateException("El préstamo " + id + " ya está cerrado");
        }
        fechaVencimiento = fechaVencimiento.plusDays(diasExtra);
        renovacionesHechas++;
    }

    public void cerrar(Instant cuando) {
        if (!estaActivo()) {
            throw new IllegalStateException("El préstamo " + id + " ya está cerrado");
        }
        devuelto = cuando;
    }
}

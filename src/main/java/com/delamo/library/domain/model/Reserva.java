package com.delamo.library.domain.model;

import java.time.Instant;

public class Reserva {

    private final String id;
    private final String tituloId;
    private final String socioId;
    private final Instant creada;
    private EstadoReserva estado = EstadoReserva.EN_ESPERA;
    private String ejemplarAsignadoId;
    private Instant caduca;

    public Reserva(String id, String tituloId, String socioId, Instant creada) {
        this.id = id;
        this.tituloId = tituloId;
        this.socioId = socioId;
        this.creada = creada;
    }

    public String getId() {
        return id;
    }
    public String getTituloId() {
        return tituloId;
    }
    public String getSocioId() {
        return socioId;
    }
    public Instant getCreada() {
        return creada;
    }
    public EstadoReserva getEstado() {
        return estado;
    }
    public String getEjemplarAsignadoId() {
        return ejemplarAsignadoId;
    }
    public Instant getCaduca() {
        return caduca;
    }

    public boolean estaEnEspera() {
        return estado == EstadoReserva.EN_ESPERA;
    }
    public boolean estaLista() {
        return estado == EstadoReserva.LISTA;
    }

    public boolean haCaducado(Instant ahora) {
        return estado == EstadoReserva.LISTA && ahora.isAfter(caduca);
    }

    public void marcarLista(String ejemplarId, Instant caducaEn) {
        exigirEstado(EstadoReserva.EN_ESPERA);
        this.estado = EstadoReserva.LISTA;
        this.ejemplarAsignadoId = ejemplarId;
        this.caduca = caducaEn;
    }

    public void recoger() {
        exigirEstado(EstadoReserva.LISTA);
        estado = EstadoReserva.RECOGIDA;
    }

    public void caducar() {
        exigirEstado(EstadoReserva.LISTA);
        estado = EstadoReserva.CADUCADA;
    }

    public void cancelar() {
        if (estado != EstadoReserva.EN_ESPERA && estado != EstadoReserva.LISTA) {
            throw new IllegalStateException(
                    "La reserva " + id + " está " + estado + " y no se puede cancelar");
        }
        estado = EstadoReserva.CANCELADA;
    }

    private void exigirEstado(EstadoReserva esperado) {
        if (estado != esperado) {
            throw new IllegalStateException(
                    "La reserva " + id + " está " + estado + ", se esperaba " + esperado);
        }
    }
}

package com.delamo.library.application;
import com.delamo.library.domain.exception.ExcepcionDominio;
import com.delamo.library.domain.model.Ejemplar;
import com.delamo.library.domain.model.EstadoEjemplar;
import com.delamo.library.domain.model.Reserva;
import com.delamo.library.domain.policy.ProveedorPoliticas;
import com.delamo.library.domain.port.RepositorioEjemplares;
import com.delamo.library.domain.port.RepositorioReservas;

import java.time.Instant;
import java.util.Optional;

public class GestorReservas {

    private final RepositorioReservas reservas;
    private final RepositorioEjemplares ejemplares;
    private final ProveedorPoliticas politicas;

    public GestorReservas(RepositorioReservas reservas, RepositorioEjemplares ejemplares,
                          ProveedorPoliticas politicas) {
        this.reservas = reservas;
        this.ejemplares = ejemplares;
        this.politicas = politicas;
    }

    public void caducarVencidas(String tituloId, Instant ahora) {
        for (Reserva reserva : reservas.delTitulo(tituloId)) {
            if (reserva.haCaducado(ahora)) {
                reserva.caducar();
                reservas.guardar(reserva);
                Ejemplar ejemplar = ejemplares.buscarPorId(reserva.getEjemplarAsignadoId())
                        .orElseThrow(() -> new ExcepcionDominio(
                                "Ejemplar inexistente: " + reserva.getEjemplarAsignadoId()));
                liberarEjemplar(ejemplar, ahora);
            }
        }
    }

    public void liberarEjemplar(Ejemplar ejemplar, Instant ahora) {
        Optional<Reserva> siguiente = reservas.delTitulo(ejemplar.getTituloId()).stream()
                .filter(Reserva::estaEnEspera)
                .findFirst();

        if (siguiente.isPresent()) {
            Reserva reserva = siguiente.get();
            reserva.marcarLista(ejemplar.getId(), ahora.plus(politicas.biblioteca().ventanaRecogida()));
            reservas.guardar(reserva);
            ejemplar.cambiarEstado(EstadoEjemplar.RESERVADO);
        } else {
            ejemplar.cambiarEstado(EstadoEjemplar.DISPONIBLE);
        }
        ejemplares.guardar(ejemplar);
    }
}
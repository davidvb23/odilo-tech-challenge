package com.delamo.library.application;

import com.delamo.library.domain.exception.ExcepcionDominio;
import com.delamo.library.domain.exception.ReservaNoPermitidaException;
import com.delamo.library.domain.model.Ejemplar;
import com.delamo.library.domain.model.Reserva;
import com.delamo.library.domain.port.RepositorioEjemplares;
import com.delamo.library.domain.port.RepositorioPrestamos;
import com.delamo.library.domain.port.RepositorioReservas;
import com.delamo.library.domain.port.RepositorioSocios;
import com.delamo.library.infrastructure.concurrency.RegistroBloqueosPorTitulo;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

/** Solo colocar reserva. Cancelar reservas queda fuera de alcance. */
public class ServicioReservas {

    private final RepositorioSocios socios;
    private final RepositorioPrestamos prestamos;
    private final RepositorioEjemplares ejemplares;
    private final RepositorioReservas reservas;
    private final GestorReservas gestor;
    private final RegistroBloqueosPorTitulo bloqueos;
    private final Clock reloj;

    public ServicioReservas(RepositorioSocios socios, RepositorioPrestamos prestamos,
                            RepositorioEjemplares ejemplares, RepositorioReservas reservas,
                            GestorReservas gestor, RegistroBloqueosPorTitulo bloqueos, Clock reloj) {
        this.socios = socios;
        this.prestamos = prestamos;
        this.ejemplares = ejemplares;
        this.reservas = reservas;
        this.gestor = gestor;
        this.bloqueos = bloqueos;
        this.reloj = reloj;
    }

    public Reserva colocarReserva(String socioId, String tituloId) {
        return bloqueos.conBloqueo(tituloId, () -> {
            socios.buscarPorId(socioId)
                    .orElseThrow(() -> new ExcepcionDominio("Socio inexistente: " + socioId));
            Instant ahora = Instant.now(reloj);

            gestor.caducarVencidas(tituloId, ahora);

            boolean yaLoTiene = prestamos.activosDelSocio(socioId).stream()
                    .anyMatch(p -> p.getTituloId().equals(tituloId));
            if (yaLoTiene) {
                throw new ReservaNoPermitidaException("el socio ya tiene este título en préstamo");
            }

            boolean yaEnCola = reservas.delTitulo(tituloId).stream()
                    .anyMatch(r -> r.getSocioId().equals(socioId) && (r.estaEnEspera() || r.estaLista()));
            if (yaEnCola) {
                throw new ReservaNoPermitidaException("el socio ya tiene una reserva activa de este título");
            }

            var ejemplaresTitulo = ejemplares.delTitulo(tituloId);
            if (ejemplaresTitulo.isEmpty()) {
                throw new ReservaNoPermitidaException("el título no tiene ejemplares");
            }
            if (ejemplaresTitulo.stream().anyMatch(Ejemplar::estaDisponible)) {
                throw new ReservaNoPermitidaException("hay ejemplares disponibles: se puede prestar directamente");
            }

            Reserva reserva = new Reserva(UUID.randomUUID().toString(), tituloId, socioId, ahora);
            reservas.guardar(reserva);
            return reserva;
        });
    }
}

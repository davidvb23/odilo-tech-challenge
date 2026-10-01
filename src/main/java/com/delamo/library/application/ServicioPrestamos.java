package com.delamo.library.application;

import com.delamo.library.domain.exception.BloqueadoPorMultasException;
import com.delamo.library.domain.exception.ExcepcionDominio;
import com.delamo.library.domain.exception.LimitePrestamosAlcanzadoException;
import com.delamo.library.domain.exception.SinEjemplarDisponibleException;
import com.delamo.library.domain.model.*;
import com.delamo.library.domain.policy.PoliticaNivel;
import com.delamo.library.domain.policy.ProveedorPoliticas;
import com.delamo.library.domain.port.RepositorioEjemplares;
import com.delamo.library.domain.port.RepositorioPrestamos;
import com.delamo.library.domain.port.RepositorioReservas;
import com.delamo.library.domain.port.RepositorioSocios;
import com.delamo.library.infrastructure.concurrency.RegistroBloqueosPorTitulo;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public class ServicioPrestamos {

    private final RepositorioSocios socios;
    private final RepositorioPrestamos prestamos;
    private final RepositorioEjemplares ejemplares;
    private final RepositorioReservas reservas;
    private final ServicioMultas multas;
    private final ProveedorPoliticas politicas;
    private final GestorReservas gestor;
    private final RegistroBloqueosPorTitulo bloqueos;
    private final Clock reloj;

    public ServicioPrestamos(RepositorioSocios socios, RepositorioPrestamos prestamos,
                             RepositorioEjemplares ejemplares, RepositorioReservas reservas,
                             ServicioMultas multas, ProveedorPoliticas politicas,
                             GestorReservas gestor, RegistroBloqueosPorTitulo bloqueos, Clock reloj) {
        this.socios = socios;
        this.prestamos = prestamos;
        this.ejemplares = ejemplares;
        this.reservas = reservas;
        this.multas = multas;
        this.politicas = politicas;
        this.gestor = gestor;
        this.bloqueos = bloqueos;
        this.reloj = reloj;
    }


    public Prestamo prestar(String socioId, String tituloId) {
        return bloqueos.conBloqueo(tituloId, () -> {
            Socio socio = socios.buscarPorId(socioId)
                    .orElseThrow(() -> new ExcepcionDominio("Socio inexistente: " + socioId));
            Instant ahora = Instant.now(reloj);
            LocalDate hoy = LocalDate.now(reloj);

            gestor.caducarVencidas(tituloId, ahora);

            PoliticaNivel politica = politicas.paraNivel(socio.getNivel());

            if (prestamos.activosDelSocio(socioId).size() >= politica.maxPrestamosSimultaneos()) {
                throw new LimitePrestamosAlcanzadoException(socioId, politica.maxPrestamosSimultaneos());
            }

            Optional<Reserva> suya = reservas.delTitulo(tituloId).stream()
                    .filter(r -> r.estaLista() && r.getSocioId().equals(socioId))
                    .findFirst();

            Ejemplar ejemplar;
            if (suya.isPresent()) {

                Reserva reserva = suya.get();
                ejemplar = ejemplares.buscarPorId(reserva.getEjemplarAsignadoId())
                        .orElseThrow(() -> new ExcepcionDominio(
                                "Ejemplar inexistente: " + reserva.getEjemplarAsignadoId()));
                reserva.recoger();
                reservas.guardar(reserva);
            } else {
                if (multas.estaBloqueado(socioId)) {
                    throw new BloqueadoPorMultasException(socioId, multas.pendiente(socioId));
                }
                // Los ejemplares RESERVADOS no cuentan como disponibles: nadie se salta la cola.
                ejemplar = ejemplares.delTitulo(tituloId).stream()
                        .filter(Ejemplar::estaDisponible)
                        .findFirst()
                        .orElseThrow(() -> new SinEjemplarDisponibleException(tituloId));
            }

            ejemplar.cambiarEstado(EstadoEjemplar.PRESTADO);
            ejemplares.guardar(ejemplar);

            Prestamo prestamo = new Prestamo(UUID.randomUUID().toString(), ejemplar.getId(), tituloId,
                    socioId, ahora, hoy.plusDays(politica.diasPrestamo()));
            prestamos.guardar(prestamo);
            return prestamo;
        });
    }
}

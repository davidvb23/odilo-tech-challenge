package com.delamo.library.application;

import com.delamo.library.domain.exception.ExcepcionDominio;
import com.delamo.library.domain.model.Ejemplar;
import com.delamo.library.domain.model.Multa;
import com.delamo.library.domain.model.Prestamo;
import com.delamo.library.domain.port.RepositorioEjemplares;
import com.delamo.library.domain.port.RepositorioPrestamos;
import com.delamo.library.infrastructure.concurrency.RegistroBloqueosPorTitulo;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

public class ServicioDevoluciones {

    private final RepositorioPrestamos prestamos;
    private final RepositorioEjemplares ejemplares;
    private final ServicioMultas multas;
    private final GestorReservas gestor;
    private final RegistroBloqueosPorTitulo bloqueos;
    private final Clock reloj;

    public ServicioDevoluciones(RepositorioPrestamos prestamos, RepositorioEjemplares ejemplares,
                                ServicioMultas multas, GestorReservas gestor,
                                RegistroBloqueosPorTitulo bloqueos, Clock reloj) {
        this.prestamos = prestamos;
        this.ejemplares = ejemplares;
        this.multas = multas;
        this.gestor = gestor;
        this.bloqueos = bloqueos;
        this.reloj = reloj;
    }

    public Optional<Multa> devolver(String prestamoId) {
        Prestamo prestamo = prestamos.buscarPorId(prestamoId)
                .orElseThrow(() -> new ExcepcionDominio("Préstamo inexistente: " + prestamoId));

        return bloqueos.conBloqueo(prestamo.getTituloId(), () -> {
            Instant ahora = Instant.now(reloj);
            LocalDate hoy = LocalDate.now(reloj);

            prestamo.cerrar(ahora);
            prestamos.guardar(prestamo);

            Optional<Multa> multa = multas.evaluarAlDevolver(prestamo, hoy);

            gestor.caducarVencidas(prestamo.getTituloId(), ahora);
            Ejemplar ejemplar = ejemplares.buscarPorId(prestamo.getEjemplarId())
                    .orElseThrow(() -> new ExcepcionDominio("Ejemplar inexistente: " + prestamo.getEjemplarId()));
            gestor.liberarEjemplar(ejemplar, ahora);

            return multa;
        });
    }
}


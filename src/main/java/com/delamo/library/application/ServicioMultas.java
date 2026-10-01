package com.delamo.library.application;

import com.delamo.library.domain.model.Multa;
import com.delamo.library.domain.model.Prestamo;

import com.delamo.library.domain.policy.PoliticaBiblioteca;
import com.delamo.library.domain.policy.ProveedorPoliticas;
import com.delamo.library.domain.port.RepositorioMultas;
import com.delamo.library.domain.port.RepositorioPrestamos;


import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public class ServicioMultas {

    private final ProveedorPoliticas politicas;
    private final RepositorioMultas multas;
    private final RepositorioPrestamos prestamos;
    private final Clock reloj;

    public ServicioMultas(ProveedorPoliticas politicas, RepositorioMultas multas,
                          RepositorioPrestamos prestamos, Clock reloj) {
        this.politicas = politicas;
        this.multas = multas;
        this.prestamos = prestamos;
        this.reloj = reloj;
    }

    public double pendiente(String socioId) {
        double multaPorDia = politicas.biblioteca().multaPorDia();
        LocalDate hoy = LocalDate.now(reloj);

        double total = 0.0;
        for (Multa m : multas.pendientesDelSocio(socioId)) {
            total += m.getImporte();
        }
        for (Prestamo p : prestamos.activosDelSocio(socioId)) {
            total += importe(multaPorDia, p.diasDeRetraso(hoy));
        }
        return total;
    }

    public boolean estaBloqueado(String socioId) {
        PoliticaBiblioteca biblioteca = politicas.biblioteca();
        return pendiente(socioId)>biblioteca.umbralBloqueo();
    }

    public Optional<Multa> evaluarAlDevolver(Prestamo prestamo, LocalDate fechaDevolucion) {
        long dias = prestamo.diasDeRetraso(fechaDevolucion);
        if (dias == 0) {
            return Optional.empty();
        }
        double valorImporte = importe(politicas.biblioteca().multaPorDia(), dias);
        Multa multa = new Multa(UUID.randomUUID().toString(), prestamo.getSocioId(), prestamo.getId(), valorImporte);
        multas.guardar(multa);
        return Optional.of(multa);
    }

    private static double importe(double multaPorDia, long dias) {
        return multaPorDia * dias;
    }
}

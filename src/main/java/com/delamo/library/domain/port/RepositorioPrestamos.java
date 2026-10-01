package com.delamo.library.domain.port;

import com.delamo.library.domain.model.Prestamo;

import java.util.List;
import java.util.Optional;

public interface RepositorioPrestamos {
    void guardar(Prestamo prestamo);
    Optional<Prestamo> buscarPorId(String id);
    List<Prestamo> activosDelSocio(String socioId);
}

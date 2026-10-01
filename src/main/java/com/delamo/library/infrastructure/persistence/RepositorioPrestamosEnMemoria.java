package com.delamo.library.infrastructure.persistence;

import com.delamo.library.domain.model.Prestamo;
import com.delamo.library.domain.port.RepositorioPrestamos;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RepositorioPrestamosEnMemoria implements RepositorioPrestamos {

    private final Map<String, Prestamo> porId = new ConcurrentHashMap<>();

    @Override public void guardar(Prestamo prestamo) { porId.put(prestamo.getId(), prestamo); }
    @Override public Optional<Prestamo> buscarPorId(String id) { return Optional.ofNullable(porId.get(id)); }

    // Recorre todos los préstamos: O(n). Sin índice socioId -> préstamos (recorte declarado).
    @Override
    public List<Prestamo> activosDelSocio(String socioId) {
        return porId.values().stream()
                .filter(p -> p.getSocioId().equals(socioId) && p.estaActivo())
                .toList();
    }
}

package com.delamo.library.infrastructure.persistence;

import com.delamo.library.domain.model.Ejemplar;
import com.delamo.library.domain.port.RepositorioEjemplares;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RepositorioEjemplaresEnMemoria implements RepositorioEjemplares {

    private final Map<String, Ejemplar> porId = new ConcurrentHashMap<>();
    private final Map<String, List<Ejemplar>> porTitulo = new ConcurrentHashMap<>();

    @Override
    public void guardar(Ejemplar ejemplar) {
        if (porId.putIfAbsent(ejemplar.getId(), ejemplar) == null) {
            porTitulo.computeIfAbsent(ejemplar.getTituloId(), k -> new CopyOnWriteArrayList<>()).add(ejemplar);
        }
    }

    @Override public Optional<Ejemplar> buscarPorId(String id) { return Optional.ofNullable(porId.get(id)); }

    @Override
    public List<Ejemplar> delTitulo(String tituloId) {
        return List.copyOf(porTitulo.getOrDefault(tituloId, List.of()));
    }
}

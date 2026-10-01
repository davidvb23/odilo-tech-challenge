package com.delamo.library.infrastructure.persistence;

import com.delamo.library.domain.model.Reserva;
import com.delamo.library.domain.port.RepositorioReservas;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RepositorioReservasEnMemoria implements RepositorioReservas {

    private final Map<String, Reserva> porId = new ConcurrentHashMap<>();
    private final Map<String, List<Reserva>> porTitulo = new ConcurrentHashMap<>();

    @Override
    public void guardar(Reserva reserva) {
        if (porId.putIfAbsent(reserva.getId(), reserva) == null) {
            porTitulo.computeIfAbsent(reserva.getTituloId(), k -> new CopyOnWriteArrayList<>()).add(reserva);
        }
    }

    @Override
    public List<Reserva> delTitulo(String tituloId) {
        return List.copyOf(porTitulo.getOrDefault(tituloId, List.of()));
    }
}

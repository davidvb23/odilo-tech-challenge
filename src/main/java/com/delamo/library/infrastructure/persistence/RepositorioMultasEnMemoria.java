package com.delamo.library.infrastructure.persistence;

import com.delamo.library.domain.model.Multa;
import com.delamo.library.domain.port.RepositorioMultas;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RepositorioMultasEnMemoria implements RepositorioMultas {

    private final Map<String, Multa> porId = new ConcurrentHashMap<>();

    @Override public void guardar(Multa multa) { porId.put(multa.getId(), multa); }

    @Override
    public List<Multa> pendientesDelSocio(String socioId) {
        return porId.values().stream()
                .filter(m -> m.getSocioId().equals(socioId) && !m.estaPagada())
                .toList();
    }
}

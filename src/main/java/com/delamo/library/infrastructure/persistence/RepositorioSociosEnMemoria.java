package com.delamo.library.infrastructure.persistence;

import com.delamo.library.domain.model.Socio;
import com.delamo.library.domain.port.RepositorioSocios;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RepositorioSociosEnMemoria implements RepositorioSocios {

    private final Map<String, Socio> porId = new ConcurrentHashMap<>();

    @Override public void guardar(Socio socio) { porId.put(socio.getId(), socio); }
    @Override public Optional<Socio> buscarPorId(String id) { return Optional.ofNullable(porId.get(id)); }
}

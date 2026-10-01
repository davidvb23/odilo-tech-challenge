package com.delamo.library.domain.port;

import com.delamo.library.domain.model.Socio;

import java.util.Optional;

public interface RepositorioSocios {
    void guardar(Socio socio);
    Optional<Socio> buscarPorId(String id);
}
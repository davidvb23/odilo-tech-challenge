package com.delamo.library.domain.port;

import com.delamo.library.domain.model.Ejemplar;

import java.util.List;
import java.util.Optional;

public interface RepositorioEjemplares {
    void guardar(Ejemplar ejemplar);
    Optional<Ejemplar> buscarPorId(String id);
    List<Ejemplar> delTitulo(String tituloId);
}
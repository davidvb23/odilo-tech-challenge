package com.delamo.library.domain.policy;

import com.delamo.library.domain.model.Nivel;

public interface ProveedorPoliticas {
    PoliticaNivel paraNivel(Nivel nivel);

    PoliticaBiblioteca biblioteca();

    void actualizar(Nivel nivel, PoliticaNivel politica);

    void actualizarBiblioteca(PoliticaBiblioteca politica);
}

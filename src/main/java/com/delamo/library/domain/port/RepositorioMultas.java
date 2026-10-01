package com.delamo.library.domain.port;

import com.delamo.library.domain.model.Multa;

import java.util.List;

public interface RepositorioMultas {
    void guardar(Multa multa);
    List<Multa> pendientesDelSocio(String socioId);   // las no pagadas
}

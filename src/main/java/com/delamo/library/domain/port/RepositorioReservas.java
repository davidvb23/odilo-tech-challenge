package com.delamo.library.domain.port;

import com.delamo.library.domain.model.Reserva;

import java.util.List;

public interface RepositorioReservas {
    void guardar(Reserva reserva);


    List<Reserva> delTitulo(String tituloId);
}
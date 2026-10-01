package com.delamo.library.infrastructure.policy;

import com.delamo.library.domain.model.Nivel;
import com.delamo.library.domain.policy.PoliticaBiblioteca;
import com.delamo.library.domain.policy.PoliticaNivel;
import com.delamo.library.domain.policy.ProveedorPoliticas;


import java.time.Duration;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ProveedorPoliticasEnMemoria implements ProveedorPoliticas {

    // Mapas inmutables: se sustituyen enteros con un swap atómico.
    // Las lecturas no usan locks y nunca ven un estado a medio actualizar.
    private final AtomicReference<Map<Nivel, PoliticaNivel>> porNivel;
    private final AtomicReference<PoliticaBiblioteca> biblioteca;

    public ProveedorPoliticasEnMemoria(Map<Nivel, PoliticaNivel> inicial, PoliticaBiblioteca bibliotecaInicial) {
        for (Nivel n : Nivel.values()) {
            if (!inicial.containsKey(n)) {
                throw new IllegalArgumentException("Falta la política del nivel " + n);
            }
        }
        this.porNivel = new AtomicReference<>(Collections.unmodifiableMap(new EnumMap<>(inicial)));
        this.biblioteca = new AtomicReference<>(bibliotecaInicial);
    }

    /** Valores de partida del enunciado. */
    public static ProveedorPoliticasEnMemoria conValoresPorDefecto() {
        Map<Nivel, PoliticaNivel> valores = new EnumMap<>(Nivel.class);
        valores.put(Nivel.ESTANDAR,   new PoliticaNivel(14, 3, 2));
        valores.put(Nivel.ESTUDIANTE, new PoliticaNivel(28, 5, 3));
        valores.put(Nivel.PERSONAL,   new PoliticaNivel(56, 10, null));
        return new ProveedorPoliticasEnMemoria(valores,
                new PoliticaBiblioteca(0.20, 10.00, Duration.ofHours(48)));
    }

    @Override
    public PoliticaNivel paraNivel(Nivel nivel) {
        return porNivel.get().get(nivel);
    }

    @Override
    public PoliticaBiblioteca biblioteca() {
        return biblioteca.get();
    }

    @Override
    public void actualizar(Nivel nivel, PoliticaNivel politica) {
        porNivel.updateAndGet(actual -> {
            Map<Nivel, PoliticaNivel> copia = new EnumMap<>(actual);
            copia.put(nivel, politica);
            return Collections.unmodifiableMap(copia);
        });
    }

    @Override
    public void actualizarBiblioteca(PoliticaBiblioteca politica) {
        biblioteca.set(politica);
    }
}

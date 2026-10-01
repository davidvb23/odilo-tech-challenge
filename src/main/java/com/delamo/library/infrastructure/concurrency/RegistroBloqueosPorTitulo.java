package com.delamo.library.infrastructure.concurrency;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class RegistroBloqueosPorTitulo {

      private final Map<String, ReentrantLock> bloqueos = new ConcurrentHashMap<>();

    public <T> T conBloqueo(String tituloId, Supplier<T> accion) {
        ReentrantLock bloqueo = bloqueos.computeIfAbsent(tituloId, k -> new ReentrantLock());
        bloqueo.lock();
        try {
            return accion.get();
        } finally {
            bloqueo.unlock();
        }
    }
}

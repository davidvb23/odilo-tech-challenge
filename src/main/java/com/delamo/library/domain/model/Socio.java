package com.delamo.library.domain.model;

public class Socio {
    private final String id;
    private final String nombre;
    private final Nivel nivel;

    public Socio(String id, String nombre, Nivel nivel) {
        this.id = id;
        this.nombre = nombre;
        this.nivel = nivel;
    }

    public String getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
    public Nivel getNivel() {
        return nivel;
    }
}

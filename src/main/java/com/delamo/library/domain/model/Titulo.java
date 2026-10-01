package com.delamo.library.domain.model;

public class Titulo {

    private final String id;
    private final String nombre;

    public Titulo(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
}
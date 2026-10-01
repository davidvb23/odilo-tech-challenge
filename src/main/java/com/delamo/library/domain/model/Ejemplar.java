package com.delamo.library.domain.model;

public class Ejemplar {
    private final String id;
    private final String tituloId;
    private EstadoEjemplar estado = EstadoEjemplar.DISPONIBLE;

    public Ejemplar(String id, String tituloId) {
        this.id = id;
        this.tituloId = tituloId;
    }

    public String getId() {
        return id;
    }
    public String getTituloId() {
        return tituloId;
    }
    public EstadoEjemplar getEstado() {
        return estado;
    }

    public boolean estaDisponible() {
        return estado == EstadoEjemplar.DISPONIBLE;
    }

    public void cambiarEstado(EstadoEjemplar nuevoEstado){
        if(estado == nuevoEstado){
            throw new IllegalStateException("El ejemplar " + id + " ya está con el estado: " + estado);
        }
        estado= nuevoEstado;
    }
}

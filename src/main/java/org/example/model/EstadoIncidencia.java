package org.example.model;

public enum EstadoIncidencia {
    EN_PROCESO("En Proceso"),
    REPARADO("Reparado"),
    FINALIZADO("Finalizado");
    
    private final String descripcion;
    
    EstadoIncidencia(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    @Override
    public String toString() {
        return descripcion;
    }
}

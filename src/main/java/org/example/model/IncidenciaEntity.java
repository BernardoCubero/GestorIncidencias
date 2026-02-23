package org.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidencias_relacionales")
public class IncidenciaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private LocalDateTime fechaCreacion;
    
    // Estado de la incidencia
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoIncidencia estado;
    
    // Datos del cliente
    private String nombreCliente;
    private String telefonoCliente;
    private String emailCliente;

    // Relaciones (El requisito de las 2 relaciones One-To-Many)
    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;

    public IncidenciaEntity() {}

    public IncidenciaEntity(String titulo) {
        this.titulo = titulo;
        this.fechaCreacion = LocalDateTime.now();
    }
    
    public IncidenciaEntity(String titulo, String nombreCliente, String telefonoCliente, String emailCliente) {
        this.titulo = titulo;
        this.nombreCliente = nombreCliente;
        this.telefonoCliente = telefonoCliente;
        this.emailCliente = emailCliente;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoIncidencia.EN_PROCESO; // Por defecto
    }

    // GETTERS Y SETTERS (Importante el de id para el Controller)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
    
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    
    public String getTelefonoCliente() { return telefonoCliente; }
    public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }
    
    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }
    
    public EstadoIncidencia getEstado() { return estado; }
    public void setEstado(EstadoIncidencia estado) { this.estado = estado; }
}
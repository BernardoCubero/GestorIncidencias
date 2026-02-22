package org.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registro_incidencias")
public class IncidenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    public IncidenciaEntity() {}

    public IncidenciaEntity(String titulo) {
        this.titulo = titulo;
        this.fechaRegistro = LocalDateTime.now();
    }


}
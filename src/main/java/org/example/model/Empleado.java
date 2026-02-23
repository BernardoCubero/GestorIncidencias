package org.example.model;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empleados")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    // RELACIÓN 1: Muchos empleados pertenecen a un departamento
    @ManyToOne
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;

    // RELACIÓN 2: Un empleado puede tener muchas incidencias
    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL)
    private List<IncidenciaEntity> incidencias = new ArrayList<>();

    public Empleado() {}

    public Empleado(String nombre, Departamento departamento) {
        this.nombre = nombre;
        this.departamento = departamento;
    }

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Departamento getDepartamento() { return departamento; }
    public void setDepartamento(Departamento departamento) { this.departamento = departamento; }

    public List<IncidenciaEntity> getIncidencias() { return incidencias; }
    public void setIncidencias(List<IncidenciaEntity> incidencias) { this.incidencias = incidencias; }
    
    @Override
    public String toString() {
        if (departamento != null) {
            return nombre + " (" + departamento.getNombre() + ")";
        }
        return nombre;
    }
}
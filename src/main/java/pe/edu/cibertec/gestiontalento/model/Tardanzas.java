package pe.edu.cibertec.gestiontalento.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tardanzas")
public class Tardanzas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tardanza")
    private int idTardanza;

    @ManyToOne
    @JoinColumn(name = "id_empleado")
    private Empleados empleado;

    private String fecha;

    @Column(name = "hora_tardanza")
    private String horaTardanza;
}
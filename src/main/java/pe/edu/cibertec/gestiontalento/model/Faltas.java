package pe.edu.cibertec.gestiontalento.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "faltas")
public class Faltas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_falta")
    private int idFalta;

    @ManyToOne
    @JoinColumn(name = "id_empleado")
    private Empleados empleado;

    private String fecha;
    private String motivo;
}
package pe.edu.cibertec.gestiontalento.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "empleados")
public class Empleados {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private int idEmpleado;

    private String nombre;
    private String apellido;
    private String dni;
    private String celular;
    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoEmpleado estado = EstadoEmpleado.Activo;

    @ManyToOne
    @JoinColumn(name = "id_horario")
    private Horarios horario;

    @ManyToOne
    @JoinColumn(name = "id_departamento")
    private Departamentos departamento;
}
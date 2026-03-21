package pe.edu.cibertec.gestiontalento.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "asistencias")
public class Asistencias {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asistencia")
    private int idAsistencia;

    @ManyToOne
    @JoinColumn(name = "id_empleado")
    private Empleados empleado;

    private String fecha;

    @Column(name = "hora_entrada")
    private String horaEntrada;

    @Column(name = "hora_salida")
    private String horaSalida;
}
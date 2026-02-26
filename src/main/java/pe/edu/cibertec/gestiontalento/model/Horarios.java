package pe.edu.cibertec.gestiontalento.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Horarios")
public class Horarios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Horario")
    private int idHorario;

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "Hora_entrada")
    private String horaEntrada;

    @Column(name = "Hora_salida")
    private String horaSalida;
}

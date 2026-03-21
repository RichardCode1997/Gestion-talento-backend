package pe.edu.cibertec.gestiontalento.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "horarios")
public class Horarios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_horario")
    private int idHorario;

    private String nombre;

    @Column(name = "hora_entrada")
    @JsonProperty("hora_entrada")
    private String horaEntrada;

    @Column(name = "hora_salida")
    @JsonProperty("hora_salida")
    private String horaSalida;
}
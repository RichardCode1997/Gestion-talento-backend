package pe.edu.cibertec.gestiontalento.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "noticias")
public class Noticias {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_noticias")
    private int idNoticia;

    @Column(name = "noticia_titulo")
    private String titulo;

    @Column(name = "noticia_detalle")
    private String detalle;

    private String estado;
}
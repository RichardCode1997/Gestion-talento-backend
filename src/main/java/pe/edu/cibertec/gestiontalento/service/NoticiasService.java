package pe.edu.cibertec.gestiontalento.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.gestiontalento.model.Noticias;
import pe.edu.cibertec.gestiontalento.repository.NoticiasRepository;

import java.util.List;

@Service
public class NoticiasService {

    private final NoticiasRepository noticiasRepository;

    @Autowired
    public NoticiasService(NoticiasRepository noticiasRepository) {
        this.noticiasRepository = noticiasRepository;
    }

    public Noticias crearNoticia(Noticias noticia) {
        return noticiasRepository.save(noticia);
    }

    public List<Noticias> listarNoticias() {
        return noticiasRepository.findAll();
    }

    public Noticias obtenerNoticiaPorId(int id) {
        return noticiasRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la noticia con ID: " + id));
    }

    public Noticias actualizarNoticia(Noticias noticia) {
        obtenerNoticiaPorId(noticia.getIdNoticia()); // Valida existencia
        return noticiasRepository.save(noticia);
    }

    public void eliminarNoticia(int id) {
        Noticias noticia = obtenerNoticiaPorId(id); // Valida existencia
        noticiasRepository.delete(noticia);
    }
}
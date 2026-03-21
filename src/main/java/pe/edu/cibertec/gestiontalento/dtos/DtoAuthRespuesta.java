package pe.edu.cibertec.gestiontalento.dtos;

import lombok.Data;

@Data
public class DtoAuthRespuesta {
    private String accessToken;
    private String tokenType = "Bearer"; // Sin el espacio al final aquí

    public DtoAuthRespuesta(String accessToken) {
        this.accessToken = accessToken;
    }
}
package pe.edu.cibertec.gestiontalento.dtos;

import lombok.Data;

@Data
public class DtoAuthRespuesta {
    private String accessToken;
    private String tokenType = "Bearer"; // Sin el espacio al final aquí
    private String correo;
    private String rol;

    public DtoAuthRespuesta(String accessToken, String correo, String rol) {
        this.accessToken = accessToken;
        this.correo      = correo;
        this.rol         = rol;
    }
}
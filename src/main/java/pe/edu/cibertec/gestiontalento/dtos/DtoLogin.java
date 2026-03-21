package pe.edu.cibertec.gestiontalento.dtos;

import lombok.Data;

@Data
public class DtoLogin {
    private String correo;
    private String password;
}
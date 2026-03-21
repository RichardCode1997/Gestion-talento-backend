package pe.edu.cibertec.gestiontalento.security;

public class ConstantesSeguridad {
    // 900,000 ms = 15 minutos
    public static final long JWT_EXPIRATION_TOKEN = 900000;

    // IMPORTANTE: Para HS512, la firma debe tener al menos 64 caracteres.
    // Esta cadena tiene exactamente 64 caracteres para que no falle.
    public static final String JWT_FIRMA = "EstaEsUnaFirmaMuySecretaYProtegidaParaGestionTalentoCibertec2026";
}
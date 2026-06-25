package be.profacile.savefunds.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de réponse pour l'authentification.
 *
 * Retourne le token JWT ainsi que les informations de l'utilisateur.
 *
 * SFB-110 : AuthResponse DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /**
     * Token JWT généré après authentification.
     */
    private String token;

    /**
     * Informations de l'utilisateur connecté.
     */
    private UserResponse user;
}
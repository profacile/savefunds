package be.profacile.savefunds.api.dto.response;

import be.profacile.savefunds.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de réponse représentant un utilisateur.
 *
 * SFB-109 : UserResponse DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    /**
     * Identifiant de l'utilisateur
     */
    private Long id;

    /**
     * Email de l'utilisateur
     */
    private String email;

    /**
     * Nom
     */
    private String nom;

    /**
     * Prénom
     */
    private String prenom;

    /**
     * Rôle (DIRIGEANT / ADMIN)
     */
    private Role role;

    /**
     * Email vérifié ou non
     */
    private Boolean emailVerified;
}
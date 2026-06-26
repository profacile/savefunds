package be.profacile.savefunds.api.dto.request;

import be.profacile.savefunds.domain.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la mise à jour partielle d'un utilisateur.
 * L'email n'est pas modifiable (identifiant de connexion).
 * Le mot de passe est géré via ChangePasswordRequest.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour mettre à jour un utilisateur (mise à jour partielle)")
public class UpdateUserRequest {

    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    @Schema(description = "Nom de l'utilisateur", example = "SANDJONG MOTIO")
    private String nom;

    @Size(min = 2, max = 100, message = "Le prénom doit contenir entre 2 et 100 caractères")
    @Schema(description = "Prénom de l'utilisateur", example = "Christian")
    private String prenom;

    @Schema(description = "Statut de vérification de l'email", example = "true")
    private Boolean emailVerified;

    @Schema(description = "Rôle de l'utilisateur", example = "DIRIGEANT",
            allowableValues = {"DIRIGEANT", "COMPTABLE", "ADMIN"})
    private Role role;

    @Size(max = 1_000_000, message = "La photo de profil est trop volumineuse")
    @Schema(description = "Photo de profil encodee en data URL")
    private String photoUrl;
}

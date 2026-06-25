package be.profacile.savefunds.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour le changement de mot de passe.
 * Requiert l'ancien mot de passe pour confirmation (sécurité).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour changer le mot de passe")
public class ChangePasswordRequest {

    @NotBlank(message = "L'ancien mot de passe est obligatoire")
    @Schema(description = "Mot de passe actuel", required = true)
    private String ancienMotDePasse;

    @NotBlank(message = "Le nouveau mot de passe est obligatoire")
    @Size(min = 6, message = "Le nouveau mot de passe doit contenir au moins 6 caractères")
    @Schema(description = "Nouveau mot de passe", required = true)
    private String nouveauMotDePasse;

    @NotBlank(message = "La confirmation est obligatoire")
    @Schema(description = "Confirmation du nouveau mot de passe", required = true)
    private String confirmationMotDePasse;
}
package be.profacile.savefunds.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO pour la mise à jour d'une company existante.
 *
 * Mise à jour partielle (field-by-field) :
 * - Tous les champs sont optionnels
 * - Seuls les champs non-null sont mis à jour
 * - userId et enterpriseNumber ne peuvent pas être modifiés
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour mettre à jour une company (mise à jour partielle)")
public class UpdateCompanyRequest {

    @Size(min = 1, max = 255, message = "La raison sociale doit contenir entre 1 et 255 caractères")
    @Schema(description = "Raison sociale de l'company", example = "Profacile SRL")
    private String legalName;

    @Schema(description = "Forme juridique", example = "SRL")
    private String legalForm;

    @Schema(description = "Secteur d'activité", example = "Services informatiques")
    private String activitySector;

    @DecimalMin(value = "0.0", message = "La trésorerie doit être positive ou nulle")
    @Digits(integer = 12, fraction = 2, message = "Format invalide (max 12 chiffres, 2 décimales)")
    @Schema(description = "Trésorerie disponible (EUR)", example = "25000.00")
    private BigDecimal cashBalance;

    @Digits(integer = 12, fraction = 2, message = "Format invalide (max 12 chiffres, 2 décimales)")
    @Schema(description = "Solde du compte courant (EUR)", example = "-5000.00")
    private BigDecimal directorCurrentAccountBalance;

    // Dans UpdateCompanyRequest — optionnels (pas de @NotNull)
    @DecimalMin(value = "0.01", message = "Le chiffre d'affaires doit être supérieur à 0")
    @Digits(integer = 12, fraction = 2, message = "Format invalide")
    @Schema(description = "Chiffre d'affaires mensuel (EUR)", example = "50000.00")
    private BigDecimal monthlyRevenue;

    @DecimalMin(value = "0.01", message = "Les charges doivent être supérieures à 0")
    @Digits(integer = 12, fraction = 2, message = "Format invalide")
    @Schema(description = "Charges mensuelles (EUR)", example = "30000.00")
    private BigDecimal monthlyExpenses;

}


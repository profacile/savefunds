package be.profacile.savefunds.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO pour la création d'une nouvelle entreprise.
 *
 * Règles de validation :
 * - userId : obligatoire, > 0
 * - raisonSociale : obligatoire, 1-255 caractères
 * - numeroEntreprise : obligatoire, format belge valide
 * - nomDirigeant, prenomDirigeant : obligatoires, 1-100 caractères
 * - Données financières : obligatoires, ≥ 0 (sauf soldeCompteCourant)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour créer une nouvelle entreprise")
public class CreateEntrepriseRequest {

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    @Positive(message = "L'ID de l'utilisateur doit être positif")
    @Schema(description = "ID de l'utilisateur propriétaire", example = "1", required = true)
    private Long userId;

    @NotBlank(message = "La raison sociale est obligatoire")
    @Size(min = 1, max = 255, message = "La raison sociale doit contenir entre 1 et 255 caractères")
    @Schema(description = "Raison sociale de l'entreprise", example = "Profacile SRL", required = true)
    private String raisonSociale;

    @NotBlank(message = "Le numéro d'entreprise est obligatoire")
    @Pattern(regexp = "^(BE\\s?)?[0-1]\\d{3}\\.?\\d{3}\\.?\\d{3}$",
             message = "Format de numéro d'entreprise belge invalide (ex: BE 0123.456.789)")
    @Schema(description = "Numéro d'entreprise belge", example = "BE 0123.456.789", required = true)
    private String numeroEntreprise;

    @Schema(description = "Forme juridique", example = "SRL")
    private String formeJuridique;

    @Schema(description = "Secteur d'activité", example = "Services informatiques")
    private String secteurActivite;

    @NotNull(message = "La trésorerie est obligatoire")
    @DecimalMin(value = "0.0", message = "La trésorerie doit être positive ou nulle")
    @Digits(integer = 12, fraction = 2, message = "Format invalide (max 12 chiffres, 2 décimales)")
    @Schema(description = "Trésorerie disponible (EUR)", example = "25000.00", required = true)
    private BigDecimal tresorerie;

    @Digits(integer = 12, fraction = 2, message = "Format invalide (max 12 chiffres, 2 décimales)")
    @Schema(description = "Solde du compte courant (EUR, peut être négatif)", example = "-5000.00")
    private BigDecimal soldeCompteCourant;

    // Dans CreateEntrepriseRequest
    @NotNull(message = "Le chiffre d'affaires mensuel est obligatoire")
    @DecimalMin(value = "0.01", message = "Le chiffre d'affaires doit être supérieur à 0")
    @Digits(integer = 12, fraction = 2, message = "Format invalide")
    @Schema(description = "Chiffre d'affaires mensuel (EUR)", example = "50000.00", required = true)
    private BigDecimal chiffreAffairesMensuel;

    @NotNull(message = "Les charges mensuelles sont obligatoires")
    @DecimalMin(value = "0.01", message = "Les charges doivent être supérieures à 0")
    @Digits(integer = 12, fraction = 2, message = "Format invalide")
    @Schema(description = "Charges mensuelles (EUR)", example = "30000.00", required = true)
    private BigDecimal chargesMensuelles;
}
package be.profacile.savefunds.api.dto.response;

import be.profacile.savefunds.domain.enums.StatutEntreprise;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Détails d'une entreprise")
public class EntrepriseResponse {

    @Schema(description = "ID unique de l'entreprise", example = "1")
    private Long id;

    @Schema(description = "ID de l'utilisateur propriétaire", example = "1")
    private Long userId;

    @Schema(description = "Raison sociale", example = "Profacile SRL")
    private String raisonSociale;

    @Schema(description = "Numéro d'entreprise belge", example = "BE0123456789")
    private String numeroEntreprise;

    @Schema(description = "Forme juridique", example = "SRL")
    private String formeJuridique;

    @Schema(description = "Secteur d'activité", example = "Informatique")
    private String secteurActivite;

    @Schema(description = "Chiffre d'affaires mensuel (EUR)", example = "50000.00")
    private BigDecimal chiffreAffairesMensuel;

    @Schema(description = "Charges mensuelles (EUR)", example = "30000.00")
    private BigDecimal chargesMensuelles;

    @Schema(description = "Trésorerie disponible (EUR)", example = "100000.00")
    private BigDecimal tresorerie;

    @Schema(description = "Solde du compte courant (EUR)", example = "5000.00")
    private BigDecimal soldeCompteCourant;

    @Schema(description = "Statut de l'entreprise", example = "ACTIVE")
    private StatutEntreprise statut;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Date de création", example = "2026-04-06T14:30:00")
    private LocalDateTime createdAt;
}
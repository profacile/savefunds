package be.profacile.savefunds.api.dto.response;

import be.profacile.savefunds.domain.enums.Decision;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour le résultat d'une analysis de prélèvement.
 *
 * Structure complète du résultat d'analysis avec système tricolore :
 *
 * 1. DÉCISION GLOBALE
 *    - globalDecision : VERT / ORANGE / ROUGE
 *    - globalDecisionDetails : Explication de la décision
 *    - globalRecommendation : Conseil général
 *
 * 2. SCORES CALCULÉS
 *    - cashScore : Trésorerie en mois de charges
 *    - revenueExpensesRatioScore : Ratio CA/Charges
 *    - directorCurrentAccountScore : Jours en débiteur
 *
 * 3. DÉCISIONS PAR CRITÈRE
 *    - cashDecision : VERT / ORANGE / ROUGE
 *    - revenueExpensesRatioDecision : VERT / ORANGE / ROUGE
 *    - directorCurrentAccountDecision : VERT / ORANGE / ROUGE
 *
 * 4. DÉTAILS ET RECOMMANDATIONS
 *    - Pour chaque critère : explication + recommendation
 *
 * @author Profacile SRL
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Résultat complet d'une analysis de prélèvement avec décision tricolore")
public class AnalysisResultResponse {

    // ==================== IDENTIFIANTS ====================

    @Schema(
            description = "ID unique du résultat d'analysis",
            example = "1",
            required = true
    )
    private Long id;

    @Schema(
            description = "ID de l'analysis de prélèvement associée",
            example = "1",
            required = true
    )
    private Long analysisId;

    // ==================== DÉCISION GLOBALE ====================

    @Schema(
            description = "Décision finale basée sur le système tricolore",
            example = "ORANGE",
            allowableValues = {"VERT", "ORANGE", "ROUGE"},
            required = true
    )
    private Decision globalDecision;

    @Schema(
            description = "Explication détaillée de la décision globale",
            example = "⚠️ Situation nécessitant une vigilance. 1 indicateur(s) en ORANGE. Prélèvement possible mais limité.",
            required = true
    )
    private String globalDecisionDetails;

    @Schema(
            description = "Recommendation générale pour le dirigeant",
            example = "Vous pouvez effectuer un prélèvement, mais restez vigilant sur votre trésorerie. Évitez les prélèvements trop importants.",
            required = true
    )
    private String globalRecommendation;

    // ==================== SCORES CALCULÉS ====================

    @Schema(
            description = "Trésorerie disponible exprimée en lastNamebre de mois de charges",
            example = "2.5",
            minimum = "0",
            required = true
    )
    private BigDecimal cashScore;

    @Schema(
            description = "Ratio Chiffre d'Affaires / Charges totales (indicateur de rentabilité)",
            example = "1.25",
            minimum = "0",
            required = true
    )
    private BigDecimal revenueExpensesRatioScore;

    @Schema(
            description = "Nombre de jours consécutifs avec compte courant débiteur",
            example = "15",
            minimum = "0",
            required = true
    )
    private Integer directorCurrentAccountScore;

    @Schema(
            description = "Montant souhaité par le dirigeant",
            example = "5000.00"
    )
    private BigDecimal requestedAmount;

    @Schema(
            description = "Montant maximum prélevable (trésorerie - 3 mois de charges)",
            example = "7000.00"
    )
    private BigDecimal maxWithdrawableAmount;

    // ==================== DÉCISIONS PAR CRITÈRE ====================

    @Schema(
            description = "Décision pour le critère trésorerie (VERT: ≥3 mois, ORANGE: 1-3 mois, ROUGE: <1 mois)",
            example = "ORANGE",
            allowableValues = {"VERT", "ORANGE", "ROUGE"},
            required = true
    )
    private Decision cashDecision;

    @Schema(
            description = "Décision pour le critère ratio CA/Charges (VERT: ≥1.3, ORANGE: 1.0-1.3, ROUGE: <1.0)",
            example = "VERT",
            allowableValues = {"VERT", "ORANGE", "ROUGE"},
            required = true
    )
    private Decision revenueExpensesRatioDecision;

    @Schema(
            description = "Décision pour le critère compte courant (VERT: 0j, ORANGE: 1-30j, ROUGE: >30j)",
            example = "VERT",
            allowableValues = {"VERT", "ORANGE", "ROUGE"},
            required = true
    )
    private Decision directorCurrentAccountDecision;

    // ==================== DÉTAILS PAR CRITÈRE ====================

    @Schema(
            description = "Explication détaillée du score de trésorerie",
            example = "Trésorerie acceptable mais limitée. Vous avez 2.50 mois de charges en réserve. Visez au moins 3 mois pour plus de sécurité."
    )
    private String cashDetails;

    @Schema(
            description = "Explication détaillée du ratio CA/Charges",
            example = "Excellente rentabilité ! Votre marge est de 25.0%, bien au-dessus du seuil minimum (30%)."
    )
    private String revenueExpensesRatioDetails;

    @Schema(
            description = "Explication détaillée de l'état du compte courant",
            example = "Compte courant débiteur depuis 15 jours. Restez vigilant et remboursez rapidement."
    )
    private String directorCurrentAccountDetails;

    // ==================== RECOMMANDATIONS PAR CRITÈRE ====================

    @Schema(
            description = "Recommendation spécifique pour améliorer la trésorerie",
            example = "Constituez une réserve d'au moins 3 mois de charges pour sécuriser votre company face aux imprévus."
    )
    private String cashRecommendation;

    @Schema(
            description = "Recommendation spécifique pour améliorer le ratio CA/Charges",
            example = "Maintenez vos marges et contrôlez vos charges. Votre situation est saine."
    )
    private String revenueExpensesRatioRecommendation;

    @Schema(
            description = "Recommendation spécifique pour gérer le compte courant",
            example = "Remboursez rapidement pour éviter un blocage bancaire et des frais supplémentaires."
    )
    private String directorCurrentAccountRecommendation;

    // ==================== MÉTADONNÉES ====================

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(
            description = "Date et heure de création du résultat (calcul de l'analysis)",
            example = "2026-04-06T14:35:00",
            type = "string",
            format = "date-time"
    )
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(
            description = "Date et heure de dernière modification du résultat",
            example = "2026-04-06T14:35:00",
            type = "string",
            format = "date-time"
    )
    private LocalDateTime updatedAt;

}
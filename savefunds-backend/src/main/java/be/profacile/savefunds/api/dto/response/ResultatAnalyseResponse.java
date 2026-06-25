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
 * DTO de réponse pour le résultat d'une analyse de prélèvement.
 *
 * Structure complète du résultat d'analyse avec système tricolore :
 *
 * 1. DÉCISION GLOBALE
 *    - decisionGlobale : VERT / ORANGE / ROUGE
 *    - detailsDecisionGlobale : Explication de la décision
 *    - recommandationGlobale : Conseil général
 *
 * 2. SCORES CALCULÉS
 *    - scoreTresorerie : Trésorerie en mois de charges
 *    - scoreRatioCACharges : Ratio CA/Charges
 *    - scoreCompteCourantDebiteur : Jours en débiteur
 *
 * 3. DÉCISIONS PAR CRITÈRE
 *    - decisionTresorerie : VERT / ORANGE / ROUGE
 *    - decisionRatioCACharges : VERT / ORANGE / ROUGE
 *    - decisionCompteCourant : VERT / ORANGE / ROUGE
 *
 * 4. DÉTAILS ET RECOMMANDATIONS
 *    - Pour chaque critère : explication + recommandation
 *
 * @author Profacile SRL
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Résultat complet d'une analyse de prélèvement avec décision tricolore")
public class ResultatAnalyseResponse {

    // ==================== IDENTIFIANTS ====================

    @Schema(
            description = "ID unique du résultat d'analyse",
            example = "1",
            required = true
    )
    private Long id;

    @Schema(
            description = "ID de l'analyse de prélèvement associée",
            example = "1",
            required = true
    )
    private Long analyseId;

    // ==================== DÉCISION GLOBALE ====================

    @Schema(
            description = "Décision finale basée sur le système tricolore",
            example = "ORANGE",
            allowableValues = {"VERT", "ORANGE", "ROUGE"},
            required = true
    )
    private Decision decisionGlobale;

    @Schema(
            description = "Explication détaillée de la décision globale",
            example = "⚠️ Situation nécessitant une vigilance. 1 indicateur(s) en ORANGE. Prélèvement possible mais limité.",
            required = true
    )
    private String detailsDecisionGlobale;

    @Schema(
            description = "Recommandation générale pour le dirigeant",
            example = "Vous pouvez effectuer un prélèvement, mais restez vigilant sur votre trésorerie. Évitez les prélèvements trop importants.",
            required = true
    )
    private String recommandationGlobale;

    // ==================== SCORES CALCULÉS ====================

    @Schema(
            description = "Trésorerie disponible exprimée en nombre de mois de charges",
            example = "2.5",
            minimum = "0",
            required = true
    )
    private BigDecimal scoreTresorerie;

    @Schema(
            description = "Ratio Chiffre d'Affaires / Charges totales (indicateur de rentabilité)",
            example = "1.25",
            minimum = "0",
            required = true
    )
    private BigDecimal scoreRatioCACharges;

    @Schema(
            description = "Nombre de jours consécutifs avec compte courant débiteur",
            example = "15",
            minimum = "0",
            required = true
    )
    private Integer scoreCompteCourantDebiteur;

    @Schema(
            description = "Montant souhaité par le dirigeant",
            example = "5000.00"
    )
    private BigDecimal montantSouhaite;

    @Schema(
            description = "Montant maximum prélevable (trésorerie - 3 mois de charges)",
            example = "7000.00"
    )
    private BigDecimal montantMaxPrelevable;

    // ==================== DÉCISIONS PAR CRITÈRE ====================

    @Schema(
            description = "Décision pour le critère trésorerie (VERT: ≥3 mois, ORANGE: 1-3 mois, ROUGE: <1 mois)",
            example = "ORANGE",
            allowableValues = {"VERT", "ORANGE", "ROUGE"},
            required = true
    )
    private Decision decisionTresorerie;

    @Schema(
            description = "Décision pour le critère ratio CA/Charges (VERT: ≥1.3, ORANGE: 1.0-1.3, ROUGE: <1.0)",
            example = "VERT",
            allowableValues = {"VERT", "ORANGE", "ROUGE"},
            required = true
    )
    private Decision decisionRatioCACharges;

    @Schema(
            description = "Décision pour le critère compte courant (VERT: 0j, ORANGE: 1-30j, ROUGE: >30j)",
            example = "VERT",
            allowableValues = {"VERT", "ORANGE", "ROUGE"},
            required = true
    )
    private Decision decisionCompteCourant;

    // ==================== DÉTAILS PAR CRITÈRE ====================

    @Schema(
            description = "Explication détaillée du score de trésorerie",
            example = "Trésorerie acceptable mais limitée. Vous avez 2.50 mois de charges en réserve. Visez au moins 3 mois pour plus de sécurité."
    )
    private String detailsTresorerie;

    @Schema(
            description = "Explication détaillée du ratio CA/Charges",
            example = "Excellente rentabilité ! Votre marge est de 25.0%, bien au-dessus du seuil minimum (30%)."
    )
    private String detailsRatioCACharges;

    @Schema(
            description = "Explication détaillée de l'état du compte courant",
            example = "Compte courant débiteur depuis 15 jours. Restez vigilant et remboursez rapidement."
    )
    private String detailsCompteCourant;

    // ==================== RECOMMANDATIONS PAR CRITÈRE ====================

    @Schema(
            description = "Recommandation spécifique pour améliorer la trésorerie",
            example = "Constituez une réserve d'au moins 3 mois de charges pour sécuriser votre entreprise face aux imprévus."
    )
    private String recommandationTresorerie;

    @Schema(
            description = "Recommandation spécifique pour améliorer le ratio CA/Charges",
            example = "Maintenez vos marges et contrôlez vos charges. Votre situation est saine."
    )
    private String recommandationRatioCACharges;

    @Schema(
            description = "Recommandation spécifique pour gérer le compte courant",
            example = "Remboursez rapidement pour éviter un blocage bancaire et des frais supplémentaires."
    )
    private String recommandationCompteCourant;

    // ==================== MÉTADONNÉES ====================

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(
            description = "Date et heure de création du résultat (calcul de l'analyse)",
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
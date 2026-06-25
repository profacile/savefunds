package be.profacile.savefunds.domain.service.impl;

import be.profacile.savefunds.domain.enums.Decision;
import be.profacile.savefunds.domain.service.GrilleTricoloreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Implémentation du service de grille de décision tricolore
 *
 * SEUILS DE DÉCISION MVP :
 *
 * 1. TRÉSORERIE EN MOIS:
 *    - VERT:   >= 3 mois
 *    - ORANGE: 1 à 3 mois
 *    - ROUGE:  < 1 mois
 *
 * 2. RATIO CA/CHARGES:
 *    - VERT:   >= 1.3
 *    - ORANGE: 1.0 à 1.3
 *    - ROUGE:  < 1.0
 *
 * 3. COMPTE COURANT DÉBITEUR:
 *    - VERT:   0 jours (jamais débiteur)
 *    - ORANGE: 1 à 30 jours
 *    - ROUGE:  > 30 jours
 *
 * 4. DÉCISION GLOBALE:
 *    - Si AU MOINS 1 ROUGE → ROUGE
 *    - Sinon si AU MOINS 1 ORANGE → ORANGE
 *    - Sinon (tous VERT) → VERT
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GrilleTricoloreServiceImpl implements GrilleTricoloreService {

    // Seuils pour la trésorerie (en mois)
    private static final BigDecimal SEUIL_TRESORERIE_VERT = new BigDecimal("3.0");
    private static final BigDecimal SEUIL_TRESORERIE_ORANGE = new BigDecimal("1.0");

    // Seuils pour le ratio CA/Charges
    private static final BigDecimal SEUIL_RATIO_VERT = new BigDecimal("1.3");
    private static final BigDecimal SEUIL_RATIO_ORANGE = new BigDecimal("1.0");

    // Seuils pour le compte courant débiteur (en jours)
    private static final int SEUIL_CC_ORANGE = 30;

    @Override
    public Decision calculerDecisionTresorerie(BigDecimal tresorerieEnMois) {
        log.debug("Calcul décision trésorerie - Valeur: {} mois", tresorerieEnMois);

        if (tresorerieEnMois == null) {
            throw new IllegalArgumentException("La trésorerie en mois ne peut pas être null");
        }

        // Comparer avec les seuils
        if (tresorerieEnMois.compareTo(SEUIL_TRESORERIE_VERT) >= 0) {
            log.debug("Trésorerie >= 3 mois → VERT");
            return Decision.VERT;
        }

        if (tresorerieEnMois.compareTo(SEUIL_TRESORERIE_ORANGE) >= 0) {
            log.debug("Trésorerie entre 1 et 3 mois → ORANGE");
            return Decision.ORANGE;
        }

        log.debug("Trésorerie < 1 mois → ROUGE");
        return Decision.ROUGE;
    }

    @Override
    public Decision calculerDecisionRatioCACharges(BigDecimal ratio) {
        log.debug("Calcul décision ratio CA/Charges - Valeur: {}", ratio);

        if (ratio == null) {
            throw new IllegalArgumentException("Le ratio CA/Charges ne peut pas être null");
        }

        // Comparer avec les seuils
        if (ratio.compareTo(SEUIL_RATIO_VERT) >= 0) {
            log.debug("Ratio >= 1.3 → VERT");
            return Decision.VERT;
        }

        if (ratio.compareTo(SEUIL_RATIO_ORANGE) >= 0) {
            log.debug("Ratio entre 1.0 et 1.3 → ORANGE");
            return Decision.ORANGE;
        }

        log.debug("Ratio < 1.0 → ROUGE");
        return Decision.ROUGE;
    }

    @Override
    public Decision calculerDecisionCompteCourant(int dureeDebiteur) {
        log.debug("Calcul décision compte courant - Durée débiteur: {} jours", dureeDebiteur);

        // Comparer avec les seuils
        if (dureeDebiteur == 0) {
            log.debug("Jamais débiteur (0 jours) → VERT");
            return Decision.VERT;
        }

        if (dureeDebiteur <= SEUIL_CC_ORANGE) {
            log.debug("Débiteur entre 1 et 30 jours → ORANGE");
            return Decision.ORANGE;
        }

        log.debug("Débiteur > 30 jours → ROUGE");
        return Decision.ROUGE;
    }

    @Override
    public Decision calculerDecisionGlobale(Decision tresorerie, Decision ratio,
                                            Decision compteCourant, Decision montant) {
        log.debug("Calcul décision globale - Tréso: {}, Ratio: {}, CC: {}, Montant: {}",
                tresorerie, ratio, compteCourant, montant);

        if (tresorerie == null || ratio == null || compteCourant == null || montant == null) {
            throw new IllegalArgumentException("Les décisions ne peuvent pas être null");
        }

        // RÈGLE: Le plus restrictif l'emporte
        // Si AU MOINS UNE décision est ROUGE → ROUGE
        if (tresorerie == Decision.ROUGE || ratio == Decision.ROUGE
                || compteCourant == Decision.ROUGE || montant == Decision.ROUGE) {
            log.debug("Au moins 1 critère ROUGE → Décision globale: ROUGE");
            return Decision.ROUGE;
        }

        // Si AU MOINS UNE décision est ORANGE → ORANGE
        if (tresorerie == Decision.ORANGE || ratio == Decision.ORANGE
                || compteCourant == Decision.ORANGE || montant == Decision.ORANGE) {
            log.debug("Au moins 1 critère ORANGE (pas de ROUGE) → Décision globale: ORANGE");
            return Decision.ORANGE;
        }

        // Tous VERT → VERT
        log.debug("Tous les critères VERT → Décision globale: VERT");
        return Decision.VERT;
    }

    @Override
    public String genererRecommandation(Decision decision, String critere) {
        log.debug("Génération recommandation - Décision: {}, Critère: {}", decision, critere);

        if (decision == null || critere == null) {
            throw new IllegalArgumentException("La décision et le critère ne peuvent pas être null");
        }

        return switch (critere.toLowerCase()) {
            case "tresorerie" -> genererRecommandationTresorerie(decision);
            case "ratio" -> genererRecommandationRatio(decision);
            case "comptecourant", "compte_courant" -> genererRecommandationCompteCourant(decision);
            default -> "Critère inconnu: " + critere;
        };
    }

    private String genererRecommandationTresorerie(Decision decision) {
        return switch (decision) {
            case VERT -> "Trésorerie excellente (≥ 3 mois de charges). Situation financière saine, prélèvement possible.";
            case ORANGE -> "Trésorerie limitée (entre 1 et 3 mois). Prélèvement à surveiller de près, prévoir un plan de trésorerie.";
            case ROUGE -> "Trésorerie critique (< 1 mois). Prélèvement fortement déconseillé, risque de défaut de paiement imminent.";
        };
    }

    private String genererRecommandationRatio(Decision decision) {
        return switch (decision) {
            case VERT -> "Ratio CA/Charges sain (≥ 1.3). L'entreprise génère suffisamment de revenus pour couvrir les charges avec marge.";
            case ORANGE -> "Ratio CA/Charges serré (entre 1.0 et 1.3). Attention aux charges, marge de sécurité limitée.";
            case ROUGE -> "Ratio CA/Charges déficitaire (< 1.0). Les charges dépassent le chiffre d'affaires, situation non viable.";
        };
    }

    private String genererRecommandationCompteCourant(Decision decision) {
        return switch (decision) {
            case VERT -> "Compte courant toujours créditeur (jamais débiteur). Excellente gestion de trésorerie.";
            case ORANGE -> "Compte courant temporairement débiteur (1 à 30 jours). Situation à surveiller, éviter de prolonger.";
            case ROUGE -> "Compte courant débiteur depuis trop longtemps (> 30 jours). Risque bancaire élevé, frais importants.";
        };
    }
}
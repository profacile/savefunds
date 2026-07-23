package be.profacile.savefunds.domain.service;

import be.profacile.savefunds.domain.enums.Decision;

import java.math.BigDecimal;

/**
 * Service de grille de décision tricolore
 * Applique les seuils métier pour déterminer VERT/ORANGE/ROUGE
 */
public interface TrafficLightDecisionService {

    /**
     * Calcule la décision basée sur la trésorerie en mois
     *
     * @param cashCoverageMonths Nombre de mois de trésorerie disponible
     * @return VERT (>= 3 mois), ORANGE (1-3 mois), ROUGE (< 1 mois)
     */
    Decision calculateCashDecision(BigDecimal cashCoverageMonths);

    /**
     * Calcule la décision basée sur le ratio CA/Charges
     *
     * @param ratio Ratio chiffre d'affaires / charges
     * @return VERT (>= 1.3), ORANGE (1.0-1.3), ROUGE (< 1.0)
     */
    Decision calculateRevenueExpensesRatioDecision(BigDecimal ratio);

    /**
     * Calcule la décision basée sur la durée du compte courant débiteur
     *
     * @param dureeDebiteur Nombre de jours consécutifs en négatif
     * @return VERT (0 jours), ORANGE (1-30 jours), ROUGE (> 30 jours)
     */
    Decision calculateDirectorCurrentAccountDecision(int dureeDebiteur);

    /**
     * Calcule la décision globale
     * Règle : Le plus restrictif l'emporte
     *
     * @param cashBalance      Décision du critère trésorerie
     * @param ratio           Décision du critère ratio CA/Charges
     * @param compteCourant   Décision du critère compte courant
     * @param decisionMontant Décision du critère montant souhaité vs max prélevable
     * @return ROUGE si au moins 1 ROUGE, sinon ORANGE si au moins 1 ORANGE, sinon VERT
     */
    Decision calculateGlobalDecision(Decision cashBalance, Decision ratio,
                                     Decision compteCourant, Decision decisionMontant);

    /**
     * Génère une recommendation textuelle
     *
     * @param decision La décision (VERT/ORANGE/ROUGE)
     * @param criterion Le critère concerné ("cashBalance", "ratio", "compteCourant")
     * @return Message de recommendation adapté
     */
    String generateRecommendation(Decision decision, String criterion);
}
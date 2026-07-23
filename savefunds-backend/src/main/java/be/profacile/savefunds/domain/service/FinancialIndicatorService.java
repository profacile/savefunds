package be.profacile.savefunds.domain.service;

import be.profacile.savefunds.domain.entity.Company;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Service de calcul des indicateurs financiers
 */
public interface FinancialIndicatorService {

    /**
     * Calcule le ratio CA/Charges
     *
     * @param chiffreAffaires Chiffre d'affaires mensuel
     * @param charges Charges mensuelles
     * @return Ratio (ex: 1.67 = CA est 1.67x les charges)
     */
    BigDecimal calculateRevenueExpensesRatio(BigDecimal chiffreAffaires, BigDecimal charges);

    /**
     * Calcule la trésorerie en lastNamebre de mois
     *
     * @param cashBalance Trésorerie disponible
     * @param monthlyExpenses Charges mensuelles
     * @return Nombre de mois de trésorerie (ex: 3.5 mois)
     */
    BigDecimal calculateCashCoverageMonths(BigDecimal cashBalance, BigDecimal monthlyExpenses);

    /**
     * Calcule la durée du compte courant débiteur
     *
     * @param directorCurrentAccountBalance Solde actuel
     * @param historiqueDebuts Liste des dates de début débiteur (optionnel pour MVP)
     * @return Nombre de jours consécutifs en négatif
     */
    int calculateDirectorCurrentAccountDebtorDays(BigDecimal directorCurrentAccountBalance, LocalDate dateDebutDebiteur);

    /**
     * Calcule tous les indicateurs d'une company
     *
     * @param company Company à result
     * @return Map<String, Object> avec tous les indicateurs
     */
    java.util.Map<String, Object> calculateAllFinancialIndicators(Company company);

    /**
     * Vérifie si les données financières sont complètes
     *
     * @param company Company à vérifier
     * @return true si toutes les données nécessaires sont présentes
     */
    boolean hasCompleteFinancialData(Company company);

    /**
     * Calcule le montant maximum prélevable
     *
     * @param cashBalance Trésorerie actuelle
     * @param monthlyExpenses Charges mensuelles
     * @param seuilSecurite Seuil de sécurité en mois (ex: 3 mois)
     * @return Montant maximum pouvant être prélevé sans danger
     */
    BigDecimal calculateMaximumWithdrawableAmount(
            BigDecimal cashBalance,
            BigDecimal monthlyExpenses,
            int seuilSecurite
    );
}
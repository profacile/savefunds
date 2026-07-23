package be.profacile.savefunds.domain.service.impl;

import be.profacile.savefunds.domain.entity.Company;
import be.profacile.savefunds.domain.service.FinancialIndicatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Implémentation du service de calcul des indicateurs financiers
 *
 * RÈGLES IMPORTANTES :
 * - Utiliser BigDecimal pour tous les calculs monétaires
 * - Arrondir avec RoundingMode.HALF_UP (arrondi mathématique)
 * - Gérer les divisions par zéro
 * - Logger les calculs importants
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialIndicatorServiceImpl implements FinancialIndicatorService {

    // Constantes métier
    private static final int PRECISION_RATIO = 2;  // 2 décimales pour les ratios
    private static final int PRECISION_MOIS = 2;   // 2 décimales pour les mois

    @Override
    public BigDecimal calculateRevenueExpensesRatio(BigDecimal chiffreAffaires, BigDecimal charges) {
        log.debug("Calcul ratio CA/Charges - CA: {}, Charges: {}", chiffreAffaires, charges);

        // Validations
        if (chiffreAffaires == null || charges == null) {
            throw new IllegalArgumentException("Le chiffre d'affaires et les charges ne peuvent pas être null");
        }

        if (charges.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Les charges doivent être strictement positives");
        }

        // Calcul du ratio CA / Charges
        BigDecimal ratio = chiffreAffaires.divide(charges, PRECISION_RATIO, RoundingMode.HALF_UP);

        log.debug("Ratio CA/Charges calculé : {}", ratio);

        return ratio;
    }

    @Override
    public BigDecimal calculateCashCoverageMonths(BigDecimal cashBalance, BigDecimal monthlyExpenses) {
        log.debug("Calcul trésorerie en mois - Tréso: {}, Charges: {}", cashBalance, monthlyExpenses);

        // Validations
        if (cashBalance == null || monthlyExpenses == null) {
            throw new IllegalArgumentException("La trésorerie et les charges mensuelles ne peuvent pas être null");
        }

        if (monthlyExpenses.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Les charges mensuelles doivent être strictement positives");
        }

        // Si trésorerie négative, retourner 0 (pas de mois de couverture)
        if (cashBalance.compareTo(BigDecimal.ZERO) < 0) {
            log.debug("Trésorerie négative, retour 0 mois");
            return BigDecimal.ZERO;
        }

        // Calcul : trésorerie / charges mensuelles
        BigDecimal mois = cashBalance.divide(monthlyExpenses, PRECISION_MOIS, RoundingMode.HALF_UP);

        log.debug("Trésorerie en mois calculée : {}", mois);

        return mois;
    }

    @Override
    public int calculateDirectorCurrentAccountDebtorDays(BigDecimal directorCurrentAccountBalance, LocalDate dateDebutDebiteur) {
        log.debug("Calcul durée CC débiteur - Solde: {}, Début: {}", directorCurrentAccountBalance, dateDebutDebiteur);

        // Si le compte n'est pas débiteur (solde >= 0), retourner 0
        if (directorCurrentAccountBalance == null || directorCurrentAccountBalance.compareTo(BigDecimal.ZERO) >= 0) {
            log.debug("Compte courant non débiteur, retour 0 jours");
            return 0;
        }

        // Si pas de date de début de débit mais solde négatif → ROUGE par défaut
        if (dateDebutDebiteur == null) {
            log.debug("Solde débiteur mais pas de date de début → 31 jours par défaut");
            return 31;
        }

        // Calculer le lastNamebre de jours depuis le début du débit
        long jours = ChronoUnit.DAYS.between(dateDebutDebiteur, LocalDate.now());

        // Assurer que le résultat n'est pas négatif
        // Minimum 1 jour si solde négatif
        int duree = (int) Math.max(1, jours);

        log.debug("Durée compte courant débiteur : {} jours", duree);

        return duree;
    }

    @Override
    public Map<String, Object> calculateAllFinancialIndicators(Company company) {
        log.info("Calcul de tous les indicateurs pour company ID: {}", company.getId());

        if (company == null) {
            throw new IllegalArgumentException("L'company ne peut pas être null");
        }

        Map<String, Object> indicateurs = new HashMap<>();

        try {
            // FinancialIndicator 1 : Ratio CA/Charges
            if (company.getMonthlyRevenue() != null &&
                    company.getMonthlyExpenses() != null) {

                BigDecimal ratioCACharges = calculateRevenueExpensesRatio(
                        company.getMonthlyRevenue(),
                        company.getMonthlyExpenses()
                );
                indicateurs.put("ratioCACharges", ratioCACharges);
            }

            // FinancialIndicator 2 : Trésorerie en mois
            if (company.getCashBalance() != null &&
                    company.getMonthlyExpenses() != null) {

                BigDecimal cashCoverageMonths = calculateCashCoverageMonths(
                        company.getCashBalance(),
                        company.getMonthlyExpenses()
                );
                indicateurs.put("cashCoverageMonths", cashCoverageMonths);
            }

            // FinancialIndicator 3 : Durée compte courant débiteur
            int directorCurrentAccountDebtorDays = calculateDirectorCurrentAccountDebtorDays(
                    company.getDirectorCurrentAccountBalance(),
                    company.getDirectorCurrentAccountDebitStartDate()
            );
            indicateurs.put("directorCurrentAccountDebtorDays", directorCurrentAccountDebtorDays);

            // FinancialIndicator 4 : Montant maximum prélevable (seuil de 3 mois par défaut)
            if (company.getCashBalance() != null &&
                    company.getMonthlyExpenses() != null) {

                BigDecimal maxWithdrawableAmount = calculateMaximumWithdrawableAmount(
                        company.getCashBalance(),
                        company.getMonthlyExpenses(),
                        3 // Seuil de sécurité : 3 mois de charges
                );
                indicateurs.put("maxWithdrawableAmount", maxWithdrawableAmount);
            }

            // Métadonnées
            indicateurs.put("companyId", company.getId());
            indicateurs.put("calculeLe", LocalDate.now());

            log.info("Tous les indicateurs calculés : {}", indicateurs.size());

        } catch (Exception e) {
            log.error("Erreur lors du calcul des indicateurs pour company {}: {}",
                    company.getId(), e.getMessage());
            throw new RuntimeException("Erreur lors du calcul des indicateurs", e);
        }

        return indicateurs;
    }

    @Override
    public boolean hasCompleteFinancialData(Company company) {
        log.debug("Vérification données complètes pour company ID: {}", company.getId());

        if (company == null) {
            log.warn("Company null, données incomplètes");
            return false;
        }

        // Vérifier que toutes les données essentielles sont présentes
        boolean chiffreAffairesOK = company.getMonthlyRevenue() != null
                && company.getMonthlyRevenue().compareTo(BigDecimal.ZERO) > 0;

        boolean chargesOK = company.getMonthlyExpenses() != null
                && company.getMonthlyExpenses().compareTo(BigDecimal.ZERO) > 0;

        boolean cashBalanceOK = company.getCashBalance() != null;

        boolean compteCourantOK = company.getDirectorCurrentAccountBalance() != null;

        boolean complete = chiffreAffairesOK && chargesOK && cashBalanceOK && compteCourantOK;

        if (!complete) {
            log.warn("Données incomplètes pour company {} - CA:{}, Charges:{}, Tréso:{}, CC:{}",
                    company.getId(), chiffreAffairesOK, chargesOK, cashBalanceOK, compteCourantOK);
        }

        return complete;
    }

    @Override
    public BigDecimal calculateMaximumWithdrawableAmount(
            BigDecimal cashBalance,
            BigDecimal monthlyExpenses,
            int seuilSecurite) {

        log.debug("Calcul montant max prélevable - Tréso: {}, Charges: {}, Seuil: {} mois",
                cashBalance, monthlyExpenses, seuilSecurite);

        // Validations
        if (cashBalance == null || monthlyExpenses == null) {
            throw new IllegalArgumentException("La trésorerie et les charges mensuelles ne peuvent pas être null");
        }

        if (monthlyExpenses.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Les charges mensuelles doivent être strictement positives");
        }

        if (seuilSecurite < 0) {
            throw new IllegalArgumentException("Le seuil de sécurité ne peut pas être négatif");
        }

        // Calcul de la trésorerie minimale à conserver (seuil de sécurité)
        BigDecimal cashBalanceSeuil = monthlyExpenses.multiply(BigDecimal.valueOf(seuilSecurite));

        // Montant maximum prélevable = trésorerie actuelle - trésorerie de sécurité
        BigDecimal montantMax = cashBalance.subtract(cashBalanceSeuil);

        // Si le résultat est négatif, on ne peut rien prélever
        if (montantMax.compareTo(BigDecimal.ZERO) < 0) {
            log.debug("Montant max prélevable négatif, retour 0");
            return BigDecimal.ZERO;
        }

        // Arrondir à 2 décimales
        BigDecimal montantMaxArrondi = montantMax.setScale(2, RoundingMode.HALF_UP);

        log.debug("Montant maximum prélevable calculé : {}", montantMaxArrondi);

        return montantMaxArrondi;
    }
}
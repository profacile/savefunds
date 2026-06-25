package be.profacile.savefunds.domain.service;

import be.profacile.savefunds.domain.entity.Entreprise;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Service de calcul des indicateurs financiers
 */
public interface IndicateurService {

    /**
     * Calcule le ratio CA/Charges
     *
     * @param chiffreAffaires Chiffre d'affaires mensuel
     * @param charges Charges mensuelles
     * @return Ratio (ex: 1.67 = CA est 1.67x les charges)
     */
    BigDecimal calculerRatioCACharges(BigDecimal chiffreAffaires, BigDecimal charges);

    /**
     * Calcule la trésorerie en nombre de mois
     *
     * @param tresorerie Trésorerie disponible
     * @param chargesMensuelles Charges mensuelles
     * @return Nombre de mois de trésorerie (ex: 3.5 mois)
     */
    BigDecimal calculerTresorerieEnMois(BigDecimal tresorerie, BigDecimal chargesMensuelles);

    /**
     * Calcule la durée du compte courant débiteur
     *
     * @param soldeCompteCourant Solde actuel
     * @param historiqueDebuts Liste des dates de début débiteur (optionnel pour MVP)
     * @return Nombre de jours consécutifs en négatif
     */
    int calculerDureeCompteCourantDebiteur(BigDecimal soldeCompteCourant, LocalDate dateDebutDebiteur);

    /**
     * Calcule tous les indicateurs d'une entreprise
     *
     * @param entreprise Entreprise à analyser
     * @return Map<String, Object> avec tous les indicateurs
     */
    java.util.Map<String, Object> calculerTousIndicateurs(Entreprise entreprise);

    /**
     * Vérifie si les données financières sont complètes
     *
     * @param entreprise Entreprise à vérifier
     * @return true si toutes les données nécessaires sont présentes
     */
    boolean donneesCompletesPresentes(Entreprise entreprise);

    /**
     * Calcule le montant maximum prélevable
     *
     * @param tresorerie Trésorerie actuelle
     * @param chargesMensuelles Charges mensuelles
     * @param seuilSecurite Seuil de sécurité en mois (ex: 3 mois)
     * @return Montant maximum pouvant être prélevé sans danger
     */
    BigDecimal calculerMontantMaximumPrelevable(
            BigDecimal tresorerie,
            BigDecimal chargesMensuelles,
            int seuilSecurite
    );
}
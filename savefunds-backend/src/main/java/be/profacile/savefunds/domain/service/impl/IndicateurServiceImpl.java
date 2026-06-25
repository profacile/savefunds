package be.profacile.savefunds.domain.service.impl;

import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.service.IndicateurService;
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
public class IndicateurServiceImpl implements IndicateurService {

    // Constantes métier
    private static final int PRECISION_RATIO = 2;  // 2 décimales pour les ratios
    private static final int PRECISION_MOIS = 2;   // 2 décimales pour les mois

    @Override
    public BigDecimal calculerRatioCACharges(BigDecimal chiffreAffaires, BigDecimal charges) {
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
    public BigDecimal calculerTresorerieEnMois(BigDecimal tresorerie, BigDecimal chargesMensuelles) {
        log.debug("Calcul trésorerie en mois - Tréso: {}, Charges: {}", tresorerie, chargesMensuelles);

        // Validations
        if (tresorerie == null || chargesMensuelles == null) {
            throw new IllegalArgumentException("La trésorerie et les charges mensuelles ne peuvent pas être null");
        }

        if (chargesMensuelles.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Les charges mensuelles doivent être strictement positives");
        }

        // Si trésorerie négative, retourner 0 (pas de mois de couverture)
        if (tresorerie.compareTo(BigDecimal.ZERO) < 0) {
            log.debug("Trésorerie négative, retour 0 mois");
            return BigDecimal.ZERO;
        }

        // Calcul : trésorerie / charges mensuelles
        BigDecimal mois = tresorerie.divide(chargesMensuelles, PRECISION_MOIS, RoundingMode.HALF_UP);

        log.debug("Trésorerie en mois calculée : {}", mois);

        return mois;
    }

    @Override
    public int calculerDureeCompteCourantDebiteur(BigDecimal soldeCompteCourant, LocalDate dateDebutDebiteur) {
        log.debug("Calcul durée CC débiteur - Solde: {}, Début: {}", soldeCompteCourant, dateDebutDebiteur);

        // Si le compte n'est pas débiteur (solde >= 0), retourner 0
        if (soldeCompteCourant == null || soldeCompteCourant.compareTo(BigDecimal.ZERO) >= 0) {
            log.debug("Compte courant non débiteur, retour 0 jours");
            return 0;
        }

        // Si pas de date de début de débit mais solde négatif → ROUGE par défaut
        if (dateDebutDebiteur == null) {
            log.debug("Solde débiteur mais pas de date de début → 31 jours par défaut");
            return 31;
        }

        // Calculer le nombre de jours depuis le début du débit
        long jours = ChronoUnit.DAYS.between(dateDebutDebiteur, LocalDate.now());

        // Assurer que le résultat n'est pas négatif
        // Minimum 1 jour si solde négatif
        int duree = (int) Math.max(1, jours);

        log.debug("Durée compte courant débiteur : {} jours", duree);

        return duree;
    }

    @Override
    public Map<String, Object> calculerTousIndicateurs(Entreprise entreprise) {
        log.info("Calcul de tous les indicateurs pour entreprise ID: {}", entreprise.getId());

        if (entreprise == null) {
            throw new IllegalArgumentException("L'entreprise ne peut pas être null");
        }

        Map<String, Object> indicateurs = new HashMap<>();

        try {
            // Indicateur 1 : Ratio CA/Charges
            if (entreprise.getChiffreAffairesMensuel() != null &&
                    entreprise.getChargesMensuelles() != null) {

                BigDecimal ratioCACharges = calculerRatioCACharges(
                        entreprise.getChiffreAffairesMensuel(),
                        entreprise.getChargesMensuelles()
                );
                indicateurs.put("ratioCACharges", ratioCACharges);
            }

            // Indicateur 2 : Trésorerie en mois
            if (entreprise.getTresorerie() != null &&
                    entreprise.getChargesMensuelles() != null) {

                BigDecimal tresorerieEnMois = calculerTresorerieEnMois(
                        entreprise.getTresorerie(),
                        entreprise.getChargesMensuelles()
                );
                indicateurs.put("tresorerieEnMois", tresorerieEnMois);
            }

            // Indicateur 3 : Durée compte courant débiteur
            int dureeCompteCourantDebiteur = calculerDureeCompteCourantDebiteur(
                    entreprise.getSoldeCompteCourant(),
                    entreprise.getDateDebutDebiteurCC()
            );
            indicateurs.put("dureeCompteCourantDebiteur", dureeCompteCourantDebiteur);

            // Indicateur 4 : Montant maximum prélevable (seuil de 3 mois par défaut)
            if (entreprise.getTresorerie() != null &&
                    entreprise.getChargesMensuelles() != null) {

                BigDecimal montantMaxPrelevable = calculerMontantMaximumPrelevable(
                        entreprise.getTresorerie(),
                        entreprise.getChargesMensuelles(),
                        3 // Seuil de sécurité : 3 mois de charges
                );
                indicateurs.put("montantMaxPrelevable", montantMaxPrelevable);
            }

            // Métadonnées
            indicateurs.put("entrepriseId", entreprise.getId());
            indicateurs.put("calculeLe", LocalDate.now());

            log.info("Tous les indicateurs calculés : {}", indicateurs.size());

        } catch (Exception e) {
            log.error("Erreur lors du calcul des indicateurs pour entreprise {}: {}",
                    entreprise.getId(), e.getMessage());
            throw new RuntimeException("Erreur lors du calcul des indicateurs", e);
        }

        return indicateurs;
    }

    @Override
    public boolean donneesCompletesPresentes(Entreprise entreprise) {
        log.debug("Vérification données complètes pour entreprise ID: {}", entreprise.getId());

        if (entreprise == null) {
            log.warn("Entreprise null, données incomplètes");
            return false;
        }

        // Vérifier que toutes les données essentielles sont présentes
        boolean chiffreAffairesOK = entreprise.getChiffreAffairesMensuel() != null
                && entreprise.getChiffreAffairesMensuel().compareTo(BigDecimal.ZERO) > 0;

        boolean chargesOK = entreprise.getChargesMensuelles() != null
                && entreprise.getChargesMensuelles().compareTo(BigDecimal.ZERO) > 0;

        boolean tresorerieOK = entreprise.getTresorerie() != null;

        boolean compteCourantOK = entreprise.getSoldeCompteCourant() != null;

        boolean complete = chiffreAffairesOK && chargesOK && tresorerieOK && compteCourantOK;

        if (!complete) {
            log.warn("Données incomplètes pour entreprise {} - CA:{}, Charges:{}, Tréso:{}, CC:{}",
                    entreprise.getId(), chiffreAffairesOK, chargesOK, tresorerieOK, compteCourantOK);
        }

        return complete;
    }

    @Override
    public BigDecimal calculerMontantMaximumPrelevable(
            BigDecimal tresorerie,
            BigDecimal chargesMensuelles,
            int seuilSecurite) {

        log.debug("Calcul montant max prélevable - Tréso: {}, Charges: {}, Seuil: {} mois",
                tresorerie, chargesMensuelles, seuilSecurite);

        // Validations
        if (tresorerie == null || chargesMensuelles == null) {
            throw new IllegalArgumentException("La trésorerie et les charges mensuelles ne peuvent pas être null");
        }

        if (chargesMensuelles.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Les charges mensuelles doivent être strictement positives");
        }

        if (seuilSecurite < 0) {
            throw new IllegalArgumentException("Le seuil de sécurité ne peut pas être négatif");
        }

        // Calcul de la trésorerie minimale à conserver (seuil de sécurité)
        BigDecimal tresorerieSeuil = chargesMensuelles.multiply(BigDecimal.valueOf(seuilSecurite));

        // Montant maximum prélevable = trésorerie actuelle - trésorerie de sécurité
        BigDecimal montantMax = tresorerie.subtract(tresorerieSeuil);

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
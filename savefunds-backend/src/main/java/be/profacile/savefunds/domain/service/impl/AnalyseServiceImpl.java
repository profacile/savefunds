package be.profacile.savefunds.domain.service.impl;

import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.domain.entity.AnalysePrelevement;
import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.entity.ResultatAnalyse;
import be.profacile.savefunds.domain.enums.Decision;
import be.profacile.savefunds.domain.enums.StatutAnalyse;
import be.profacile.savefunds.domain.repository.AnalyseRepository;
import be.profacile.savefunds.domain.repository.EntrepriseRepository;
import be.profacile.savefunds.domain.repository.ResultatAnalyseRepository;
import be.profacile.savefunds.domain.service.GrilleTricoloreService;
import be.profacile.savefunds.domain.service.IndicateurService;
import be.profacile.savefunds.service.AnalyseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implémentation du service d'analyse de prélèvement.
 *
 * Utilise :
 * - IndicateurService pour calculer les scores
 * - GrilleTricoloreService pour calculer les décisions
 * - ResultatAnalyseRepository pour sauvegarder les résultats
 *
 * @author Profacile SRL
 * @version 2.0.0
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AnalyseServiceImpl implements AnalyseService {

    private final AnalyseRepository analyseRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final ResultatAnalyseRepository resultatAnalyseRepository;
    private final IndicateurService indicateurService;
    private final GrilleTricoloreService grilleTricoloreService;

    @Override
    @Transactional(readOnly = true)
    public Optional<AnalysePrelevement> findById(Long id) {
        log.debug("Recherche de l'analyse avec id : {}", id);
        return analyseRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalysePrelevement> findByEntrepriseId(Long entrepriseId) {
        log.debug("Recherche des analyses pour l'entreprise : {}", entrepriseId);
        return analyseRepository.findByEntreprise_IdOrderByCreatedAtDesc(entrepriseId);
    }

    @Override
    public AnalysePrelevement create(Long entrepriseId, BigDecimal montantSouhaite) {
        log.info("Création d'une nouvelle analyse pour entreprise {} - montant: {}",
                entrepriseId, montantSouhaite);

        // ===== VALIDATION =====

        if (entrepriseId == null) {
            throw new IllegalArgumentException("L'entrepriseId est obligatoire");
        }

        if (montantSouhaite == null || montantSouhaite.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à zéro");
        }

        // ===== VÉRIFICATION ENTREPRISE =====

        Entreprise entreprise = entrepriseRepository.findById(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Entreprise non trouvée avec l'ID: " + entrepriseId));

        // ===== CRÉATION DE L'ANALYSE =====

        AnalysePrelevement analyse = AnalysePrelevement.builder()
                .entreprise(entreprise)
                .montantSouhaite(montantSouhaite)
                .statut(StatutAnalyse.EN_ATTENTE)
                .build();

        AnalysePrelevement saved = analyseRepository.save(analyse);

        log.info("Analyse créée avec succès - ID: {}", saved.getId());

        return saved;
    }

    @Override
    public AnalysePrelevement update(Long id, AnalysePrelevement updates) {
        log.info("Mise à jour de l'analyse avec id : {}", id);

        // ===== RÉCUPÉRATION =====

        AnalysePrelevement existing = analyseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Analyse non trouvée avec l'ID: " + id));

        // ===== MISE À JOUR FIELD-BY-FIELD =====

        if (updates.getMontantSouhaite() != null) {
            if (updates.getMontantSouhaite().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Le montant doit être supérieur à zéro");
            }
            existing.setMontantSouhaite(updates.getMontantSouhaite());
        }

        if (updates.getStatut() != null) {
            existing.setStatut(updates.getStatut());
        }

        AnalysePrelevement saved = analyseRepository.save(existing);

        log.info("Analyse mise à jour avec succès - ID: {}", saved.getId());

        return saved;
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression de l'analyse avec id : {}", id);

        AnalysePrelevement analyse = analyseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Analyse non trouvée avec l'ID: " + id));

        analyseRepository.delete(analyse);

        log.info("Analyse supprimée avec succès - ID: {}", id);
    }

    @Override
    public ResultatAnalyse effectuerAnalyse(Long analyseId) {
        log.info("═══════════════════════════════════════════════════");
        log.info("Début de l'analyse pour analyseId : {}", analyseId);
        log.info("═══════════════════════════════════════════════════");

        // ===== 1. RÉCUPÉRATION DES DONNÉES =====

        AnalysePrelevement analyse = analyseRepository.findById(analyseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Analyse non trouvée avec l'ID: " + analyseId));

        if (analyse.getStatut() == StatutAnalyse.TERMINEE) {
            throw new IllegalStateException(
                    "L'analyse a déjà été effectuée. Consultez le résultat existant.");
        }

        Entreprise entreprise = entrepriseRepository.findById(analyse.getEntreprise().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Entreprise non trouvée avec l'ID: " + analyse.getEntreprise().getId()));

        log.info("Entreprise: {} (ID: {})",
                entreprise.getRaisonSociale(), entreprise.getId());
        log.info("Montant souhaité: {}", analyse.getMontantSouhaite());

        // ===== 2. VÉRIFICATION DES DONNÉES =====

        if (!indicateurService.donneesCompletesPresentes(entreprise)) {
            throw new IllegalStateException(
                    "Données financières incomplètes pour l'entreprise. " +
                            "Veuillez renseigner : CA, charges, trésorerie, compte courant.");
        }

        // ===== 3. CALCUL DES INDICATEURS =====

        Map<String, Object> indicateurs = indicateurService.calculerTousIndicateurs(entreprise);

        BigDecimal tresorerieEnMois = (BigDecimal) indicateurs.get("tresorerieEnMois");
        BigDecimal ratioCACharges = (BigDecimal) indicateurs.get("ratioCACharges");
        Integer dureeCompteCourantDebiteur = (Integer) indicateurs.get("dureeCompteCourantDebiteur");

        log.info("───────────────────────────────────────────────────");
        log.info("INDICATEURS CALCULÉS:");
        log.info("  • Trésorerie: {} mois de charges", tresorerieEnMois);
        log.info("  • Ratio CA/Charges: {}", ratioCACharges);
        log.info("  • Compte courant débiteur: {} jours", dureeCompteCourantDebiteur);
        log.info("───────────────────────────────────────────────────");

        // ===== 3.5 VÉRIFICATION DU MONTANT SOUHAITE =====

        BigDecimal montantMax = (BigDecimal) indicateurs.get("montantMaxPrelevable");
        BigDecimal montantSouhaite = analyse.getMontantSouhaite();

        Decision decisionMontant;
        if (montantMax.compareTo(BigDecimal.ZERO) <= 0) {
            decisionMontant = Decision.ROUGE; // trésorerie < 3 mois de charges
        } else if (montantSouhaite.compareTo(montantMax) > 0) {
            decisionMontant = Decision.ROUGE; // montant demandé dépasse le prélevable
        } else {
            BigDecimal ratio = montantSouhaite.divide(montantMax, 2, RoundingMode.HALF_UP);
            decisionMontant = ratio.compareTo(new BigDecimal("0.75")) > 0
                    ? Decision.ORANGE
                    : Decision.VERT;
        }

        log.info("  • Montant souhaité: {} / Max prélevable: {} → {}",
                montantSouhaite, montantMax, decisionMontant);

        // ===== 4. CALCUL DES DÉCISIONS (GRILLE TRICOLORE) =====

        Decision decisionTresorerie = grilleTricoloreService
                .calculerDecisionTresorerie(tresorerieEnMois);

        Decision decisionRatio = grilleTricoloreService
                .calculerDecisionRatioCACharges(ratioCACharges);

        Decision decisionCompteCourant = grilleTricoloreService
                .calculerDecisionCompteCourant(dureeCompteCourantDebiteur);

        Decision decisionGlobale = grilleTricoloreService.calculerDecisionGlobale(
                decisionTresorerie,
                decisionRatio,
                decisionCompteCourant,
                decisionMontant);

        log.info("DÉCISIONS CALCULÉES:");
        log.info("  • Trésorerie: {}", decisionTresorerie);
        log.info("  • Ratio CA/Charges: {}", decisionRatio);
        log.info("  • Compte courant: {}", decisionCompteCourant);
        log.info("  ► DÉCISION GLOBALE: {}", decisionGlobale);
        log.info("───────────────────────────────────────────────────");

        // ===== 5. GÉNÉRATION DES RECOMMANDATIONS =====

        String recommandationTresorerie = grilleTricoloreService
                .genererRecommandation(decisionTresorerie, "tresorerie");

        String recommandationRatio = grilleTricoloreService
                .genererRecommandation(decisionRatio, "ratio");

        String recommandationCompteCourant = grilleTricoloreService
                .genererRecommandation(decisionCompteCourant, "compte_courant");

        String recommandationGlobale = genererRecommandationGlobale(decisionGlobale, montantSouhaite, montantMax);

        // ===== 6. GÉNÉRATION DES DÉTAILS =====

        String detailsTresorerie = genererDetailsTresorerie(
                tresorerieEnMois, decisionTresorerie);

        String detailsRatio = genererDetailsRatio(
                ratioCACharges, decisionRatio);

        String detailsCompteCourant = genererDetailsCompteCourant(
                dureeCompteCourantDebiteur, decisionCompteCourant);

        String detailsGlobale = genererDetailsGlobale(
                decisionGlobale, decisionTresorerie, decisionRatio, decisionCompteCourant);

        // ===== 7. CRÉATION DU RÉSULTAT =====

        ResultatAnalyse resultat = ResultatAnalyse.builder()
                .analyse(analyse)
                .decisionGlobale(decisionGlobale)
                .detailsDecisionGlobale(detailsGlobale)
                .recommandationGlobale(recommandationGlobale)
                .scoreTresorerie(tresorerieEnMois)
                .scoreRatioCACharges(ratioCACharges)
                .scoreCompteCourantDebiteur(dureeCompteCourantDebiteur)
                .montantMaxPrelevable(montantMax)
                .decisionTresorerie(decisionTresorerie)
                .decisionRatioCACharges(decisionRatio)
                .decisionCompteCourant(decisionCompteCourant)
                .detailsTresorerie(detailsTresorerie)
                .detailsRatioCACharges(detailsRatio)
                .detailsCompteCourant(detailsCompteCourant)
                .recommandationTresorerie(recommandationTresorerie)
                .recommandationRatioCACharges(recommandationRatio)
                .recommandationCompteCourant(recommandationCompteCourant)
                .build();

        resultat = resultatAnalyseRepository.save(resultat);

        log.info("Résultat d'analyse créé - ID: {}", resultat.getId());

        // ===== 8. MISE À JOUR DE L'ANALYSE =====

        analyse.setStatut(StatutAnalyse.TERMINEE);
        analyseRepository.save(analyse);

        log.info("═══════════════════════════════════════════════════");
        log.info("Analyse terminée avec succès !");
        log.info("  • Analyse ID: {}", analyseId);
        log.info("  • Résultat ID: {}", resultat.getId());
        log.info("  • Décision: {}", decisionGlobale);
        log.info("═══════════════════════════════════════════════════");

        return resultat;
    }

    // ==================== MÉTHODES PRIVÉES (GÉNÉRATION DE TEXTE) ====================

    /**
     * Génère la recommandation globale basée sur la décision finale et le montant.
     */
    private String genererRecommandationGlobale(Decision decision, BigDecimal montantSouhaite, BigDecimal montantMax) {
        return switch (decision) {
            case VERT ->
                    "✅ Situation financière saine. Le prélèvement de " + formatMontant(montantSouhaite) +
                            " peut être effectué sans risque majeur. Continuez à maintenir vos indicateurs au vert.";

            case ORANGE -> {
                if (montantSouhaite.compareTo(montantMax) > 0) {
                    yield "⚠️ Situation financière acceptable, mais le montant demandé (" + formatMontant(montantSouhaite) +
                            ") dépasse le maximum recommandé (" + formatMontant(montantMax) +
                            "). Réduisez le montant du prélèvement.";
                }
                yield "⚠️ Situation nécessitant une vigilance. Le prélèvement est possible mais restez prudent. " +
                        "Surveillez l'évolution de vos indicateurs et envisagez des mesures correctives.";
            }

            case ROUGE -> {
                if (montantMax.compareTo(BigDecimal.ZERO) <= 0) {
                    yield "🛑 Trésorerie insuffisante. Aucun prélèvement n'est possible actuellement " +
                            "sans mettre l'entreprise en danger. Consultez votre comptable.";
                }
                if (montantSouhaite.compareTo(montantMax) > 0) {
                    yield "🛑 Situation financière saine, mais le montant demandé (" + formatMontant(montantSouhaite) +
                            ") dépasse le maximum prélevable (" + formatMontant(montantMax) +
                            "). Le prélèvement est déconseillé dans ces conditions.";
                }
                yield "🛑 Situation critique. Le prélèvement est fortement déconseillé et pourrait mettre " +
                        "l'entreprise en danger. Consultez votre comptable avant toute décision.";
            }
        };
    }

    private String formatMontant(BigDecimal montant) {
        return String.format("%,.2f €", montant);
    }

    /**
     * Génère les détails pour le critère trésorerie.
     */
    private String genererDetailsTresorerie(BigDecimal tresoMois, Decision decision) {
        String base = String.format("Trésorerie actuelle: %.2f mois de charges.", tresoMois);

        String complement = switch (decision) {
            case VERT -> " Excellente réserve financière, vous êtes bien protégé contre les imprévus.";
            case ORANGE -> " Réserve acceptable mais limitée. Visez au moins 3 mois pour plus de sécurité.";
            case ROUGE -> " Réserve critique ! Risque élevé de difficulté en cas d'imprévu.";
        };

        return base + complement;
    }

    /**
     * Génère les détails pour le critère ratio CA/Charges.
     */
    private String genererDetailsRatio(BigDecimal ratio, Decision decision) {
        BigDecimal marge = ratio.subtract(BigDecimal.ONE)
                .multiply(new BigDecimal("100"));

        String base = String.format("Ratio CA/Charges: %.2f (marge: %.1f%%).", ratio, marge);

        String complement = switch (decision) {
            case VERT -> " Excellente rentabilité, votre entreprise génère de bons profits.";
            case ORANGE -> " Rentabilité fragile. Améliorez votre efficacité ou vos prix.";
            case ROUGE -> " Situation déficitaire ! Vos charges dépassent vos revenus.";
        };

        return base + complement;
    }

    /**
     * Génère les détails pour le critère compte courant.
     */
    private String genererDetailsCompteCourant(int duree, Decision decision) {
        if (duree == 0) {
            return "Compte courant créditeur ou à l'équilibre. Situation saine, aucune dette envers l'entreprise.";
        }

        String base = String.format("Compte courant débiteur depuis %d jours.", duree);

        String complement = switch (decision) {
            case ORANGE -> " Restez vigilant et remboursez rapidement pour éviter l'accumulation.";
            case ROUGE -> " Durée excessive ! Risque de blocage bancaire et de redressement fiscal.";
            default -> "";
        };

        return base + complement;
    }

    /**
     * Génère les détails pour la décision globale.
     */
    private String genererDetailsGlobale(Decision globale, Decision treso, Decision ratio, Decision cc) {
        long nbRouges = countDecision(Decision.ROUGE, treso, ratio, cc);
        long nbOranges = countDecision(Decision.ORANGE, treso, ratio, cc);
        long nbVerts = countDecision(Decision.VERT, treso, ratio, cc);

        if (globale == Decision.VERT) {
            return String.format(
                    "Tous les indicateurs sont au vert (%d/3). Situation financière excellente.",
                    nbVerts);
        } else if (globale == Decision.ORANGE) {
            return String.format(
                    "%d indicateur(s) en ORANGE, %d en VERT. Vigilance recommandée sur les points faibles.",
                    nbOranges, nbVerts);
        } else {
            return String.format(
                    "%d indicateur(s) en ROUGE ! Situation critique nécessitant une action immédiate.",
                    nbRouges);
        }
    }

    /**
     * Compte le nombre de décisions d'un type donné.
     */
    private long countDecision(Decision target, Decision... decisions) {
        long count = 0;
        for (Decision d : decisions) {
            if (d == target) {
                count++;
            }
        }
        return count;
    }
}
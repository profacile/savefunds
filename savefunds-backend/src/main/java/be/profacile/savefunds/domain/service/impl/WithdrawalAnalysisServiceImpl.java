package be.profacile.savefunds.domain.service.impl;

import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.domain.entity.WithdrawalAnalysis;
import be.profacile.savefunds.domain.entity.Company;
import be.profacile.savefunds.domain.entity.AnalysisResult;
import be.profacile.savefunds.domain.enums.Decision;
import be.profacile.savefunds.domain.enums.AnalysisStatus;
import be.profacile.savefunds.domain.repository.WithdrawalAnalysisRepository;
import be.profacile.savefunds.domain.repository.CompanyRepository;
import be.profacile.savefunds.domain.repository.AnalysisResultRepository;
import be.profacile.savefunds.domain.service.TrafficLightDecisionService;
import be.profacile.savefunds.domain.service.FinancialIndicatorService;
import be.profacile.savefunds.service.WithdrawalAnalysisService;
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
 * Implémentation du service d'analysis de prélèvement.
 *
 * Utilise :
 * - FinancialIndicatorService pour calculer les scores
 * - TrafficLightDecisionService pour calculer les décisions
 * - AnalysisResultRepository pour sauvegarder les résultats
 *
 * @author Profacile SRL
 * @version 2.0.0
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WithdrawalAnalysisServiceImpl implements WithdrawalAnalysisService {

    private final WithdrawalAnalysisRepository withdrawalAnalysisRepository;
    private final CompanyRepository companyRepository;
    private final AnalysisResultRepository resultWithdrawalAnalysisRepository;
    private final FinancialIndicatorService financialIndicatorService;
    private final TrafficLightDecisionService grilleTricoloreService;

    @Override
    @Transactional(readOnly = true)
    public Optional<WithdrawalAnalysis> findById(Long id) {
        log.debug("Recherche de l'analysis avec id : {}", id);
        return withdrawalAnalysisRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WithdrawalAnalysis> findByCompanyId(Long companyId) {
        log.debug("Recherche des analyses pour l'company : {}", companyId);
        return withdrawalAnalysisRepository.findByCompany_IdOrderByCreatedAtDesc(companyId);
    }

    @Override
    public WithdrawalAnalysis create(Long companyId, BigDecimal requestedAmount) {
        log.info("Création d'une nouvelle analysis pour company {} - montant: {}",
                companyId, requestedAmount);

        // ===== VALIDATION =====

        if (companyId == null) {
            throw new IllegalArgumentException("L'companyId est obligatoire");
        }

        if (requestedAmount == null || requestedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à zéro");
        }

        // ===== VÉRIFICATION ENTREPRISE =====

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Company non trouvée avec l'ID: " + companyId));

        // ===== CRÉATION DE L'ANALYSE =====

        WithdrawalAnalysis analysis = WithdrawalAnalysis.builder()
                .company(company)
                .requestedAmount(requestedAmount)
                .status(AnalysisStatus.EN_ATTENTE)
                .build();

        WithdrawalAnalysis saved = withdrawalAnalysisRepository.save(analysis);

        log.info("Analyse créée avec succès - ID: {}", saved.getId());

        return saved;
    }

    @Override
    public WithdrawalAnalysis update(Long id, WithdrawalAnalysis updates) {
        log.info("Mise à jour de l'analysis avec id : {}", id);

        // ===== RÉCUPÉRATION =====

        WithdrawalAnalysis existing = withdrawalAnalysisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Analyse non trouvée avec l'ID: " + id));

        // ===== MISE À JOUR FIELD-BY-FIELD =====

        if (updates.getRequestedAmount() != null) {
            if (updates.getRequestedAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Le montant doit être supérieur à zéro");
            }
            existing.setRequestedAmount(updates.getRequestedAmount());
        }

        if (updates.getStatus() != null) {
            existing.setStatus(updates.getStatus());
        }

        WithdrawalAnalysis saved = withdrawalAnalysisRepository.save(existing);

        log.info("Analyse mise à jour avec succès - ID: {}", saved.getId());

        return saved;
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression de l'analysis avec id : {}", id);

        WithdrawalAnalysis analysis = withdrawalAnalysisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Analyse non trouvée avec l'ID: " + id));

        withdrawalAnalysisRepository.delete(analysis);

        log.info("Analyse supprimée avec succès - ID: {}", id);
    }

    @Override
    public AnalysisResult runAnalysis(Long analysisId) {
        log.info("═══════════════════════════════════════════════════");
        log.info("Début de l'analysis pour analysisId : {}", analysisId);
        log.info("═══════════════════════════════════════════════════");

        // ===== 1. RÉCUPÉRATION DES DONNÉES =====

        WithdrawalAnalysis analysis = withdrawalAnalysisRepository.findById(analysisId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Analyse non trouvée avec l'ID: " + analysisId));

        if (analysis.getStatus() == AnalysisStatus.TERMINEE) {
            throw new IllegalStateException(
                    "L'analysis a déjà été effectuée. Consultez le résultat existant.");
        }

        Company company = companyRepository.findById(analysis.getCompany().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Company non trouvée avec l'ID: " + analysis.getCompany().getId()));

        log.info("Company: {} (ID: {})",
                company.getLegalName(), company.getId());
        log.info("Montant souhaité: {}", analysis.getRequestedAmount());

        // ===== 2. VÉRIFICATION DES DONNÉES =====

        if (!financialIndicatorService.hasCompleteFinancialData(company)) {
            throw new IllegalStateException(
                    "Données financières incomplètes pour l'company. " +
                            "Veuillez renseigner : CA, charges, trésorerie, compte courant.");
        }

        // ===== 3. CALCUL DES INDICATEURS =====

        Map<String, Object> indicateurs = financialIndicatorService.calculateAllFinancialIndicators(company);

        BigDecimal cashCoverageMonths = (BigDecimal) indicateurs.get("cashCoverageMonths");
        BigDecimal ratioCACharges = (BigDecimal) indicateurs.get("ratioCACharges");
        Integer directorCurrentAccountDebtorDays = (Integer) indicateurs.get("directorCurrentAccountDebtorDays");

        log.info("───────────────────────────────────────────────────");
        log.info("INDICATEURS CALCULÉS:");
        log.info("  • Trésorerie: {} mois de charges", cashCoverageMonths);
        log.info("  • Ratio CA/Charges: {}", ratioCACharges);
        log.info("  • Compte courant débiteur: {} jours", directorCurrentAccountDebtorDays);
        log.info("───────────────────────────────────────────────────");

        // ===== 3.5 VÉRIFICATION DU MONTANT SOUHAITE =====

        BigDecimal montantMax = (BigDecimal) indicateurs.get("maxWithdrawableAmount");
        BigDecimal requestedAmount = analysis.getRequestedAmount();

        Decision decisionMontant;
        if (montantMax.compareTo(BigDecimal.ZERO) <= 0) {
            decisionMontant = Decision.ROUGE; // trésorerie < 3 mois de charges
        } else if (requestedAmount.compareTo(montantMax) > 0) {
            decisionMontant = Decision.ROUGE; // montant demandé dépasse le prélevable
        } else {
            BigDecimal ratio = requestedAmount.divide(montantMax, 2, RoundingMode.HALF_UP);
            decisionMontant = ratio.compareTo(new BigDecimal("0.75")) > 0
                    ? Decision.ORANGE
                    : Decision.VERT;
        }

        log.info("  • Montant souhaité: {} / Max prélevable: {} → {}",
                requestedAmount, montantMax, decisionMontant);

        // ===== 4. CALCUL DES DÉCISIONS (GRILLE TRICOLORE) =====

        Decision cashDecision = grilleTricoloreService
                .calculateCashDecision(cashCoverageMonths);

        Decision decisionRatio = grilleTricoloreService
                .calculateRevenueExpensesRatioDecision(ratioCACharges);

        Decision directorCurrentAccountDecision = grilleTricoloreService
                .calculateDirectorCurrentAccountDecision(directorCurrentAccountDebtorDays);

        Decision globalDecision = grilleTricoloreService.calculateGlobalDecision(
                cashDecision,
                decisionRatio,
                directorCurrentAccountDecision,
                decisionMontant);

        log.info("DÉCISIONS CALCULÉES:");
        log.info("  • Trésorerie: {}", cashDecision);
        log.info("  • Ratio CA/Charges: {}", decisionRatio);
        log.info("  • Compte courant: {}", directorCurrentAccountDecision);
        log.info("  ► DÉCISION GLOBALE: {}", globalDecision);
        log.info("───────────────────────────────────────────────────");

        // ===== 5. GÉNÉRATION DES RECOMMANDATIONS =====

        String cashRecommendation = grilleTricoloreService
                .generateRecommendation(cashDecision, "cashBalance");

        String recommendationRatio = grilleTricoloreService
                .generateRecommendation(decisionRatio, "ratio");

        String directorCurrentAccountRecommendation = grilleTricoloreService
                .generateRecommendation(directorCurrentAccountDecision, "compte_courant");

        String globalRecommendation = generateRecommendationGlobale(globalDecision, requestedAmount, montantMax);

        // ===== 6. GÉNÉRATION DES DÉTAILS =====

        String cashDetails = generateCashDetails(
                cashCoverageMonths, cashDecision);

        String detailsRatio = generateRatioDetails(
                ratioCACharges, decisionRatio);

        String directorCurrentAccountDetails = generateDirectorCurrentAccountDetails(
                directorCurrentAccountDebtorDays, directorCurrentAccountDecision);

        String detailsGlobale = generateGlobalDetails(
                globalDecision, cashDecision, decisionRatio, directorCurrentAccountDecision);

        // ===== 7. CRÉATION DU RÉSULTAT =====

        AnalysisResult result = AnalysisResult.builder()
                .analysis(analysis)
                .globalDecision(globalDecision)
                .globalDecisionDetails(detailsGlobale)
                .globalRecommendation(globalRecommendation)
                .cashScore(cashCoverageMonths)
                .revenueExpensesRatioScore(ratioCACharges)
                .directorCurrentAccountScore(directorCurrentAccountDebtorDays)
                .maxWithdrawableAmount(montantMax)
                .cashDecision(cashDecision)
                .revenueExpensesRatioDecision(decisionRatio)
                .directorCurrentAccountDecision(directorCurrentAccountDecision)
                .cashDetails(cashDetails)
                .revenueExpensesRatioDetails(detailsRatio)
                .directorCurrentAccountDetails(directorCurrentAccountDetails)
                .cashRecommendation(cashRecommendation)
                .revenueExpensesRatioRecommendation(recommendationRatio)
                .directorCurrentAccountRecommendation(directorCurrentAccountRecommendation)
                .build();

        result = resultWithdrawalAnalysisRepository.save(result);

        log.info("Résultat d'analysis créé - ID: {}", result.getId());

        // ===== 8. MISE À JOUR DE L'ANALYSE =====

        analysis.setStatus(AnalysisStatus.TERMINEE);
        withdrawalAnalysisRepository.save(analysis);

        log.info("═══════════════════════════════════════════════════");
        log.info("Analyse terminée avec succès !");
        log.info("  • Analyse ID: {}", analysisId);
        log.info("  • Résultat ID: {}", result.getId());
        log.info("  • Décision: {}", globalDecision);
        log.info("═══════════════════════════════════════════════════");

        return result;
    }

    // ==================== MÉTHODES PRIVÉES (GÉNÉRATION DE TEXTE) ====================

    /**
     * Génère la recommendation globale basée sur la décision finale et le montant.
     */
    private String generateRecommendationGlobale(Decision decision, BigDecimal requestedAmount, BigDecimal montantMax) {
        return switch (decision) {
            case VERT ->
                    "✅ Situation financière saine. Le prélèvement de " + formatMontant(requestedAmount) +
                            " peut être effectué sans risque majeur. Continuez à maintenir vos indicateurs au vert.";

            case ORANGE -> {
                if (requestedAmount.compareTo(montantMax) > 0) {
                    yield "⚠️ Situation financière acceptable, mais le montant demandé (" + formatMontant(requestedAmount) +
                            ") dépasse le maximum recommandé (" + formatMontant(montantMax) +
                            "). Réduisez le montant du prélèvement.";
                }
                yield "⚠️ Situation nécessitant une vigilance. Le prélèvement est possible mais restez prudent. " +
                        "Surveillez l'évolution de vos indicateurs et envisagez des mesures correctives.";
            }

            case ROUGE -> {
                if (montantMax.compareTo(BigDecimal.ZERO) <= 0) {
                    yield "🛑 Trésorerie insuffisante. Aucun prélèvement n'est possible actuellement " +
                            "sans mettre l'company en danger. Consultez votre comptable.";
                }
                if (requestedAmount.compareTo(montantMax) > 0) {
                    yield "🛑 Situation financière saine, mais le montant demandé (" + formatMontant(requestedAmount) +
                            ") dépasse le maximum prélevable (" + formatMontant(montantMax) +
                            "). Le prélèvement est déconseillé dans ces conditions.";
                }
                yield "🛑 Situation critique. Le prélèvement est fortement déconseillé et pourrait mettre " +
                        "l'company en danger. Consultez votre comptable avant toute décision.";
            }
        };
    }

    private String formatMontant(BigDecimal montant) {
        return String.format("%,.2f €", montant);
    }

    /**
     * Génère les détails pour le critère trésorerie.
     */
    private String generateCashDetails(BigDecimal tresoMois, Decision decision) {
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
    private String generateRatioDetails(BigDecimal ratio, Decision decision) {
        BigDecimal marge = ratio.subtract(BigDecimal.ONE)
                .multiply(new BigDecimal("100"));

        String base = String.format("Ratio CA/Charges: %.2f (marge: %.1f%%).", ratio, marge);

        String complement = switch (decision) {
            case VERT -> " Excellente rentabilité, votre company génère de bons profits.";
            case ORANGE -> " Rentabilité fragile. Améliorez votre efficacité ou vos prix.";
            case ROUGE -> " Situation déficitaire ! Vos charges dépassent vos revenus.";
        };

        return base + complement;
    }

    /**
     * Génère les détails pour le critère compte courant.
     */
    private String generateDirectorCurrentAccountDetails(int duree, Decision decision) {
        if (duree == 0) {
            return "Compte courant créditeur ou à l'équilibre. Situation saine, aucune dette envers l'company.";
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
    private String generateGlobalDetails(Decision globale, Decision treso, Decision ratio, Decision cc) {
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
     * Compte le lastNamebre de décisions d'un type donné.
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
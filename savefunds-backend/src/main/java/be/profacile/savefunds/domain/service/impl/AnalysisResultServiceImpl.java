package be.profacile.savefunds.domain.service.impl;

import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.domain.entity.AnalysisResult;
import be.profacile.savefunds.domain.repository.AnalysisResultRepository;
import be.profacile.savefunds.domain.service.AnalysisResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implémentation du service de gestion des résultats d'analysis.
 *
 * Règle métier : Une analysis a un seul résultat détaillé (relation 1-1).
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AnalysisResultServiceImpl implements AnalysisResultService {

    private final AnalysisResultRepository analysisResultRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<AnalysisResult> findById(Long id) {
        log.debug("Recherche du résultat d'analysis avec ID : {}", id);
        return analysisResultRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AnalysisResult> findByAnalysis_Id(Long analysisId) {
        log.debug("Recherche du résultat pour l'analysis ID : {}", analysisId);
        return analysisResultRepository.findByAnalysis_Id(analysisId);
    }

    @Override
    public AnalysisResult create(AnalysisResult result) {
        log.info("Création d'un résultat d'analysis pour analysisId : {}",
                result != null && result.getAnalysis() != null ? result.getAnalysis().getId() : null);

        // ===== VALIDATIONS =====

        if (result == null) {
            throw new IllegalArgumentException("Le résultat d'analysis ne peut pas être null");
        }

        if (result.getAnalysis() == null || result.getAnalysis().getId() == null) {
            throw new IllegalArgumentException("L'analysis est obligatoire");
        }

        if (result.getGlobalDecision() == null) {
            throw new IllegalArgumentException("La décision globale est obligatoire");
        }

        // ===== VÉRIFICATION UNICITÉ =====

        Optional<AnalysisResult> existing = analysisResultRepository.findByAnalysis_Id(result.getAnalysis().getId());
        if (existing.isPresent()) {
            throw new IllegalArgumentException(
                    "Un résultat existe déjà pour l'analysis ID : " + result.getAnalysis().getId()
            );
        }

        // ===== SAUVEGARDE =====

        AnalysisResult saved = analysisResultRepository.save(result);

        log.info("Résultat d'analysis créé avec ID : {} pour analysis ID : {}",
                saved.getId(), saved.getAnalysis().getId());

        return saved;
    }

    @Override
    public AnalysisResult update(Long id, AnalysisResult result) {
        log.info("Mise à jour du résultat d'analysis ID : {}", id);

        // ===== RÉCUPÉRATION =====

        AnalysisResult existing = analysisResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AnalysisResult", "id", id));

        // ===== MISE À JOUR FIELD-BY-FIELD =====

        // Décision globale
        if (result.getGlobalDecision() != null) {
            existing.setGlobalDecision(result.getGlobalDecision());
        }
        if (result.getGlobalDecisionDetails() != null) {
            existing.setGlobalDecisionDetails(result.getGlobalDecisionDetails());
        }
        if (result.getGlobalRecommendation() != null) {
            existing.setGlobalRecommendation(result.getGlobalRecommendation());
        }

        // Scores
        if (result.getCashScore() != null) {
            existing.setCashScore(result.getCashScore());
        }
        if (result.getRevenueExpensesRatioScore() != null) {
            existing.setRevenueExpensesRatioScore(result.getRevenueExpensesRatioScore());
        }
        if (result.getDirectorCurrentAccountScore() != null) {
            existing.setDirectorCurrentAccountScore(result.getDirectorCurrentAccountScore());
        }

        // Décisions par critère
        if (result.getCashDecision() != null) {
            existing.setCashDecision(result.getCashDecision());
        }
        if (result.getRevenueExpensesRatioDecision() != null) {
            existing.setRevenueExpensesRatioDecision(result.getRevenueExpensesRatioDecision());
        }
        if (result.getDirectorCurrentAccountDecision() != null) {
            existing.setDirectorCurrentAccountDecision(result.getDirectorCurrentAccountDecision());
        }

        // Détails par critère
        if (result.getCashDetails() != null) {
            existing.setCashDetails(result.getCashDetails());
        }
        if (result.getRevenueExpensesRatioDetails() != null) {
            existing.setRevenueExpensesRatioDetails(result.getRevenueExpensesRatioDetails());
        }
        if (result.getDirectorCurrentAccountDetails() != null) {
            existing.setDirectorCurrentAccountDetails(result.getDirectorCurrentAccountDetails());
        }

        // Recommendations par critère
        if (result.getCashRecommendation() != null) {
            existing.setCashRecommendation(result.getCashRecommendation());
        }
        if (result.getRevenueExpensesRatioRecommendation() != null) {
            existing.setRevenueExpensesRatioRecommendation(result.getRevenueExpensesRatioRecommendation());
        }
        if (result.getDirectorCurrentAccountRecommendation() != null) {
            existing.setDirectorCurrentAccountRecommendation(result.getDirectorCurrentAccountRecommendation());
        }

        // ===== SAUVEGARDE =====

        AnalysisResult updated = analysisResultRepository.save(existing);

        log.info("Résultat d'analysis mis à jour : ID {}, Décision globale : {}",
                updated.getId(), updated.getGlobalDecision());

        return updated;
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression du résultat d'analysis ID : {}", id);

        if (!analysisResultRepository.existsById(id)) {
            throw new ResourceNotFoundException("AnalysisResult", "id", id);
        }

        analysisResultRepository.deleteById(id);

        log.info("Résultat d'analysis supprimé : ID {}", id);
    }
}
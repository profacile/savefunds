package be.profacile.savefunds.service;

import be.profacile.savefunds.domain.entity.WithdrawalAnalysis;
import be.profacile.savefunds.domain.entity.AnalysisResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des analyses de prélèvement.
 */
public interface WithdrawalAnalysisService {

    Optional<WithdrawalAnalysis> findById(Long id);

    List<WithdrawalAnalysis> findByCompanyId(Long companyId);

    WithdrawalAnalysis create(Long companyId, BigDecimal requestedAmount);

    WithdrawalAnalysis update(Long id, WithdrawalAnalysis analysis);

    void delete(Long id);

    /**
     * Lance l'analysis complète d'un prélèvement.
     * Crée le AnalysisResult et change le status à TERMINEE.
     *
     * @param analysisId ID de l'analysis à effectuer
     * @return Le AnalysisResult créé
     */
    AnalysisResult runAnalysis(Long analysisId);
}
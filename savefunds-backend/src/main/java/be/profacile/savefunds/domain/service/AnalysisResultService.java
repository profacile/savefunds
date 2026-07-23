package be.profacile.savefunds.domain.service;

import be.profacile.savefunds.domain.entity.AnalysisResult;

import java.util.Optional;

/**
 * Service de gestion des résultats d'analysis
 * Gère les détails de décision par critère
 */
public interface AnalysisResultService {
    
    /**
     * Trouve un résultat par ID
     */
    Optional<AnalysisResult> findById(Long id);
    
    /**
     * Trouve le résultat associé à une analysis
     * Une analysis a un seul résultat détaillé
     */
    Optional<AnalysisResult> findByAnalysis_Id(Long analysisId);
    
    /**
     * Crée un nouveau résultat d'analysis
     */
    AnalysisResult create(AnalysisResult result);
    
    /**
     * Met à jour un résultat existant
     */
    AnalysisResult update(Long id, AnalysisResult result);
    
    /**
     * Supprime un résultat
     */
    void delete(Long id);
}
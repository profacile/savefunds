package be.profacile.savefunds.domain.service;

import be.profacile.savefunds.domain.entity.ResultatAnalyse;

import java.util.Optional;

/**
 * Service de gestion des résultats d'analyse
 * Gère les détails de décision par critère
 */
public interface ResultatAnalyseService {
    
    /**
     * Trouve un résultat par ID
     */
    Optional<ResultatAnalyse> findById(Long id);
    
    /**
     * Trouve le résultat associé à une analyse
     * Une analyse a un seul résultat détaillé
     */
    Optional<ResultatAnalyse> findByAnalyse_Id(Long analyseId);
    
    /**
     * Crée un nouveau résultat d'analyse
     */
    ResultatAnalyse create(ResultatAnalyse resultat);
    
    /**
     * Met à jour un résultat existant
     */
    ResultatAnalyse update(Long id, ResultatAnalyse resultat);
    
    /**
     * Supprime un résultat
     */
    void delete(Long id);
}
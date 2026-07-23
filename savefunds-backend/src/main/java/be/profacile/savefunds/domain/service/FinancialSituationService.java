package be.profacile.savefunds.domain.service;

import be.profacile.savefunds.domain.entity.FinancialSituation;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des situations financières
 * Gère l'historique des snapshots financiers
 */
public interface FinancialSituationService {

    /**
     * Trouve une situation par ID
     */
    Optional<FinancialSituation> findById(Long id);

    /**
     * Trouve toutes les situations d'une company
     * Triées de la plus récente à la plus ancienne
     */
    List<FinancialSituation> findByCompanyId(Long companyId);

    /**
     * Trouve la situation la plus récente d'une company
     * Lance exception si aucune situation trouvée
     */
    FinancialSituation findLastByCompanyId(Long companyId);

    /**
     * Crée une nouvelle situation (snapshot)
     */
    FinancialSituation create(FinancialSituation situation);

    /**
     * Supprime une situation
     */
    void delete(Long id);
}
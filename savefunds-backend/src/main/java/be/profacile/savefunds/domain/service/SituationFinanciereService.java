package be.profacile.savefunds.domain.service;

import be.profacile.savefunds.domain.entity.SituationFinanciere;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des situations financières
 * Gère l'historique des snapshots financiers
 */
public interface SituationFinanciereService {

    /**
     * Trouve une situation par ID
     */
    Optional<SituationFinanciere> findById(Long id);

    /**
     * Trouve toutes les situations d'une entreprise
     * Triées de la plus récente à la plus ancienne
     */
    List<SituationFinanciere> findByEntrepriseId(Long entrepriseId);

    /**
     * Trouve la situation la plus récente d'une entreprise
     * Lance exception si aucune situation trouvée
     */
    SituationFinanciere findLastByEntrepriseId(Long entrepriseId);

    /**
     * Crée une nouvelle situation (snapshot)
     */
    SituationFinanciere create(SituationFinanciere situation);

    /**
     * Supprime une situation
     */
    void delete(Long id);
}
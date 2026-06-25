package be.profacile.savefunds.service;

import be.profacile.savefunds.domain.entity.AnalysePrelevement;
import be.profacile.savefunds.domain.entity.ResultatAnalyse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des analyses de prélèvement.
 */
public interface AnalyseService {

    Optional<AnalysePrelevement> findById(Long id);

    List<AnalysePrelevement> findByEntrepriseId(Long entrepriseId);

    AnalysePrelevement create(Long entrepriseId, BigDecimal montantSouhaite);

    AnalysePrelevement update(Long id, AnalysePrelevement analyse);

    void delete(Long id);

    /**
     * Lance l'analyse complète d'un prélèvement.
     * Crée le ResultatAnalyse et change le statut à TERMINEE.
     *
     * @param analyseId ID de l'analyse à effectuer
     * @return Le ResultatAnalyse créé
     */
    ResultatAnalyse effectuerAnalyse(Long analyseId);
}
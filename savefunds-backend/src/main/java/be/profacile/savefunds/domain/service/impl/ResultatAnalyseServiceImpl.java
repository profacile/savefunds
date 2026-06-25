package be.profacile.savefunds.domain.service.impl;

import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.domain.entity.ResultatAnalyse;
import be.profacile.savefunds.domain.repository.ResultatAnalyseRepository;
import be.profacile.savefunds.domain.service.ResultatAnalyseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implémentation du service de gestion des résultats d'analyse.
 *
 * Règle métier : Une analyse a un seul résultat détaillé (relation 1-1).
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ResultatAnalyseServiceImpl implements ResultatAnalyseService {

    private final ResultatAnalyseRepository resultatRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<ResultatAnalyse> findById(Long id) {
        log.debug("Recherche du résultat d'analyse avec ID : {}", id);
        return resultatRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ResultatAnalyse> findByAnalyse_Id(Long analyseId) {
        log.debug("Recherche du résultat pour l'analyse ID : {}", analyseId);
        return resultatRepository.findByAnalyse_Id(analyseId);
    }

    @Override
    public ResultatAnalyse create(ResultatAnalyse resultat) {
        log.info("Création d'un résultat d'analyse pour analyseId : {}",
                resultat != null && resultat.getAnalyse() != null ? resultat.getAnalyse().getId() : null);

        // ===== VALIDATIONS =====

        if (resultat == null) {
            throw new IllegalArgumentException("Le résultat d'analyse ne peut pas être null");
        }

        if (resultat.getAnalyse() == null || resultat.getAnalyse().getId() == null) {
            throw new IllegalArgumentException("L'analyse est obligatoire");
        }

        if (resultat.getDecisionGlobale() == null) {
            throw new IllegalArgumentException("La décision globale est obligatoire");
        }

        // ===== VÉRIFICATION UNICITÉ =====

        Optional<ResultatAnalyse> existing = resultatRepository.findByAnalyse_Id(resultat.getAnalyse().getId());
        if (existing.isPresent()) {
            throw new IllegalArgumentException(
                    "Un résultat existe déjà pour l'analyse ID : " + resultat.getAnalyse().getId()
            );
        }

        // ===== SAUVEGARDE =====

        ResultatAnalyse saved = resultatRepository.save(resultat);

        log.info("Résultat d'analyse créé avec ID : {} pour analyse ID : {}",
                saved.getId(), saved.getAnalyse().getId());

        return saved;
    }

    @Override
    public ResultatAnalyse update(Long id, ResultatAnalyse resultat) {
        log.info("Mise à jour du résultat d'analyse ID : {}", id);

        // ===== RÉCUPÉRATION =====

        ResultatAnalyse existing = resultatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ResultatAnalyse", "id", id));

        // ===== MISE À JOUR FIELD-BY-FIELD =====

        // Décision globale
        if (resultat.getDecisionGlobale() != null) {
            existing.setDecisionGlobale(resultat.getDecisionGlobale());
        }
        if (resultat.getDetailsDecisionGlobale() != null) {
            existing.setDetailsDecisionGlobale(resultat.getDetailsDecisionGlobale());
        }
        if (resultat.getRecommandationGlobale() != null) {
            existing.setRecommandationGlobale(resultat.getRecommandationGlobale());
        }

        // Scores
        if (resultat.getScoreTresorerie() != null) {
            existing.setScoreTresorerie(resultat.getScoreTresorerie());
        }
        if (resultat.getScoreRatioCACharges() != null) {
            existing.setScoreRatioCACharges(resultat.getScoreRatioCACharges());
        }
        if (resultat.getScoreCompteCourantDebiteur() != null) {
            existing.setScoreCompteCourantDebiteur(resultat.getScoreCompteCourantDebiteur());
        }

        // Décisions par critère
        if (resultat.getDecisionTresorerie() != null) {
            existing.setDecisionTresorerie(resultat.getDecisionTresorerie());
        }
        if (resultat.getDecisionRatioCACharges() != null) {
            existing.setDecisionRatioCACharges(resultat.getDecisionRatioCACharges());
        }
        if (resultat.getDecisionCompteCourant() != null) {
            existing.setDecisionCompteCourant(resultat.getDecisionCompteCourant());
        }

        // Détails par critère
        if (resultat.getDetailsTresorerie() != null) {
            existing.setDetailsTresorerie(resultat.getDetailsTresorerie());
        }
        if (resultat.getDetailsRatioCACharges() != null) {
            existing.setDetailsRatioCACharges(resultat.getDetailsRatioCACharges());
        }
        if (resultat.getDetailsCompteCourant() != null) {
            existing.setDetailsCompteCourant(resultat.getDetailsCompteCourant());
        }

        // Recommandations par critère
        if (resultat.getRecommandationTresorerie() != null) {
            existing.setRecommandationTresorerie(resultat.getRecommandationTresorerie());
        }
        if (resultat.getRecommandationRatioCACharges() != null) {
            existing.setRecommandationRatioCACharges(resultat.getRecommandationRatioCACharges());
        }
        if (resultat.getRecommandationCompteCourant() != null) {
            existing.setRecommandationCompteCourant(resultat.getRecommandationCompteCourant());
        }

        // ===== SAUVEGARDE =====

        ResultatAnalyse updated = resultatRepository.save(existing);

        log.info("Résultat d'analyse mis à jour : ID {}, Décision globale : {}",
                updated.getId(), updated.getDecisionGlobale());

        return updated;
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression du résultat d'analyse ID : {}", id);

        if (!resultatRepository.existsById(id)) {
            throw new ResourceNotFoundException("ResultatAnalyse", "id", id);
        }

        resultatRepository.deleteById(id);

        log.info("Résultat d'analyse supprimé : ID {}", id);
    }
}
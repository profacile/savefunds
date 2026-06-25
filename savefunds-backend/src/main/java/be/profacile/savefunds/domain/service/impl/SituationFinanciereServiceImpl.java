package be.profacile.savefunds.domain.service.impl;

import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.domain.entity.SituationFinanciere;
import be.profacile.savefunds.domain.repository.SituationFinanciereRepository;
import be.profacile.savefunds.domain.service.SituationFinanciereService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service de gestion des situations financières
 * Gère l'historique des snapshots financiers d'une entreprise
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SituationFinanciereServiceImpl implements SituationFinanciereService {

    private final SituationFinanciereRepository situationRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<SituationFinanciere> findById(Long id) {
        log.debug("Recherche de la situation financière avec ID : {}", id);

        return situationRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SituationFinanciere> findByEntrepriseId(Long entrepriseId) {
        log.debug("Recherche de l'historique des situations financières pour entrepriseId : {}", entrepriseId);

        // Retourne la liste complète de l'historique (du plus récent au plus ancien)
        return situationRepository.findByEntrepriseIdOrderByCapturedAtDesc(entrepriseId);
    }

    @Override
    @Transactional(readOnly = true)
    public SituationFinanciere findLastByEntrepriseId(Long entrepriseId) {
        log.debug("Recherche de la dernière situation financière pour entrepriseId : {}", entrepriseId);

        // Retourne uniquement la situation la plus récente
        SituationFinanciere situation =
                situationRepository.findFirstByEntrepriseIdOrderByCapturedAtDesc(entrepriseId);

        if (situation == null) {
            throw new ResourceNotFoundException("Situation", "entrepriseId", entrepriseId);
        }

        log.debug("Dernière situation trouvée : ID {}, capturée le {}",
                situation.getId(), situation.getCapturedAt());

        return situation;
    }

    @Override
    public SituationFinanciere create(SituationFinanciere situation) {
        log.info("Création d'une nouvelle situation financière pour entrepriseId : {}",
                situation != null ? situation.getEntrepriseId() : null);

        // Validations
        if (situation == null) {
            throw new IllegalArgumentException("La situation financière ne peut pas être null");
        }

        if (situation.getEntrepriseId() == null) {
            throw new IllegalArgumentException("L'entrepriseId est obligatoire");
        }

        // Définir la date de capture si non fournie
        if (situation.getCapturedAt() == null) {
            situation.setCapturedAt(LocalDateTime.now());
        }

        // Sauvegarder
        SituationFinanciere saved = situationRepository.save(situation);

        log.info("Situation financière créée avec succès : ID {}, entrepriseId {}, capturée le {}",
                saved.getId(), saved.getEntrepriseId(), saved.getCapturedAt());

        return saved;
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression de la situation financière avec ID : {}", id);

        // Vérifier que la situation existe
        if (!situationRepository.existsById(id)) {
            throw new ResourceNotFoundException("SituationFinanciere", "id", id);
        }

        // Supprimer
        situationRepository.deleteById(id);

        log.info("Situation financière supprimée avec succès : ID {}", id);
    }
}
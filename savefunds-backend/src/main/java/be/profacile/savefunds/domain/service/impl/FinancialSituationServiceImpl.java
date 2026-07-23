package be.profacile.savefunds.domain.service.impl;

import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.domain.entity.FinancialSituation;
import be.profacile.savefunds.domain.repository.FinancialSituationRepository;
import be.profacile.savefunds.domain.service.FinancialSituationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service de gestion des situations financières
 * Gère l'historique des snapshots financiers d'une company
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FinancialSituationServiceImpl implements FinancialSituationService {

    private final FinancialSituationRepository financialSituationRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<FinancialSituation> findById(Long id) {
        log.debug("Recherche de la situation financière avec ID : {}", id);

        return financialSituationRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialSituation> findByCompanyId(Long companyId) {
        log.debug("Recherche de l'historique des situations financières pour companyId : {}", companyId);

        // Retourne la liste complète de l'historique (du plus récent au plus ancien)
        return financialSituationRepository.findByCompanyIdOrderByCapturedAtDesc(companyId);
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialSituation findLastByCompanyId(Long companyId) {
        log.debug("Recherche de la dernière situation financière pour companyId : {}", companyId);

        // Retourne uniquement la situation la plus récente
        FinancialSituation situation =
                financialSituationRepository.findFirstByCompanyIdOrderByCapturedAtDesc(companyId);

        if (situation == null) {
            throw new ResourceNotFoundException("Situation", "companyId", companyId);
        }

        log.debug("Dernière situation trouvée : ID {}, capturée le {}",
                situation.getId(), situation.getCapturedAt());

        return situation;
    }

    @Override
    public FinancialSituation create(FinancialSituation situation) {
        log.info("Création d'une nouvelle situation financière pour companyId : {}",
                situation != null ? situation.getCompanyId() : null);

        // Validations
        if (situation == null) {
            throw new IllegalArgumentException("La situation financière ne peut pas être null");
        }

        if (situation.getCompanyId() == null) {
            throw new IllegalArgumentException("L'companyId est obligatoire");
        }

        // Définir la date de capture si non fournie
        if (situation.getCapturedAt() == null) {
            situation.setCapturedAt(LocalDateTime.now());
        }

        // Sauvegarder
        FinancialSituation saved = financialSituationRepository.save(situation);

        log.info("Situation financière créée avec succès : ID {}, companyId {}, capturée le {}",
                saved.getId(), saved.getCompanyId(), saved.getCapturedAt());

        return saved;
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression de la situation financière avec ID : {}", id);

        // Vérifier que la situation existe
        if (!financialSituationRepository.existsById(id)) {
            throw new ResourceNotFoundException("FinancialSituation", "id", id);
        }

        // Supprimer
        financialSituationRepository.deleteById(id);

        log.info("Situation financière supprimée avec succès : ID {}", id);
    }
}
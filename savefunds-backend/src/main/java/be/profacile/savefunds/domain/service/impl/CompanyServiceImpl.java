package be.profacile.savefunds.domain.service.impl;

import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.domain.entity.Company;
import be.profacile.savefunds.domain.repository.CompanyRepository;
import be.profacile.savefunds.domain.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service de gestion des companies
 * <p>
 * Règle métier : Un utilisateur ne peut avoir qu'une seule company
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Company> findById(Long id) {
        log.debug("Recherche de l'company avec ID : {}", id);

        return companyRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Company> findByUserId(Long userId) {
        log.debug("Recherche de la derniere company pour userId : {}", userId);

        return companyRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Company> findAllByUserId(Long userId) {
        log.debug("Recherche des companies pour userId : {}", userId);

        return companyRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Company> findAll() {
        log.debug("Récupération de toutes les companies");

        return companyRepository.findAll();
    }

    @Override
    public Company create(Company company) {
        log.info("Création d'une company pour userId : {}",
                company != null ? company.getUserId() : null);

        // Validations
        if (company == null) {
            throw new IllegalArgumentException("L'company ne peut pas être null");
        }

        if (company.getUserId() == null) {
            throw new IllegalArgumentException("Le userId est obligatoire");
        }

        if (company.getLegalName() == null || company.getLegalName().isBlank()) {
            throw new IllegalArgumentException("La raison sociale est obligatoire");
        }

        if (company.getEnterpriseNumber() == null || company.getEnterpriseNumber().isBlank()) {
            throw new IllegalArgumentException("Le numéro d'company est obligatoire");
        }

        // Validation métier : un userId ne peut avoir qu'une seule company
        if (companyRepository.existsByUserIdAndEnterpriseNumber(
                company.getUserId(),
                company.getEnterpriseNumber()
        )) {
            throw new IllegalArgumentException(
                    "Cette company est deja rattachee a cet utilisateur"
            );
        }

        // Sauvegarder
        Company saved = companyRepository.save(company);

        log.info("Company créée avec succès : ID {}, raison sociale '{}', userId {}",
                saved.getId(), saved.getLegalName(), saved.getUserId());

        return saved;
    }

    @Override
    public Company update(Long id, Company company) {
        log.info("Mise à jour de l'company ID : {}", id);

        // Vérifier que l'company existe
        Company existing = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));

        // Mise à jour champ par champ (évite la perte de données)

        // Informations de base
        if (company.getLegalName() != null) {
            existing.setLegalName(company.getLegalName());
        }

        if (company.getEnterpriseNumber() != null) {
            existing.setEnterpriseNumber(company.getEnterpriseNumber());
        }

        if (company.getLegalForm() != null) {
            existing.setLegalForm(company.getLegalForm());
        }

        if (company.getActivitySector() != null) {
            existing.setActivitySector(company.getActivitySector());
        }

        // Données financières
        if (company.getMonthlyRevenue() != null) {
            existing.setMonthlyRevenue(company.getMonthlyRevenue());
        }

        if (company.getMonthlyExpenses() != null) {
            existing.setMonthlyExpenses(company.getMonthlyExpenses());
        }

        if (company.getCashBalance() != null) {
            existing.setCashBalance(company.getCashBalance());
        }

        if (company.getDirectorCurrentAccountBalance() != null) {
            existing.setDirectorCurrentAccountBalance(company.getDirectorCurrentAccountBalance());

            // Gestion automatique de la date de début débiteur
            if (company.getDirectorCurrentAccountBalance().compareTo(BigDecimal.ZERO) >= 0) {
                existing.setDirectorCurrentAccountDebitStartDate(null);
                log.debug("Solde CC positif → directorCurrentAccountDebitStartDate réinitialisée");
            } else {
                if (existing.getDirectorCurrentAccountDebitStartDate() == null) {
                    existing.setDirectorCurrentAccountDebitStartDate(LocalDate.now());
                    log.debug("Solde CC négatif → directorCurrentAccountDebitStartDate = {}", LocalDate.now());
                }
            }
        }

        // Statut
        if (company.getStatus() != null) {
            existing.setStatus(company.getStatus());
        }

        // Sauvegarder les modifications
        Company updated = companyRepository.save(existing);

        log.info("Company mise à jour avec succès : ID {}, raison sociale '{}'",
                updated.getId(), updated.getLegalName());

        return updated;
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression de l'company ID : {}", id);

        // Vérifier que l'company existe
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Company", "id", id);
        }

        // Supprimer
        companyRepository.deleteById(id);

        log.info("Company supprimée avec succès : ID {}", id);
    }
}

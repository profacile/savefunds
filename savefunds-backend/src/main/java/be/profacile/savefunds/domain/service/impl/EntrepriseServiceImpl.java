package be.profacile.savefunds.domain.service.impl;

import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.repository.EntrepriseRepository;
import be.profacile.savefunds.domain.service.EntrepriseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service de gestion des entreprises
 * <p>
 * Règle métier : Un utilisateur ne peut avoir qu'une seule entreprise
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EntrepriseServiceImpl implements EntrepriseService {

    private final EntrepriseRepository entrepriseRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Entreprise> findById(Long id) {
        log.debug("Recherche de l'entreprise avec ID : {}", id);

        return entrepriseRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Entreprise> findByUserId(Long userId) {
        log.debug("Recherche de la derniere entreprise pour userId : {}", userId);

        return entrepriseRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Entreprise> findAllByUserId(Long userId) {
        log.debug("Recherche des entreprises pour userId : {}", userId);

        return entrepriseRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Entreprise> findAll() {
        log.debug("Récupération de toutes les entreprises");

        return entrepriseRepository.findAll();
    }

    @Override
    public Entreprise create(Entreprise entreprise) {
        log.info("Création d'une entreprise pour userId : {}",
                entreprise != null ? entreprise.getUserId() : null);

        // Validations
        if (entreprise == null) {
            throw new IllegalArgumentException("L'entreprise ne peut pas être null");
        }

        if (entreprise.getUserId() == null) {
            throw new IllegalArgumentException("Le userId est obligatoire");
        }

        if (entreprise.getRaisonSociale() == null || entreprise.getRaisonSociale().isBlank()) {
            throw new IllegalArgumentException("La raison sociale est obligatoire");
        }

        if (entreprise.getNumeroEntreprise() == null || entreprise.getNumeroEntreprise().isBlank()) {
            throw new IllegalArgumentException("Le numéro d'entreprise est obligatoire");
        }

        // Validation métier : un userId ne peut avoir qu'une seule entreprise
        if (entrepriseRepository.existsByUserIdAndNumeroEntreprise(
                entreprise.getUserId(),
                entreprise.getNumeroEntreprise()
        )) {
            throw new IllegalArgumentException(
                    "Cette entreprise est deja rattachee a cet utilisateur"
            );
        }

        // Sauvegarder
        Entreprise saved = entrepriseRepository.save(entreprise);

        log.info("Entreprise créée avec succès : ID {}, raison sociale '{}', userId {}",
                saved.getId(), saved.getRaisonSociale(), saved.getUserId());

        return saved;
    }

    @Override
    public Entreprise update(Long id, Entreprise entreprise) {
        log.info("Mise à jour de l'entreprise ID : {}", id);

        // Vérifier que l'entreprise existe
        Entreprise existing = entrepriseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise", "id", id));

        // Mise à jour champ par champ (évite la perte de données)

        // Informations de base
        if (entreprise.getRaisonSociale() != null) {
            existing.setRaisonSociale(entreprise.getRaisonSociale());
        }

        if (entreprise.getNumeroEntreprise() != null) {
            existing.setNumeroEntreprise(entreprise.getNumeroEntreprise());
        }

        if (entreprise.getFormeJuridique() != null) {
            existing.setFormeJuridique(entreprise.getFormeJuridique());
        }

        if (entreprise.getSecteurActivite() != null) {
            existing.setSecteurActivite(entreprise.getSecteurActivite());
        }

        // Données financières
        if (entreprise.getChiffreAffairesMensuel() != null) {
            existing.setChiffreAffairesMensuel(entreprise.getChiffreAffairesMensuel());
        }

        if (entreprise.getChargesMensuelles() != null) {
            existing.setChargesMensuelles(entreprise.getChargesMensuelles());
        }

        if (entreprise.getTresorerie() != null) {
            existing.setTresorerie(entreprise.getTresorerie());
        }

        if (entreprise.getSoldeCompteCourant() != null) {
            existing.setSoldeCompteCourant(entreprise.getSoldeCompteCourant());

            // Gestion automatique de la date de début débiteur
            if (entreprise.getSoldeCompteCourant().compareTo(BigDecimal.ZERO) >= 0) {
                existing.setDateDebutDebiteurCC(null);
                log.debug("Solde CC positif → dateDebutDebiteurCC réinitialisée");
            } else {
                if (existing.getDateDebutDebiteurCC() == null) {
                    existing.setDateDebutDebiteurCC(LocalDate.now());
                    log.debug("Solde CC négatif → dateDebutDebiteurCC = {}", LocalDate.now());
                }
            }
        }

        // Statut
        if (entreprise.getStatut() != null) {
            existing.setStatut(entreprise.getStatut());
        }

        // Sauvegarder les modifications
        Entreprise updated = entrepriseRepository.save(existing);

        log.info("Entreprise mise à jour avec succès : ID {}, raison sociale '{}'",
                updated.getId(), updated.getRaisonSociale());

        return updated;
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression de l'entreprise ID : {}", id);

        // Vérifier que l'entreprise existe
        if (!entrepriseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Entreprise", "id", id);
        }

        // Supprimer
        entrepriseRepository.deleteById(id);

        log.info("Entreprise supprimée avec succès : ID {}", id);
    }
}

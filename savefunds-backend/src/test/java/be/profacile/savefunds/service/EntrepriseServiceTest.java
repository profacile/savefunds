package be.profacile.savefunds.service;

import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.enums.StatutEntreprise;
import be.profacile.savefunds.domain.repository.EntrepriseRepository;
import be.profacile.savefunds.domain.service.EntrepriseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests du service EntrepriseService
 *
 * SFB-81 : Complété par Wilfred Tiwa
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("EntrepriseService Tests")
class EntrepriseServiceTest {

    @Autowired
    private EntrepriseService entrepriseService;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @BeforeEach
    void setUp() {
        entrepriseRepository.deleteAll();
    }

    @Test
    @DisplayName("Devrait créer une entreprise")
    void shouldCreateEntreprise() {
        // Given
        Entreprise entreprise = new Entreprise();
        entreprise.setUserId(1L);
        entreprise.setRaisonSociale("Tech Solutions SPRL");
        entreprise.setNumeroEntreprise("BE0123456789");
        entreprise.setFormeJuridique("SPRL");
        entreprise.setSecteurActivite("Informatique");
        entreprise.setChiffreAffairesMensuel(new BigDecimal("50000.00"));
        entreprise.setChargesMensuelles(new BigDecimal("30000.00"));
        entreprise.setTresorerie(new BigDecimal("100000.00"));
        entreprise.setSoldeCompteCourant(new BigDecimal("5000.00"));
        entreprise.setStatut(StatutEntreprise.ACTIVE);

        // When
        Entreprise saved = entrepriseService.create(entreprise);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getRaisonSociale()).isEqualTo("Tech Solutions SPRL");
        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(saved.getStatut()).isEqualTo(StatutEntreprise.ACTIVE);
    }

    @Test
    @DisplayName("Devrait empêcher un userId d'avoir deux entreprises")
    void shouldPreventDuplicateEntrepriseForUserId() {
        // Given
        Entreprise entreprise1 = new Entreprise();
        entreprise1.setUserId(1L);
        entreprise1.setRaisonSociale("Entreprise 1");
        entreprise1.setNumeroEntreprise("BE0111111111");
        entreprise1.setChiffreAffairesMensuel(new BigDecimal("50000"));
        entreprise1.setChargesMensuelles(new BigDecimal("30000"));
        entreprise1.setTresorerie(new BigDecimal("100000"));
        entreprise1.setSoldeCompteCourant(new BigDecimal("5000"));
        entreprise1.setStatut(StatutEntreprise.ACTIVE);

        entrepriseService.create(entreprise1);

        // Tentative de créer une deuxième entreprise pour le même userId
        Entreprise entreprise2 = new Entreprise();
        entreprise2.setUserId(1L); // Même userId !
        entreprise2.setRaisonSociale("Entreprise 2");
        entreprise2.setNumeroEntreprise("BE0222222222");
        entreprise2.setChiffreAffairesMensuel(new BigDecimal("40000"));
        entreprise2.setChargesMensuelles(new BigDecimal("25000"));
        entreprise2.setTresorerie(new BigDecimal("80000"));
        entreprise2.setSoldeCompteCourant(new BigDecimal("3000"));
        entreprise2.setStatut(StatutEntreprise.ACTIVE);

        // When / Then
        assertThatThrownBy(() -> entrepriseService.create(entreprise2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("possède déjà une entreprise");
    }

    @Test
    @DisplayName("Devrait trouver une entreprise par ID")
    void shouldFindById() {
        // Given
        Entreprise entreprise = createEntreprise(1L, "Test SPRL", "BE0123456789");
        Entreprise saved = entrepriseRepository.save(entreprise);

        // When
        Optional<Entreprise> found = entrepriseService.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getRaisonSociale()).isEqualTo("Test SPRL");
    }

    @Test
    @DisplayName("Devrait trouver une entreprise par userId")
    void shouldFindByUserId() {
        // Given
        Entreprise entreprise = createEntreprise(1L, "Test SPRL", "BE0123456789");
        entrepriseRepository.save(entreprise);

        // When
        Optional<Entreprise> found = entrepriseService.findByUserId(1L);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(1L);
        assertThat(found.get().getRaisonSociale()).isEqualTo("Test SPRL");
    }

    @Test
    @DisplayName("Devrait retourner vide si userId inexistant")
    void shouldReturnEmptyWhenUserIdNotFound() {
        // When
        Optional<Entreprise> found = entrepriseService.findByUserId(999L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Devrait récupérer toutes les entreprises")
    void shouldFindAll() {
        // Given
        entrepriseRepository.save(createEntreprise(1L, "Entreprise 1", "BE0111111111"));
        entrepriseRepository.save(createEntreprise(2L, "Entreprise 2", "BE0222222222"));
        entrepriseRepository.save(createEntreprise(3L, "Entreprise 3", "BE0333333333"));

        // When
        List<Entreprise> all = entrepriseService.findAll();

        // Then
        assertThat(all).hasSize(3);
    }

    @Test
    @DisplayName("Devrait mettre à jour une entreprise")
    void shouldUpdateEntreprise() {
        // Given
        Entreprise entreprise = createEntreprise(1L, "Ancienne Raison Sociale", "BE0123456789");
        Entreprise saved = entrepriseRepository.save(entreprise);

        // Préparer les modifications
        Entreprise updates = new Entreprise();
        updates.setRaisonSociale("Nouvelle Raison Sociale");
        updates.setChiffreAffairesMensuel(new BigDecimal("75000.00"));
        updates.setStatut(StatutEntreprise.EN_VIGILANCE);

        // When
        Entreprise updated = entrepriseService.update(saved.getId(), updates);

        // Then
        assertThat(updated.getRaisonSociale()).isEqualTo("Nouvelle Raison Sociale");
        assertThat(updated.getChiffreAffairesMensuel()).isEqualByComparingTo("75000.00");
        assertThat(updated.getStatut()).isEqualTo(StatutEntreprise.EN_VIGILANCE);

        // Vérifier que les autres champs n'ont pas été modifiés
        assertThat(updated.getNumeroEntreprise()).isEqualTo("BE0123456789");
        assertThat(updated.getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Devrait lancer exception si mise à jour entreprise inexistante")
    void shouldThrowExceptionWhenUpdatingNonExistentEntreprise() {
        // Given
        Entreprise updates = new Entreprise();
        updates.setRaisonSociale("Test");

        // When / Then
        assertThatThrownBy(() -> entrepriseService.update(999L, updates))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Entreprise")
                .hasMessageContaining("id");
    }

    @Test
    @DisplayName("Devrait supprimer une entreprise")
    void shouldDeleteEntreprise() {
        // Given
        Entreprise entreprise = createEntreprise(1L, "Test SPRL", "BE0123456789");
        Entreprise saved = entrepriseRepository.save(entreprise);
        Long entrepriseId = saved.getId();

        // When
        entrepriseService.delete(entrepriseId);

        // Then
        Optional<Entreprise> found = entrepriseRepository.findById(entrepriseId);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Devrait lancer exception si suppression entreprise inexistante")
    void shouldThrowExceptionWhenDeletingNonExistentEntreprise() {
        // When / Then
        assertThatThrownBy(() -> entrepriseService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Entreprise")
                .hasMessageContaining("id");
    }

    @Test
    @DisplayName("Devrait rejeter création si raison sociale vide")
    void shouldRejectCreationWithoutRaisonSociale() {
        // Given
        Entreprise entreprise = new Entreprise();
        entreprise.setUserId(1L);
        entreprise.setNumeroEntreprise("BE0123456789");
        // Raison sociale manquante

        // When / Then
        assertThatThrownBy(() -> entrepriseService.create(entreprise))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("raison sociale");
    }

    @Test
    @DisplayName("Devrait rejeter création si numéro entreprise manquant")
    void shouldRejectCreationWithoutNumeroEntreprise() {
        // Given
        Entreprise entreprise = new Entreprise();
        entreprise.setUserId(1L);
        entreprise.setRaisonSociale("Test SPRL");
        // Numéro entreprise manquant

        // When / Then
        assertThatThrownBy(() -> entrepriseService.create(entreprise))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("numéro d'entreprise");
    }

    // ===== HELPER METHODS =====

    private Entreprise createEntreprise(Long userId, String raisonSociale, String numeroEntreprise) {
        Entreprise entreprise = new Entreprise();
        entreprise.setUserId(userId);
        entreprise.setRaisonSociale(raisonSociale);
        entreprise.setNumeroEntreprise(numeroEntreprise);
        entreprise.setChiffreAffairesMensuel(new BigDecimal("50000.00"));
        entreprise.setChargesMensuelles(new BigDecimal("30000.00"));
        entreprise.setTresorerie(new BigDecimal("100000.00"));
        entreprise.setSoldeCompteCourant(new BigDecimal("5000.00"));
        entreprise.setStatut(StatutEntreprise.ACTIVE);
        return entreprise;
    }
}
package be.profacile.savefunds.service;

import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.domain.entity.SituationFinanciere;
import be.profacile.savefunds.domain.repository.SituationFinanciereRepository;
import be.profacile.savefunds.domain.service.SituationFinanciereService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests du service SituationFinanciereService
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("SituationFinanciereService Tests")
class SituationFinanciereServiceTest {

    @Autowired
    private SituationFinanciereService situationService;

    @Autowired
    private SituationFinanciereRepository situationRepository;

    @BeforeEach
    void setUp() {
        situationRepository.deleteAll();
    }

    @Test
    @DisplayName("Devrait créer une situation financière")
    void shouldCreateSituation() {
        SituationFinanciere situation = buildSituation(1L);

        SituationFinanciere created = situationService.create(situation);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getEntrepriseId()).isEqualTo(1L);
        assertThat(created.getCapturedAt()).isNotNull();
    }

    @Test
    @DisplayName("Devrait trouver une situation par ID")
    void shouldFindById() {
        SituationFinanciere saved = situationRepository.save(buildSituation(1L));

        Optional<SituationFinanciere> result = situationService.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("Devrait trouver l'historique d'une entreprise")
    void shouldFindHistorique() {
        SituationFinanciere ancienne = buildSituation(1L);
        ancienne.setCapturedAt(LocalDateTime.now().minusDays(1));

        SituationFinanciere recente = buildSituation(1L);
        recente.setCapturedAt(LocalDateTime.now());

        situationRepository.save(ancienne);
        situationRepository.save(recente);

        List<SituationFinanciere> historique = situationService.findByEntrepriseId(1L);

        assertThat(historique).hasSize(2);
        assertThat(historique.get(0).getCapturedAt())
                .isAfterOrEqualTo(historique.get(1).getCapturedAt());
    }

    @Test
    @DisplayName("Devrait trouver la situation la plus récente")
    void shouldFindLast() {
        SituationFinanciere ancienne = buildSituation(1L);
        ancienne.setCapturedAt(LocalDateTime.now().minusDays(1));

        SituationFinanciere recente = buildSituation(1L);
        recente.setCapturedAt(LocalDateTime.now());

        situationRepository.save(ancienne);
        SituationFinanciere savedRecente = situationRepository.save(recente);

        SituationFinanciere result = situationService.findLastByEntrepriseId(1L);

        assertThat(result.getId()).isEqualTo(savedRecente.getId());
    }

    @Test
    @DisplayName("Devrait lever une exception si aucune situation n'existe pour l'entreprise")
    void shouldThrowExceptionWhenNoSituationFoundForEntreprise() {
        assertThatThrownBy(() -> situationService.findLastByEntrepriseId(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Devrait supprimer une situation")
    void shouldDeleteSituation() {
        SituationFinanciere saved = situationRepository.save(buildSituation(1L));

        situationService.delete(saved.getId());

        assertThat(situationRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("Devrait lever une exception si on supprime une situation inexistante")
    void shouldThrowExceptionWhenDeletingUnknownSituation() {
        assertThatThrownBy(() -> situationService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Devrait lever une exception si entrepriseId est null lors de la création")
    void shouldThrowExceptionWhenCreateWithNullEntrepriseId() {
        SituationFinanciere situation = buildSituation(1L);
        situation.setEntrepriseId(null);

        assertThatThrownBy(() -> situationService.create(situation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("entrepriseId");
    }

    private SituationFinanciere buildSituation(Long entrepriseId) {
        SituationFinanciere situation = new SituationFinanciere();
        situation.setEntrepriseId(entrepriseId);
        situation.setChiffreAffairesMensuel(new BigDecimal("5000.00"));
        situation.setChargesMensuelles(new BigDecimal("2000.00"));
        situation.setTresorerie(new BigDecimal("3000.00"));
        situation.setSoldeCompteCourant(new BigDecimal("1000.00"));
        situation.setRatioCACharges(new BigDecimal("2.50"));
        situation.setTresorerieEnMois(new BigDecimal("1.50"));
        situation.setDureeCompteCourantDebiteur(0);
        situation.setCapturedAt(LocalDateTime.now());
        situation.setSource("MANUEL");
        situation.setNotes("Test");
        return situation;
    }
}
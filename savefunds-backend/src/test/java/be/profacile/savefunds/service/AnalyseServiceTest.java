package be.profacile.savefunds.service;

import be.profacile.savefunds.domain.entity.AnalysePrelevement;
import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.entity.ResultatAnalyse;
import be.profacile.savefunds.domain.enums.Decision;
import be.profacile.savefunds.domain.enums.StatutAnalyse;
import be.profacile.savefunds.domain.enums.StatutEntreprise;
import be.profacile.savefunds.domain.repository.AnalyseRepository;
import be.profacile.savefunds.domain.repository.EntrepriseRepository;
import be.profacile.savefunds.service.AnalyseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("AnalyseService Tests")
class AnalyseServiceTest {

    @Autowired
    private AnalyseService analyseService;

    @Autowired
    private AnalyseRepository analyseRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @BeforeEach
    void setUp() {
        analyseRepository.deleteAll();
        entrepriseRepository.deleteAll();
    }

    // ===================================================================
    // CREATE
    // ===================================================================

    @Test
    @DisplayName("Devrait créer une analyse")
    void shouldCreateAnalyse() {
        // Given
        Entreprise entreprise = entrepriseRepository.save(createEntrepriseWithCompleteData());

        // When — la signature réelle est create(Long entrepriseId, BigDecimal montant)
        AnalysePrelevement saved = analyseService.create(
                entreprise.getId(),
                new BigDecimal("5000.00")
        );

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMontantSouhaite()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(saved.getStatut()).isEqualTo(StatutAnalyse.EN_ATTENTE);
        assertThat(saved.getEntreprise().getId()).isEqualTo(entreprise.getId());
    }

    @Test
    @DisplayName("Devrait rejeter la création si l'entreprise n'existe pas")
    void shouldRejectCreateWhenEntrepriseNotFound() {
        // When / Then
        assertThatThrownBy(() -> analyseService.create(999L, new BigDecimal("5000.00")))
                .hasMessageContaining("Entreprise");
    }

    @Test
    @DisplayName("Devrait rejeter la création si le montant est nul ou négatif")
    void shouldRejectCreateWhenMontantInvalid() {
        Entreprise entreprise = entrepriseRepository.save(createEntrepriseWithCompleteData());

        assertThatThrownBy(() -> analyseService.create(entreprise.getId(), BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ===================================================================
    // FIND
    // ===================================================================

    @Test
    @DisplayName("Devrait trouver les analyses d'une entreprise")
    void shouldFindByEntrepriseId() {
        // Given
        Entreprise entreprise = entrepriseRepository.save(createEntrepriseWithCompleteData());

        analyseRepository.save(AnalysePrelevement.builder()
                .entreprise(entreprise)
                .montantSouhaite(new BigDecimal("5000.00"))
                .statut(StatutAnalyse.EN_ATTENTE)
                .build());

        analyseRepository.save(AnalysePrelevement.builder()
                .entreprise(entreprise)
                .montantSouhaite(new BigDecimal("3000.00"))
                .statut(StatutAnalyse.EN_ATTENTE)
                .build());

        // When
        List<AnalysePrelevement> analyses = analyseService.findByEntrepriseId(entreprise.getId());

        // Then
        assertThat(analyses).hasSize(2);
        assertThat(analyses)
                .extracting(a -> a.getEntreprise().getId())
                .containsOnly(entreprise.getId());
    }

    @Test
    @DisplayName("Devrait trouver une analyse par ID")
    void shouldFindById() {
        Entreprise entreprise = entrepriseRepository.save(createEntrepriseWithCompleteData());
        AnalysePrelevement saved = analyseRepository.save(AnalysePrelevement.builder()
                .entreprise(entreprise)
                .montantSouhaite(new BigDecimal("2000.00"))
                .statut(StatutAnalyse.EN_ATTENTE)
                .build());

        assertThat(analyseService.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("Devrait retourner vide si analyse inexistante")
    void shouldReturnEmptyWhenNotFound() {
        assertThat(analyseService.findById(999L)).isEmpty();
    }

    // ===================================================================
    // EFFECTUER ANALYSE
    // ===================================================================

    @Test
    @DisplayName("Devrait effectuer l'analyse et retourner un résultat")
    void shouldEffectuerAnalyse() {
        // Given
        Entreprise entreprise = entrepriseRepository.save(createEntrepriseWithCompleteData());
        AnalysePrelevement analyse = analyseRepository.save(AnalysePrelevement.builder()
                .entreprise(entreprise)
                .montantSouhaite(new BigDecimal("5000.00"))
                .statut(StatutAnalyse.EN_ATTENTE)
                .build());

        // When — effectuerAnalyse retourne un ResultatAnalyse
        ResultatAnalyse resultat = analyseService.effectuerAnalyse(analyse.getId());

        // Then — résultat cohérent
        assertThat(resultat.getId()).isNotNull();
        assertThat(resultat.getAnalyse().getId()).isEqualTo(analyse.getId());
        assertThat(resultat.getDecisionGlobale()).isIn(Decision.VERT, Decision.ORANGE, Decision.ROUGE);
        assertThat(resultat.getScoreTresorerie()).isNotNull();
        assertThat(resultat.getScoreRatioCACharges()).isNotNull();
        assertThat(resultat.getScoreCompteCourantDebiteur()).isNotNull();
        assertThat(resultat.getRecommandationGlobale()).isNotBlank();

        // Statut de l'analyse mis à jour
        AnalysePrelevement updated = analyseService.findById(analyse.getId()).orElseThrow();
        assertThat(updated.getStatut()).isEqualTo(StatutAnalyse.TERMINEE);
    }

    @Test
    @DisplayName("Devrait retourner VERT pour une situation financière saine")
    void shouldReturnVertForHealthySituation() {
        // Given — CA bien supérieur aux charges, trésorerie solide, CC positif
        Entreprise entreprise = new Entreprise();
        entreprise.setUserId(1L);
        entreprise.setRaisonSociale("Saine SRL");
        entreprise.setNumeroEntreprise("BE0123456789");
        entreprise.setChiffreAffairesMensuel(new BigDecimal("100000.00"));
        entreprise.setChargesMensuelles(new BigDecimal("30000.00"));
        entreprise.setTresorerie(new BigDecimal("200000.00"));
        entreprise.setSoldeCompteCourant(new BigDecimal("10000.00"));
        entreprise.setStatut(StatutEntreprise.ACTIVE);
        entrepriseRepository.save(entreprise);

        AnalysePrelevement analyse = analyseRepository.save(AnalysePrelevement.builder()
                .entreprise(entreprise)
                .montantSouhaite(new BigDecimal("5000.00"))
                .statut(StatutAnalyse.EN_ATTENTE)
                .build());

        // When
        ResultatAnalyse resultat = analyseService.effectuerAnalyse(analyse.getId());

        // Then
        assertThat(resultat.getDecisionGlobale()).isEqualTo(Decision.VERT);
    }

    @Test
    @DisplayName("Devrait retourner ROUGE pour une situation critique")
    void shouldReturnRougeForCriticalSituation() {
        // Given — charges > CA, trésorerie faible, CC débiteur
        Entreprise entreprise = new Entreprise();
        entreprise.setUserId(1L);
        entreprise.setRaisonSociale("En Difficulté SRL");
        entreprise.setNumeroEntreprise("BE9876543210");
        entreprise.setChiffreAffairesMensuel(new BigDecimal("30000.00"));
        entreprise.setChargesMensuelles(new BigDecimal("35000.00"));
        entreprise.setTresorerie(new BigDecimal("20000.00"));
        entreprise.setSoldeCompteCourant(new BigDecimal("-5000.00"));
        entreprise.setStatut(StatutEntreprise.ACTIVE);
        entrepriseRepository.save(entreprise);

        AnalysePrelevement analyse = analyseRepository.save(AnalysePrelevement.builder()
                .entreprise(entreprise)
                .montantSouhaite(new BigDecimal("15000.00"))
                .statut(StatutAnalyse.EN_ATTENTE)
                .build());

        // When
        ResultatAnalyse resultat = analyseService.effectuerAnalyse(analyse.getId());

        // Then
        assertThat(resultat.getDecisionGlobale()).isEqualTo(Decision.ROUGE);
        assertThat(resultat.getRecommandationGlobale()).isNotBlank();
    }

    @Test
    @DisplayName("Devrait rejeter l'analyse si les données financières sont incomplètes")
    void shouldRejectEffectuerAnalyseWhenDataIncomplete() {
        // Given — entreprise sans charges ni trésorerie
        Entreprise entreprise = new Entreprise();
        entreprise.setUserId(1L);
        entreprise.setRaisonSociale("Incomplète SRL");
        entreprise.setNumeroEntreprise("BE0000000001");
        entreprise.setChiffreAffairesMensuel(new BigDecimal("50000.00"));
        // charges, trésorerie, compte courant manquants
        entrepriseRepository.save(entreprise);

        AnalysePrelevement analyse = analyseRepository.save(AnalysePrelevement.builder()
                .entreprise(entreprise)
                .montantSouhaite(new BigDecimal("5000.00"))
                .statut(StatutAnalyse.EN_ATTENTE)
                .build());

        // When / Then
        assertThatThrownBy(() -> analyseService.effectuerAnalyse(analyse.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Données financières incomplètes");
    }

    @Test
    @DisplayName("Devrait rejeter si l'analyse est déjà TERMINEE")
    void shouldRejectEffectuerAnalyseWhenAlreadyTerminee() {
        Entreprise entreprise = entrepriseRepository.save(createEntrepriseWithCompleteData());

        AnalysePrelevement analyse = analyseRepository.save(AnalysePrelevement.builder()
                .entreprise(entreprise)
                .montantSouhaite(new BigDecimal("5000.00"))
                .statut(StatutAnalyse.TERMINEE)           // déjà terminée
                .build());

        assertThatThrownBy(() -> analyseService.effectuerAnalyse(analyse.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("déjà été effectuée");
    }

    // ===================================================================
    // HELPER
    // ===================================================================

    private Entreprise createEntrepriseWithCompleteData() {
        Entreprise entreprise = new Entreprise();
        entreprise.setUserId(1L);
        entreprise.setRaisonSociale("Test Entreprise SRL");
        entreprise.setNumeroEntreprise("BE0123456789");
        entreprise.setChiffreAffairesMensuel(new BigDecimal("50000.00"));
        entreprise.setChargesMensuelles(new BigDecimal("30000.00"));
        entreprise.setTresorerie(new BigDecimal("100000.00"));
        entreprise.setSoldeCompteCourant(new BigDecimal("5000.00"));
        entreprise.setStatut(StatutEntreprise.ACTIVE);
        return entreprise;
    }
}
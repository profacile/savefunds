package be.profacile.savefunds.service;

import be.profacile.savefunds.domain.entity.AnalysePrelevement;
import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.entity.ResultatAnalyse;
import be.profacile.savefunds.domain.enums.Decision;
import be.profacile.savefunds.domain.enums.StatutAnalyse;
import be.profacile.savefunds.domain.repository.AnalyseRepository;
import be.profacile.savefunds.domain.repository.EntrepriseRepository;
import be.profacile.savefunds.domain.repository.ResultatAnalyseRepository;
import be.profacile.savefunds.domain.service.ResultatAnalyseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("ResultatAnalyseService Tests")
class ResultatAnalyseServiceTest {

    @Autowired
    private ResultatAnalyseService resultatService;

    @Autowired
    private ResultatAnalyseRepository resultatRepository;

    @Autowired
    private AnalyseRepository analyseRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    // Analyses de test réutilisées dans les différents tests
    private AnalysePrelevement analyse1, analyse2, analyse3,
            analyse4, analyse5, analyse6;

    @BeforeEach
    void setUp() {
        resultatRepository.deleteAll();
        analyseRepository.deleteAll();
        entrepriseRepository.deleteAll();

        Entreprise entreprise = new Entreprise();
        entreprise.setUserId(1L);
        entreprise.setRaisonSociale("Test SRL");
        entreprise.setNumeroEntreprise("BE0123456789");
        entreprise.setChiffreAffairesMensuel(new BigDecimal("50000.00"));
        entreprise.setChargesMensuelles(new BigDecimal("30000.00"));
        entreprise.setTresorerie(new BigDecimal("100000.00"));
        entreprise.setSoldeCompteCourant(new BigDecimal("5000.00"));
        entrepriseRepository.save(entreprise);

        analyse1 = saveAnalyse(entreprise, "3000.00");
        analyse2 = saveAnalyse(entreprise, "3000.00");
        analyse3 = saveAnalyse(entreprise, "3000.00");
        analyse4 = saveAnalyse(entreprise, "3000.00");
        analyse5 = saveAnalyse(entreprise, "3000.00");
        analyse6 = saveAnalyse(entreprise, "3000.00");
    }

    private AnalysePrelevement saveAnalyse(Entreprise entreprise, String montant) {
        return analyseRepository.save(AnalysePrelevement.builder()
                .entreprise(entreprise)
                .montantSouhaite(new BigDecimal(montant))
                .statut(StatutAnalyse.EN_ATTENTE)
                .build());
    }

    @Test
    @DisplayName("Devrait créer un résultat d'analyse")
    void shouldCreateResultat() {
        ResultatAnalyse resultat = new ResultatAnalyse();
        resultat.setAnalyse(analyse1);
        resultat.setScoreTresorerie(new BigDecimal("5.50"));
        resultat.setScoreRatioCACharges(new BigDecimal("1.75"));
        resultat.setScoreCompteCourantDebiteur(0);
        resultat.setDecisionTresorerie(Decision.VERT);
        resultat.setDecisionRatioCACharges(Decision.VERT);
        resultat.setDecisionCompteCourant(Decision.VERT);
        resultat.setDecisionGlobale(Decision.VERT);
        resultat.setRecommandationTresorerie("Trésorerie excellente.");
        resultat.setRecommandationRatioCACharges("Ratio CA/Charges sain.");
        resultat.setRecommandationCompteCourant("Compte courant créditeur.");

        ResultatAnalyse saved = resultatService.create(resultat);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAnalyse().getId()).isEqualTo(analyse1.getId());
        assertThat(saved.getDecisionGlobale()).isEqualTo(Decision.VERT);
        assertThat(saved.getScoreTresorerie()).isEqualByComparingTo(new BigDecimal("5.50"));
        assertThat(saved.getScoreCompteCourantDebiteur()).isEqualTo(0);
    }

    @Test
    @DisplayName("Devrait trouver un résultat par analyseId")
    void shouldFindByAnalyseId() {
        ResultatAnalyse resultat = new ResultatAnalyse();
        resultat.setAnalyse(analyse1);
        resultat.setDecisionGlobale(Decision.VERT);
        resultat.setScoreTresorerie(new BigDecimal("4.00"));
        resultat.setScoreRatioCACharges(new BigDecimal("1.50"));
        resultat.setScoreCompteCourantDebiteur(0);
        resultat.setDecisionTresorerie(Decision.VERT);
        resultat.setDecisionRatioCACharges(Decision.ORANGE);
        resultat.setDecisionCompteCourant(Decision.VERT);
        resultatRepository.save(resultat);

        Optional<ResultatAnalyse> found = resultatService.findByAnalyse_Id(analyse1.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getAnalyse().getId()).isEqualTo(analyse1.getId());
        assertThat(found.get().getDecisionGlobale()).isEqualTo(Decision.VERT);
    }

    @Test
    @DisplayName("Devrait retourner vide si analyseId inexistant")
    void shouldReturnEmptyWhenAnalyseIdNotFound() {
        Optional<ResultatAnalyse> found = resultatService.findByAnalyse_Id(999L);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Devrait trouver un résultat par ID")
    void shouldFindById() {
        ResultatAnalyse resultat = new ResultatAnalyse();
        resultat.setAnalyse(analyse2);
        resultat.setDecisionGlobale(Decision.ORANGE);
        resultat.setScoreTresorerie(new BigDecimal("2.00"));
        resultat.setScoreRatioCACharges(new BigDecimal("1.20"));
        resultat.setScoreCompteCourantDebiteur(15);
        resultat.setDecisionTresorerie(Decision.ORANGE);
        resultat.setDecisionRatioCACharges(Decision.VERT);
        resultat.setDecisionCompteCourant(Decision.ORANGE);

        ResultatAnalyse saved = resultatRepository.save(resultat);

        Optional<ResultatAnalyse> found = resultatService.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getDecisionGlobale()).isEqualTo(Decision.ORANGE);
    }

    @Test
    @DisplayName("Devrait mettre à jour un résultat")
    void shouldUpdateResultat() {
        ResultatAnalyse resultat = new ResultatAnalyse();
        resultat.setAnalyse(analyse3);
        resultat.setDecisionGlobale(Decision.ORANGE);
        resultat.setScoreTresorerie(new BigDecimal("2.00"));
        resultat.setScoreRatioCACharges(new BigDecimal("1.10"));
        resultat.setScoreCompteCourantDebiteur(15);
        resultat.setDecisionTresorerie(Decision.ORANGE);
        resultat.setDecisionRatioCACharges(Decision.VERT);
        resultat.setDecisionCompteCourant(Decision.ORANGE);

        ResultatAnalyse saved = resultatRepository.save(resultat);

        ResultatAnalyse updates = new ResultatAnalyse();
        updates.setDecisionGlobale(Decision.VERT);
        updates.setScoreTresorerie(new BigDecimal("4.50"));
        updates.setDecisionTresorerie(Decision.VERT);

        ResultatAnalyse updated = resultatService.update(saved.getId(), updates);

        assertThat(updated.getDecisionGlobale()).isEqualTo(Decision.VERT);
        assertThat(updated.getScoreTresorerie()).isEqualByComparingTo(new BigDecimal("4.50"));
        assertThat(updated.getDecisionTresorerie()).isEqualTo(Decision.VERT);
        assertThat(updated.getScoreRatioCACharges()).isEqualByComparingTo(new BigDecimal("1.10"));
    }

    @Test
    @DisplayName("Devrait supprimer un résultat")
    void shouldDeleteResultat() {
        ResultatAnalyse resultat = new ResultatAnalyse();
        resultat.setAnalyse(analyse4);
        resultat.setDecisionGlobale(Decision.ROUGE);
        resultat.setScoreTresorerie(new BigDecimal("0.50"));
        resultat.setScoreRatioCACharges(new BigDecimal("0.85"));
        resultat.setScoreCompteCourantDebiteur(45);
        resultat.setDecisionTresorerie(Decision.ROUGE);
        resultat.setDecisionRatioCACharges(Decision.ROUGE);
        resultat.setDecisionCompteCourant(Decision.ROUGE);

        ResultatAnalyse saved = resultatRepository.save(resultat);
        Long resultatId = saved.getId();

        resultatService.delete(resultatId);

        assertThat(resultatRepository.findById(resultatId)).isEmpty();
    }

    @Test
    @DisplayName("Devrait créer un résultat avec décision ROUGE")
    void shouldCreateResultatWithDecisionRouge() {
        ResultatAnalyse resultat = new ResultatAnalyse();
        resultat.setAnalyse(analyse5);
        resultat.setScoreTresorerie(new BigDecimal("0.50"));
        resultat.setScoreRatioCACharges(new BigDecimal("0.85"));
        resultat.setScoreCompteCourantDebiteur(45);
        resultat.setDecisionTresorerie(Decision.ROUGE);
        resultat.setDecisionRatioCACharges(Decision.ROUGE);
        resultat.setDecisionCompteCourant(Decision.ROUGE);
        resultat.setDecisionGlobale(Decision.ROUGE);
        resultat.setRecommandationTresorerie("🔴 ALERTE : Trésorerie critique.");
        resultat.setRecommandationRatioCACharges("🔴 ALERTE : Situation déficitaire.");
        resultat.setRecommandationCompteCourant("🔴 ALERTE : Compte courant débiteur > 30 jours.");

        ResultatAnalyse saved = resultatService.create(resultat);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDecisionGlobale()).isEqualTo(Decision.ROUGE);
        assertThat(saved.getScoreTresorerie()).isLessThan(new BigDecimal("1.00"));
        assertThat(saved.getScoreCompteCourantDebiteur()).isGreaterThan(30);
    }

    @Test
    @DisplayName("Devrait créer un résultat avec décisions mixtes (ORANGE global)")
    void shouldCreateResultatWithMixedDecisions() {
        ResultatAnalyse resultat = new ResultatAnalyse();
        resultat.setAnalyse(analyse6);
        resultat.setScoreTresorerie(new BigDecimal("4.50"));
        resultat.setScoreRatioCACharges(new BigDecimal("1.65"));
        resultat.setScoreCompteCourantDebiteur(20);
        resultat.setDecisionTresorerie(Decision.VERT);
        resultat.setDecisionRatioCACharges(Decision.VERT);
        resultat.setDecisionCompteCourant(Decision.ORANGE);
        resultat.setDecisionGlobale(Decision.ORANGE);

        ResultatAnalyse saved = resultatService.create(resultat);

        assertThat(saved.getDecisionGlobale()).isEqualTo(Decision.ORANGE);
        assertThat(saved.getDecisionTresorerie()).isEqualTo(Decision.VERT);
        assertThat(saved.getDecisionRatioCACharges()).isEqualTo(Decision.VERT);
        assertThat(saved.getDecisionCompteCourant()).isEqualTo(Decision.ORANGE);
    }
}
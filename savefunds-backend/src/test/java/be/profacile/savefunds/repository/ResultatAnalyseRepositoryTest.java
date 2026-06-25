package be.profacile.savefunds.repository;

import be.profacile.savefunds.domain.entity.AnalysePrelevement;
import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.entity.ResultatAnalyse;
import be.profacile.savefunds.domain.enums.Decision;
import be.profacile.savefunds.domain.enums.StatutAnalyse;
import be.profacile.savefunds.domain.repository.ResultatAnalyseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ResultatAnalyseRepositoryTest {

    @Autowired
    private ResultatAnalyseRepository resultatRepository;

    @Autowired
    private TestEntityManager entityManager;

    private AnalysePrelevement analyse;

    @BeforeEach
    void setUp() {
        // ResultatAnalyse.analyse est @OneToOne non nullable → il faut une vraie entité en base
        Entreprise entreprise = new Entreprise();
        entreprise.setUserId(1L);
        entreprise.setRaisonSociale("Test SRL");
        entreprise.setNumeroEntreprise("BE0123456789");
        entreprise.setChiffreAffairesMensuel(new BigDecimal("50000.00"));
        entreprise.setChargesMensuelles(new BigDecimal("30000.00"));
        entreprise.setTresorerie(new BigDecimal("100000.00"));
        entreprise.setSoldeCompteCourant(new BigDecimal("5000.00"));
        entityManager.persist(entreprise);

        analyse = AnalysePrelevement.builder()
                .entreprise(entreprise)
                .montantSouhaite(new BigDecimal("3000.00"))
                .statut(StatutAnalyse.EN_ATTENTE)
                .build();
        entityManager.persist(analyse);
        entityManager.flush();
    }

    @Test
    void shouldSaveResultat() {
        ResultatAnalyse resultat = new ResultatAnalyse();
        resultat.setAnalyse(analyse);                              // ← relation JPA
        resultat.setScoreTresorerie(new BigDecimal("3.00"));
        resultat.setScoreRatioCACharges(new BigDecimal("1.50"));
        resultat.setScoreCompteCourantDebiteur(0);
        resultat.setDecisionTresorerie(Decision.VERT);
        resultat.setDecisionRatioCACharges(Decision.VERT);
        resultat.setDecisionCompteCourant(Decision.ORANGE);
        resultat.setDecisionGlobale(Decision.VERT);

        ResultatAnalyse saved = resultatRepository.save(resultat);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAnalyse().getId()).isEqualTo(analyse.getId()); // ← via relation
        assertThat(saved.getScoreTresorerie()).isEqualByComparingTo("3.00");
        assertThat(saved.getScoreCompteCourantDebiteur()).isEqualTo(0);
        assertThat(saved.getDecisionGlobale()).isEqualTo(Decision.VERT);
    }

    @Test
    void shouldFindByAnalyseId() {
        ResultatAnalyse resultat = new ResultatAnalyse();
        resultat.setAnalyse(analyse);
        resultat.setDecisionGlobale(Decision.VERT);
        resultatRepository.save(resultat);

        Optional<ResultatAnalyse> result = resultatRepository.findByAnalyse_Id(analyse.getId()); // ← underscore

        assertThat(result).isPresent();
        assertThat(result.get().getAnalyse().getId()).isEqualTo(analyse.getId());
        assertThat(result.get().getDecisionGlobale()).isEqualTo(Decision.VERT);
    }

    @Test
    void shouldReturnEmptyWhenAnalyseIdNotFound() {
        Optional<ResultatAnalyse> result = resultatRepository.findByAnalyse_Id(999L);
        assertThat(result).isEmpty();
    }

    @Test
    void shouldStoreAllCriteriaScores() {
        ResultatAnalyse resultat = new ResultatAnalyse();
        resultat.setAnalyse(analyse);
        resultat.setScoreTresorerie(new BigDecimal("4.50"));
        resultat.setScoreRatioCACharges(new BigDecimal("1.75"));
        resultat.setScoreCompteCourantDebiteur(15);
        resultat.setDecisionTresorerie(Decision.VERT);
        resultat.setDecisionRatioCACharges(Decision.VERT);
        resultat.setDecisionCompteCourant(Decision.ORANGE);
        resultat.setDecisionGlobale(Decision.VERT);

        ResultatAnalyse saved = resultatRepository.save(resultat);

        assertThat(saved.getScoreTresorerie()).isEqualByComparingTo("4.50");
        assertThat(saved.getScoreRatioCACharges()).isEqualByComparingTo("1.75");
        assertThat(saved.getScoreCompteCourantDebiteur()).isEqualTo(15);
    }

    @Test
    void shouldStoreAllCriteriaDecisions() {
        ResultatAnalyse resultat = new ResultatAnalyse();
        resultat.setAnalyse(analyse);
        resultat.setDecisionTresorerie(Decision.VERT);
        resultat.setDecisionRatioCACharges(Decision.VERT);
        resultat.setDecisionCompteCourant(Decision.ORANGE);
        resultat.setDecisionGlobale(Decision.VERT);

        ResultatAnalyse saved = resultatRepository.save(resultat);

        assertThat(saved.getDecisionTresorerie()).isEqualTo(Decision.VERT);
        assertThat(saved.getDecisionRatioCACharges()).isEqualTo(Decision.VERT);
        assertThat(saved.getDecisionCompteCourant()).isEqualTo(Decision.ORANGE);
        assertThat(saved.getDecisionGlobale()).isEqualTo(Decision.VERT);
    }
}
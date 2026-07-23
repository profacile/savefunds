package be.profacile.savefunds.repository;



import be.profacile.savefunds.domain.entity.WithdrawalAnalysis;

import be.profacile.savefunds.domain.entity.Company;

import be.profacile.savefunds.domain.entity.AnalysisResult;

import be.profacile.savefunds.domain.enums.Decision;

import be.profacile.savefunds.domain.enums.AnalysisStatus;

import be.profacile.savefunds.domain.repository.AnalysisResultRepository;

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

class AnalysisResultRepositoryTest {



    @Autowired

    private AnalysisResultRepository analysisResultRepository;



    @Autowired

    private TestEntityManager entityManager;



    private WithdrawalAnalysis analysis;



    @BeforeEach

    void setUp() {

        // AnalysisResult.analysis est @OneToOne non nullable â†’ il faut une vraie entitÃ© en base

        Company company = new Company();

        company.setUserId(1L);

        company.setLegalName("Test SRL");

        company.setEnterpriseNumber("BE0123456789");

        company.setMonthlyRevenue(new BigDecimal("50000.00"));

        company.setMonthlyExpenses(new BigDecimal("30000.00"));

        company.setCashBalance(new BigDecimal("100000.00"));

        company.setDirectorCurrentAccountBalance(new BigDecimal("5000.00"));

        entityManager.persist(company);



        analysis = WithdrawalAnalysis.builder()

                .company(company)

                .requestedAmount(new BigDecimal("3000.00"))

                .status(AnalysisStatus.EN_ATTENTE)

                .build();

        entityManager.persist(analysis);

        entityManager.flush();

    }



    @Test

    void shouldSaveResultat() {

        AnalysisResult result = new AnalysisResult();

        result.setAnalysis(analysis);                              // â† relation JPA

        result.setCashScore(new BigDecimal("3.00"));

        result.setRevenueExpensesRatioScore(new BigDecimal("1.50"));

        result.setDirectorCurrentAccountScore(0);

        result.setCashDecision(Decision.VERT);

        result.setRevenueExpensesRatioDecision(Decision.VERT);

        result.setDirectorCurrentAccountDecision(Decision.ORANGE);

        result.setGlobalDecision(Decision.VERT);



        AnalysisResult saved = analysisResultRepository.save(result);



        assertThat(saved.getId()).isNotNull();

        assertThat(saved.getAnalysis().getId()).isEqualTo(analysis.getId()); // â† via relation

        assertThat(saved.getCashScore()).isEqualByComparingTo("3.00");

        assertThat(saved.getDirectorCurrentAccountScore()).isEqualTo(0);

        assertThat(saved.getGlobalDecision()).isEqualTo(Decision.VERT);

    }



    @Test

    void shouldFindByAnalyseId() {

        AnalysisResult result = new AnalysisResult();

        result.setAnalysis(analysis);

        result.setGlobalDecision(Decision.VERT);

        analysisResultRepository.save(result);



        Optional<AnalysisResult> found = analysisResultRepository.findByAnalysis_Id(analysis.getId()); // â† underscore



        assertThat(found).isPresent();
        assertThat(found.get().getAnalysis().getId()).isEqualTo(analysis.getId());

        assertThat(found.get().getGlobalDecision()).isEqualTo(Decision.VERT);

    }



    @Test

    void shouldReturnEmptyWhenAnalyseIdNotFound() {

        Optional<AnalysisResult> result = analysisResultRepository.findByAnalysis_Id(999L);

        assertThat(result).isEmpty();

    }



    @Test

    void shouldStoreAllCriteriaScores() {

        AnalysisResult result = new AnalysisResult();

        result.setAnalysis(analysis);

        result.setCashScore(new BigDecimal("4.50"));

        result.setRevenueExpensesRatioScore(new BigDecimal("1.75"));

        result.setDirectorCurrentAccountScore(15);

        result.setCashDecision(Decision.VERT);

        result.setRevenueExpensesRatioDecision(Decision.VERT);

        result.setDirectorCurrentAccountDecision(Decision.ORANGE);

        result.setGlobalDecision(Decision.VERT);



        AnalysisResult saved = analysisResultRepository.save(result);



        assertThat(saved.getCashScore()).isEqualByComparingTo("4.50");

        assertThat(saved.getRevenueExpensesRatioScore()).isEqualByComparingTo("1.75");

        assertThat(saved.getDirectorCurrentAccountScore()).isEqualTo(15);

    }



    @Test

    void shouldStoreAllCriteriaDecisions() {

        AnalysisResult result = new AnalysisResult();

        result.setAnalysis(analysis);

        result.setCashDecision(Decision.VERT);

        result.setRevenueExpensesRatioDecision(Decision.VERT);

        result.setDirectorCurrentAccountDecision(Decision.ORANGE);

        result.setGlobalDecision(Decision.VERT);



        AnalysisResult saved = analysisResultRepository.save(result);



        assertThat(saved.getCashDecision()).isEqualTo(Decision.VERT);

        assertThat(saved.getRevenueExpensesRatioDecision()).isEqualTo(Decision.VERT);

        assertThat(saved.getDirectorCurrentAccountDecision()).isEqualTo(Decision.ORANGE);

        assertThat(saved.getGlobalDecision()).isEqualTo(Decision.VERT);

    }

}

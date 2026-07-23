package be.profacile.savefunds.service;



import be.profacile.savefunds.domain.entity.WithdrawalAnalysis;

import be.profacile.savefunds.domain.entity.Company;

import be.profacile.savefunds.domain.entity.AnalysisResult;

import be.profacile.savefunds.domain.enums.Decision;

import be.profacile.savefunds.domain.enums.AnalysisStatus;

import be.profacile.savefunds.domain.repository.WithdrawalAnalysisRepository;

import be.profacile.savefunds.domain.repository.CompanyRepository;

import be.profacile.savefunds.domain.repository.AnalysisResultRepository;

import be.profacile.savefunds.domain.service.AnalysisResultService;

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

@DisplayName("AnalysisResultService Tests")

class AnalysisResultServiceTest {



    @Autowired

    private AnalysisResultService analysisResultService;



    @Autowired

    private AnalysisResultRepository analysisResultRepository;



    @Autowired

    private WithdrawalAnalysisRepository withdrawalAnalysisRepository;



    @Autowired

    private CompanyRepository companyRepository;



    // Analyses de test rÃ©utilisÃ©es dans les diffÃ©rents tests

    private WithdrawalAnalysis analysis1, analysis2, analysis3,

            analysis4, analysis5, analysis6;



    @BeforeEach

    void setUp() {

        analysisResultRepository.deleteAll();

        withdrawalAnalysisRepository.deleteAll();

        companyRepository.deleteAll();



        Company company = new Company();

        company.setUserId(1L);

        company.setLegalName("Test SRL");

        company.setEnterpriseNumber("BE0123456789");

        company.setMonthlyRevenue(new BigDecimal("50000.00"));

        company.setMonthlyExpenses(new BigDecimal("30000.00"));

        company.setCashBalance(new BigDecimal("100000.00"));

        company.setDirectorCurrentAccountBalance(new BigDecimal("5000.00"));

        companyRepository.save(company);



        analysis1 = saveAnalyse(company, "3000.00");

        analysis2 = saveAnalyse(company, "3000.00");

        analysis3 = saveAnalyse(company, "3000.00");

        analysis4 = saveAnalyse(company, "3000.00");

        analysis5 = saveAnalyse(company, "3000.00");

        analysis6 = saveAnalyse(company, "3000.00");

    }



    private WithdrawalAnalysis saveAnalyse(Company company, String montant) {

        return withdrawalAnalysisRepository.save(WithdrawalAnalysis.builder()

                .company(company)

                .requestedAmount(new BigDecimal(montant))

                .status(AnalysisStatus.EN_ATTENTE)

                .build());

    }



    @Test

    @DisplayName("Devrait crÃ©er un rÃ©sultat d'analysis")

    void shouldCreateResultat() {

        AnalysisResult result = new AnalysisResult();

        result.setAnalysis(analysis1);

        result.setCashScore(new BigDecimal("5.50"));

        result.setRevenueExpensesRatioScore(new BigDecimal("1.75"));

        result.setDirectorCurrentAccountScore(0);

        result.setCashDecision(Decision.VERT);

        result.setRevenueExpensesRatioDecision(Decision.VERT);

        result.setDirectorCurrentAccountDecision(Decision.VERT);

        result.setGlobalDecision(Decision.VERT);

        result.setCashRecommendation("TrÃ©sorerie excellente.");

        result.setRevenueExpensesRatioRecommendation("Ratio CA/Charges sain.");

        result.setDirectorCurrentAccountRecommendation("Compte courant crÃ©diteur.");



        AnalysisResult saved = analysisResultService.create(result);



        assertThat(saved.getId()).isNotNull();

        assertThat(saved.getAnalysis().getId()).isEqualTo(analysis1.getId());

        assertThat(saved.getGlobalDecision()).isEqualTo(Decision.VERT);

        assertThat(saved.getCashScore()).isEqualByComparingTo(new BigDecimal("5.50"));

        assertThat(saved.getDirectorCurrentAccountScore()).isEqualTo(0);

    }



    @Test

    @DisplayName("Devrait trouver un rÃ©sultat par analysisId")

    void shouldFindByAnalyseId() {

        AnalysisResult result = new AnalysisResult();

        result.setAnalysis(analysis1);

        result.setGlobalDecision(Decision.VERT);

        result.setCashScore(new BigDecimal("4.00"));

        result.setRevenueExpensesRatioScore(new BigDecimal("1.50"));

        result.setDirectorCurrentAccountScore(0);

        result.setCashDecision(Decision.VERT);

        result.setRevenueExpensesRatioDecision(Decision.ORANGE);

        result.setDirectorCurrentAccountDecision(Decision.VERT);

        analysisResultRepository.save(result);



        Optional<AnalysisResult> found = analysisResultService.findByAnalysis_Id(analysis1.getId());



        assertThat(found).isPresent();

        assertThat(found.get().getAnalysis().getId()).isEqualTo(analysis1.getId());

        assertThat(found.get().getGlobalDecision()).isEqualTo(Decision.VERT);

    }



    @Test

    @DisplayName("Devrait retourner vide si analysisId inexistant")

    void shouldReturnEmptyWhenAnalyseIdNotFound() {

        Optional<AnalysisResult> found = analysisResultService.findByAnalysis_Id(999L);

        assertThat(found).isEmpty();

    }



    @Test

    @DisplayName("Devrait trouver un rÃ©sultat par ID")

    void shouldFindById() {

        AnalysisResult result = new AnalysisResult();

        result.setAnalysis(analysis2);

        result.setGlobalDecision(Decision.ORANGE);

        result.setCashScore(new BigDecimal("2.00"));

        result.setRevenueExpensesRatioScore(new BigDecimal("1.20"));

        result.setDirectorCurrentAccountScore(15);

        result.setCashDecision(Decision.ORANGE);

        result.setRevenueExpensesRatioDecision(Decision.VERT);

        result.setDirectorCurrentAccountDecision(Decision.ORANGE);



        AnalysisResult saved = analysisResultRepository.save(result);



        Optional<AnalysisResult> found = analysisResultService.findById(saved.getId());



        assertThat(found).isPresent();

        assertThat(found.get().getId()).isEqualTo(saved.getId());

        assertThat(found.get().getGlobalDecision()).isEqualTo(Decision.ORANGE);

    }



    @Test

    @DisplayName("Devrait mettre Ã  jour un rÃ©sultat")

    void shouldUpdateResultat() {

        AnalysisResult result = new AnalysisResult();

        result.setAnalysis(analysis3);

        result.setGlobalDecision(Decision.ORANGE);

        result.setCashScore(new BigDecimal("2.00"));

        result.setRevenueExpensesRatioScore(new BigDecimal("1.10"));

        result.setDirectorCurrentAccountScore(15);

        result.setCashDecision(Decision.ORANGE);

        result.setRevenueExpensesRatioDecision(Decision.VERT);

        result.setDirectorCurrentAccountDecision(Decision.ORANGE);



        AnalysisResult saved = analysisResultRepository.save(result);



        AnalysisResult updates = new AnalysisResult();

        updates.setGlobalDecision(Decision.VERT);

        updates.setCashScore(new BigDecimal("4.50"));

        updates.setCashDecision(Decision.VERT);



        AnalysisResult updated = analysisResultService.update(saved.getId(), updates);



        assertThat(updated.getGlobalDecision()).isEqualTo(Decision.VERT);

        assertThat(updated.getCashScore()).isEqualByComparingTo(new BigDecimal("4.50"));

        assertThat(updated.getCashDecision()).isEqualTo(Decision.VERT);

        assertThat(updated.getRevenueExpensesRatioScore()).isEqualByComparingTo(new BigDecimal("1.10"));

    }



    @Test

    @DisplayName("Devrait supprimer un rÃ©sultat")

    void shouldDeleteResultat() {

        AnalysisResult result = new AnalysisResult();

        result.setAnalysis(analysis4);

        result.setGlobalDecision(Decision.ROUGE);

        result.setCashScore(new BigDecimal("0.50"));

        result.setRevenueExpensesRatioScore(new BigDecimal("0.85"));

        result.setDirectorCurrentAccountScore(45);

        result.setCashDecision(Decision.ROUGE);

        result.setRevenueExpensesRatioDecision(Decision.ROUGE);

        result.setDirectorCurrentAccountDecision(Decision.ROUGE);



        AnalysisResult saved = analysisResultRepository.save(result);

        Long resultId = saved.getId();



        analysisResultService.delete(resultId);



        assertThat(analysisResultRepository.findById(resultId)).isEmpty();

    }



    @Test

    @DisplayName("Devrait crÃ©er un rÃ©sultat avec dÃ©cision ROUGE")

    void shouldCreateResultatWithDecisionRouge() {

        AnalysisResult result = new AnalysisResult();

        result.setAnalysis(analysis5);

        result.setCashScore(new BigDecimal("0.50"));

        result.setRevenueExpensesRatioScore(new BigDecimal("0.85"));

        result.setDirectorCurrentAccountScore(45);

        result.setCashDecision(Decision.ROUGE);

        result.setRevenueExpensesRatioDecision(Decision.ROUGE);

        result.setDirectorCurrentAccountDecision(Decision.ROUGE);

        result.setGlobalDecision(Decision.ROUGE);

        result.setCashRecommendation("ðŸ”´ ALERTE : TrÃ©sorerie critique.");

        result.setRevenueExpensesRatioRecommendation("ðŸ”´ ALERTE : Situation dÃ©ficitaire.");

        result.setDirectorCurrentAccountRecommendation("ðŸ”´ ALERTE : Compte courant dÃ©biteur > 30 jours.");



        AnalysisResult saved = analysisResultService.create(result);



        assertThat(saved.getId()).isNotNull();

        assertThat(saved.getGlobalDecision()).isEqualTo(Decision.ROUGE);

        assertThat(saved.getCashScore()).isLessThan(new BigDecimal("1.00"));

        assertThat(saved.getDirectorCurrentAccountScore()).isGreaterThan(30);

    }



    @Test

    @DisplayName("Devrait crÃ©er un rÃ©sultat avec dÃ©cisions mixtes (ORANGE global)")

    void shouldCreateResultatWithMixedDecisions() {

        AnalysisResult result = new AnalysisResult();

        result.setAnalysis(analysis6);

        result.setCashScore(new BigDecimal("4.50"));

        result.setRevenueExpensesRatioScore(new BigDecimal("1.65"));

        result.setDirectorCurrentAccountScore(20);

        result.setCashDecision(Decision.VERT);

        result.setRevenueExpensesRatioDecision(Decision.VERT);

        result.setDirectorCurrentAccountDecision(Decision.ORANGE);

        result.setGlobalDecision(Decision.ORANGE);



        AnalysisResult saved = analysisResultService.create(result);



        assertThat(saved.getGlobalDecision()).isEqualTo(Decision.ORANGE);

        assertThat(saved.getCashDecision()).isEqualTo(Decision.VERT);

        assertThat(saved.getRevenueExpensesRatioDecision()).isEqualTo(Decision.VERT);

        assertThat(saved.getDirectorCurrentAccountDecision()).isEqualTo(Decision.ORANGE);

    }

}

package be.profacile.savefunds.service;



import be.profacile.savefunds.domain.entity.WithdrawalAnalysis;

import be.profacile.savefunds.domain.entity.Company;

import be.profacile.savefunds.domain.entity.AnalysisResult;

import be.profacile.savefunds.domain.enums.Decision;

import be.profacile.savefunds.domain.enums.AnalysisStatus;

import be.profacile.savefunds.domain.enums.CompanyStatus;

import be.profacile.savefunds.domain.repository.WithdrawalAnalysisRepository;

import be.profacile.savefunds.domain.repository.CompanyRepository;

import be.profacile.savefunds.service.WithdrawalAnalysisService;

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

@DisplayName("WithdrawalAnalysisService Tests")

class WithdrawalAnalysisServiceTest {



    @Autowired

    private WithdrawalAnalysisService withdrawalAnalysisService;



    @Autowired

    private WithdrawalAnalysisRepository withdrawalAnalysisRepository;



    @Autowired

    private CompanyRepository companyRepository;



    @BeforeEach

    void setUp() {

        withdrawalAnalysisRepository.deleteAll();

        companyRepository.deleteAll();

    }



    // ===================================================================

    // CREATE

    // ===================================================================



    @Test

    @DisplayName("Devrait crÃ©er une analysis")

    void shouldCreateAnalyse() {

        // Given

        Company company = companyRepository.save(createCompanyWithCompleteData());



        // When â€” la signature rÃ©elle est create(Long companyId, BigDecimal montant)

        WithdrawalAnalysis saved = withdrawalAnalysisService.create(

                company.getId(),

                new BigDecimal("5000.00")

        );



        // Then

        assertThat(saved.getId()).isNotNull();

        assertThat(saved.getRequestedAmount()).isEqualByComparingTo(new BigDecimal("5000.00"));

        assertThat(saved.getStatus()).isEqualTo(AnalysisStatus.EN_ATTENTE);

        assertThat(saved.getCompany().getId()).isEqualTo(company.getId());

    }



    @Test

    @DisplayName("Devrait rejeter la crÃ©ation si l'company n'existe pas")

    void shouldRejectCreateWhenCompanyNotFound() {

        // When / Then

        assertThatThrownBy(() -> withdrawalAnalysisService.create(999L, new BigDecimal("5000.00")))

                .hasMessageContaining("Company");

    }



    @Test

    @DisplayName("Devrait rejeter la crÃ©ation si le montant est nul ou nÃ©gatif")

    void shouldRejectCreateWhenMontantInvalid() {

        Company company = companyRepository.save(createCompanyWithCompleteData());



        assertThatThrownBy(() -> withdrawalAnalysisService.create(company.getId(), BigDecimal.ZERO))

                .isInstanceOf(IllegalArgumentException.class);

    }



    // ===================================================================

    // FIND

    // ===================================================================



    @Test

    @DisplayName("Devrait trouver les analysiss d'une company")

    void shouldFindByCompanyId() {

        // Given

        Company company = companyRepository.save(createCompanyWithCompleteData());



        withdrawalAnalysisRepository.save(WithdrawalAnalysis.builder()

                .company(company)

                .requestedAmount(new BigDecimal("5000.00"))

                .status(AnalysisStatus.EN_ATTENTE)

                .build());



        withdrawalAnalysisRepository.save(WithdrawalAnalysis.builder()

                .company(company)

                .requestedAmount(new BigDecimal("3000.00"))

                .status(AnalysisStatus.EN_ATTENTE)

                .build());



        // When

        List<WithdrawalAnalysis> analysiss = withdrawalAnalysisService.findByCompanyId(company.getId());



        // Then

        assertThat(analysiss).hasSize(2);

        assertThat(analysiss)

                .extracting(a -> a.getCompany().getId())

                .containsOnly(company.getId());

    }



    @Test

    @DisplayName("Devrait trouver une analysis par ID")

    void shouldFindById() {

        Company company = companyRepository.save(createCompanyWithCompleteData());

        WithdrawalAnalysis saved = withdrawalAnalysisRepository.save(WithdrawalAnalysis.builder()

                .company(company)

                .requestedAmount(new BigDecimal("2000.00"))

                .status(AnalysisStatus.EN_ATTENTE)

                .build());



        assertThat(withdrawalAnalysisService.findById(saved.getId())).isPresent();

    }



    @Test

    @DisplayName("Devrait retourner vide si analysis inexistante")

    void shouldReturnEmptyWhenNotFound() {

        assertThat(withdrawalAnalysisService.findById(999L)).isEmpty();

    }



    // ===================================================================

    // EFFECTUER ANALYSE

    // ===================================================================



    @Test

    @DisplayName("Devrait effectuer l'analysis et retourner un rÃ©sultat")

    void shouldEffectuerAnalyse() {

        // Given

        Company company = companyRepository.save(createCompanyWithCompleteData());

        WithdrawalAnalysis analysis = withdrawalAnalysisRepository.save(WithdrawalAnalysis.builder()

                .company(company)

                .requestedAmount(new BigDecimal("5000.00"))

                .status(AnalysisStatus.EN_ATTENTE)

                .build());



        // When â€” runAnalysis retourne un AnalysisResult

        AnalysisResult result = withdrawalAnalysisService.runAnalysis(analysis.getId());



        // Then â€” rÃ©sultat cohÃ©rent

        assertThat(result.getId()).isNotNull();

        assertThat(result.getAnalysis().getId()).isEqualTo(analysis.getId());

        assertThat(result.getGlobalDecision()).isIn(Decision.VERT, Decision.ORANGE, Decision.ROUGE);

        assertThat(result.getCashScore()).isNotNull();

        assertThat(result.getRevenueExpensesRatioScore()).isNotNull();

        assertThat(result.getDirectorCurrentAccountScore()).isNotNull();

        assertThat(result.getGlobalRecommendation()).isNotBlank();



        // Statut de l'analysis mis Ã  jour

        WithdrawalAnalysis updated = withdrawalAnalysisService.findById(analysis.getId()).orElseThrow();

        assertThat(updated.getStatus()).isEqualTo(AnalysisStatus.TERMINEE);

    }



    @Test

    @DisplayName("Devrait retourner VERT pour une situation financiÃ¨re saine")

    void shouldReturnVertForHealthySituation() {

        // Given â€” CA bien supÃ©rieur aux charges, trÃ©sorerie solide, CC positif

        Company company = new Company();

        company.setUserId(1L);

        company.setLegalName("Saine SRL");

        company.setEnterpriseNumber("BE0123456789");

        company.setMonthlyRevenue(new BigDecimal("100000.00"));

        company.setMonthlyExpenses(new BigDecimal("30000.00"));

        company.setCashBalance(new BigDecimal("200000.00"));

        company.setDirectorCurrentAccountBalance(new BigDecimal("10000.00"));

        company.setStatus(CompanyStatus.ACTIVE);

        companyRepository.save(company);



        WithdrawalAnalysis analysis = withdrawalAnalysisRepository.save(WithdrawalAnalysis.builder()

                .company(company)

                .requestedAmount(new BigDecimal("5000.00"))

                .status(AnalysisStatus.EN_ATTENTE)

                .build());



        // When

        AnalysisResult result = withdrawalAnalysisService.runAnalysis(analysis.getId());



        // Then

        assertThat(result.getGlobalDecision()).isEqualTo(Decision.VERT);

    }



    @Test

    @DisplayName("Devrait retourner ROUGE pour une situation critique")

    void shouldReturnRougeForCriticalSituation() {

        // Given â€” charges > CA, trÃ©sorerie faible, CC dÃ©biteur

        Company company = new Company();

        company.setUserId(1L);

        company.setLegalName("En DifficultÃ© SRL");

        company.setEnterpriseNumber("BE9876543210");

        company.setMonthlyRevenue(new BigDecimal("30000.00"));

        company.setMonthlyExpenses(new BigDecimal("35000.00"));

        company.setCashBalance(new BigDecimal("20000.00"));

        company.setDirectorCurrentAccountBalance(new BigDecimal("-5000.00"));

        company.setStatus(CompanyStatus.ACTIVE);

        companyRepository.save(company);



        WithdrawalAnalysis analysis = withdrawalAnalysisRepository.save(WithdrawalAnalysis.builder()

                .company(company)

                .requestedAmount(new BigDecimal("15000.00"))

                .status(AnalysisStatus.EN_ATTENTE)

                .build());



        // When

        AnalysisResult result = withdrawalAnalysisService.runAnalysis(analysis.getId());



        // Then

        assertThat(result.getGlobalDecision()).isEqualTo(Decision.ROUGE);

        assertThat(result.getGlobalRecommendation()).isNotBlank();

    }



    @Test

    @DisplayName("Devrait rejeter l'analysis si les Données financières sont incomplÃ¨tes")

    void shouldRejectEffectuerAnalyseWhenDataIncomplete() {

        // Given â€” company sans charges ni trÃ©sorerie

        Company company = new Company();

        company.setUserId(1L);

        company.setLegalName("IncomplÃ¨te SRL");

        company.setEnterpriseNumber("BE0000000001");

        company.setMonthlyRevenue(new BigDecimal("50000.00"));

        // charges, trÃ©sorerie, compte courant manquants

        companyRepository.save(company);



        WithdrawalAnalysis analysis = withdrawalAnalysisRepository.save(WithdrawalAnalysis.builder()

                .company(company)

                .requestedAmount(new BigDecimal("5000.00"))

                .status(AnalysisStatus.EN_ATTENTE)

                .build());



        // When / Then

        assertThatThrownBy(() -> withdrawalAnalysisService.runAnalysis(analysis.getId()))

                .isInstanceOf(IllegalStateException.class)

                .hasMessageContaining("Données financières incomplètes");

    }



    @Test

    @DisplayName("Devrait rejeter si l'analysis est dÃ©jÃ  TERMINEE")

    void shouldRejectEffectuerAnalyseWhenAlreadyTerminee() {

        Company company = companyRepository.save(createCompanyWithCompleteData());



        WithdrawalAnalysis analysis = withdrawalAnalysisRepository.save(WithdrawalAnalysis.builder()

                .company(company)

                .requestedAmount(new BigDecimal("5000.00"))

                .status(AnalysisStatus.TERMINEE)           // dÃ©jÃ  terminÃ©e

                .build());



        assertThatThrownBy(() -> withdrawalAnalysisService.runAnalysis(analysis.getId()))

                .isInstanceOf(IllegalStateException.class)

                .hasMessageContaining("effectu");

    }



    // ===================================================================

    // HELPER

    // ===================================================================



    private Company createCompanyWithCompleteData() {

        Company company = new Company();

        company.setUserId(1L);

        company.setLegalName("Test Company SRL");

        company.setEnterpriseNumber("BE0123456789");

        company.setMonthlyRevenue(new BigDecimal("50000.00"));

        company.setMonthlyExpenses(new BigDecimal("30000.00"));

        company.setCashBalance(new BigDecimal("100000.00"));

        company.setDirectorCurrentAccountBalance(new BigDecimal("5000.00"));

        company.setStatus(CompanyStatus.ACTIVE);

        return company;

    }

}

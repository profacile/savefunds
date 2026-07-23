package be.profacile.savefunds.repository;



import be.profacile.savefunds.domain.entity.WithdrawalAnalysis;

import be.profacile.savefunds.domain.entity.Company;

import be.profacile.savefunds.domain.enums.AnalysisStatus;

import be.profacile.savefunds.domain.enums.CompanyStatus;

import be.profacile.savefunds.domain.repository.WithdrawalAnalysisRepository;

import be.profacile.savefunds.domain.repository.CompanyRepository;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.test.context.ActiveProfiles;



import java.math.BigDecimal;

import java.util.List;



import static org.junit.Assert.*;



@DataJpaTest

@ActiveProfiles("test")

class WithdrawalAnalysisRepositoryTest {



    @Autowired

    private WithdrawalAnalysisRepository withdrawalAnalysisRepository;



    @Autowired

    private CompanyRepository companyRepository;



    private Company company;



    @BeforeEach

    void setUp() {

        Company e = new Company();

        e.setUserId(1L);

        e.setLegalName("Tech Solutions SPRL");

        e.setEnterpriseNumber("BE0123456789");

        e.setLegalForm("SPRL");

        e.setActivitySector("Informatique");

        e.setMonthlyRevenue(new BigDecimal("50000.00"));

        e.setMonthlyExpenses(new BigDecimal("30000.00"));

        e.setCashBalance(new BigDecimal("100000.00"));

        e.setDirectorCurrentAccountBalance(new BigDecimal("5000.00"));

        e.setStatus(CompanyStatus.ACTIVE);



        this.company = companyRepository.save(e);  // âœ… champ de classe

    }



    @Test

    @DisplayName("Devrait sauvegarder une analysis")

    void shouldSaveWithdrawalAnalysis() {

        WithdrawalAnalysis analysis = WithdrawalAnalysis.builder()

                .company(company)

                .requestedAmount(new BigDecimal("1500.00"))

                .status(AnalysisStatus.EN_ATTENTE)

                .build();



        WithdrawalAnalysis saved = withdrawalAnalysisRepository.save(analysis);



        assertNotNull(saved.getId());

        assertEquals(company.getId(), saved.getCompany().getId());

        assertEquals(new BigDecimal("1500.00"), saved.getRequestedAmount());

        assertEquals(AnalysisStatus.EN_ATTENTE, saved.getStatus());

        assertNotNull(saved.getCreatedAt());

    }



    @Test

    @DisplayName("Devrait trouver les analysiss d'une company triÃ©es par date desc")

    void shouldFindByCompany_IdOrderByCreatedAtDesc() {

        withdrawalAnalysisRepository.save(WithdrawalAnalysis.builder()

                .company(company)

                .requestedAmount(new BigDecimal("100.00"))

                .status(AnalysisStatus.EN_ATTENTE)

                .build());



        withdrawalAnalysisRepository.save(WithdrawalAnalysis.builder()

                .company(company)

                .requestedAmount(new BigDecimal("200.00"))

                .status(AnalysisStatus.EN_ATTENTE)

                .build());



        withdrawalAnalysisRepository.save(WithdrawalAnalysis.builder()

                .company(company)

                .requestedAmount(new BigDecimal("300.00"))

                .status(AnalysisStatus.EN_ATTENTE)

                .build());



        List<WithdrawalAnalysis> result =

                withdrawalAnalysisRepository.findByCompany_IdOrderByCreatedAtDesc(company.getId()); // âœ… underscore



        assertNotNull(result);

        assertEquals(3, result.size());

        result.forEach(a -> assertEquals(company.getId(), a.getCompany().getId()));

    }



    @Test

    @DisplayName("Devrait retourner une liste vide si aucune analysis")

    void shouldReturnEmptyListWhenNoAnalyseExists() {

        List<WithdrawalAnalysis> result =

                withdrawalAnalysisRepository.findByCompany_IdOrderByCreatedAtDesc(999L); // âœ… underscore



        assertNotNull(result);

        assertTrue(result.isEmpty());

    }

}

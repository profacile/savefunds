package be.profacile.savefunds.service;



import be.profacile.savefunds.api.exception.ResourceNotFoundException;

import be.profacile.savefunds.domain.entity.FinancialSituation;

import be.profacile.savefunds.domain.repository.FinancialSituationRepository;

import be.profacile.savefunds.domain.service.FinancialSituationService;

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

 * Tests du service FinancialSituationService

 */

@SpringBootTest

@Transactional

@ActiveProfiles("test")

@DisplayName("FinancialSituationService Tests")

class FinancialSituationServiceTest {



    @Autowired

    private FinancialSituationService situationService;



    @Autowired

    private FinancialSituationRepository financialSituationRepository;



    @BeforeEach

    void setUp() {

        financialSituationRepository.deleteAll();

    }



    @Test

    @DisplayName("Devrait crÃ©er une situation financiÃ¨re")

    void shouldCreateSituation() {

        FinancialSituation situation = buildSituation(1L);



        FinancialSituation created = situationService.create(situation);



        assertThat(created.getId()).isNotNull();

        assertThat(created.getCompanyId()).isEqualTo(1L);

        assertThat(created.getCapturedAt()).isNotNull();

    }



    @Test

    @DisplayName("Devrait trouver une situation par ID")

    void shouldFindById() {

        FinancialSituation saved = financialSituationRepository.save(buildSituation(1L));



        Optional<FinancialSituation> result = situationService.findById(saved.getId());



        assertThat(result).isPresent();

        assertThat(result.get().getId()).isEqualTo(saved.getId());

    }



    @Test

    @DisplayName("Devrait trouver l'historique d'une company")

    void shouldFindHistorique() {

        FinancialSituation ancienne = buildSituation(1L);

        ancienne.setCapturedAt(LocalDateTime.now().minusDays(1));



        FinancialSituation recente = buildSituation(1L);

        recente.setCapturedAt(LocalDateTime.now());



        financialSituationRepository.save(ancienne);

        financialSituationRepository.save(recente);



        List<FinancialSituation> historique = situationService.findByCompanyId(1L);



        assertThat(historique).hasSize(2);

        assertThat(historique.get(0).getCapturedAt())

                .isAfterOrEqualTo(historique.get(1).getCapturedAt());

    }



    @Test

    @DisplayName("Devrait trouver la situation la plus rÃ©cente")

    void shouldFindLast() {

        FinancialSituation ancienne = buildSituation(1L);

        ancienne.setCapturedAt(LocalDateTime.now().minusDays(1));



        FinancialSituation recente = buildSituation(1L);

        recente.setCapturedAt(LocalDateTime.now());



        financialSituationRepository.save(ancienne);

        FinancialSituation savedRecente = financialSituationRepository.save(recente);



        FinancialSituation result = situationService.findLastByCompanyId(1L);



        assertThat(result.getId()).isEqualTo(savedRecente.getId());

    }



    @Test

    @DisplayName("Devrait lever une exception si aucune situation n'existe pour l'company")

    void shouldThrowExceptionWhenNoSituationFoundForCompany() {

        assertThatThrownBy(() -> situationService.findLastByCompanyId(999L))

                .isInstanceOf(ResourceNotFoundException.class);

    }



    @Test

    @DisplayName("Devrait supprimer une situation")

    void shouldDeleteSituation() {

        FinancialSituation saved = financialSituationRepository.save(buildSituation(1L));



        situationService.delete(saved.getId());



        assertThat(financialSituationRepository.findById(saved.getId())).isEmpty();

    }



    @Test

    @DisplayName("Devrait lever une exception si on supprime une situation inexistante")

    void shouldThrowExceptionWhenDeletingUnknownSituation() {

        assertThatThrownBy(() -> situationService.delete(999L))

                .isInstanceOf(ResourceNotFoundException.class);

    }



    @Test

    @DisplayName("Devrait lever une exception si companyId est null lors de la crÃ©ation")

    void shouldThrowExceptionWhenCreateWithNullCompanyId() {

        FinancialSituation situation = buildSituation(1L);

        situation.setCompanyId(null);



        assertThatThrownBy(() -> situationService.create(situation))

                .isInstanceOf(IllegalArgumentException.class)

                .hasMessageContaining("companyId");

    }



    private FinancialSituation buildSituation(Long companyId) {

        FinancialSituation situation = new FinancialSituation();

        situation.setCompanyId(companyId);

        situation.setMonthlyRevenue(new BigDecimal("5000.00"));

        situation.setMonthlyExpenses(new BigDecimal("2000.00"));

        situation.setCashBalance(new BigDecimal("3000.00"));

        situation.setDirectorCurrentAccountBalance(new BigDecimal("1000.00"));

        situation.setRatioCACharges(new BigDecimal("2.50"));

        situation.setCashCoverageMonths(new BigDecimal("1.50"));

        situation.setDirectorCurrentAccountDebtorDays(0);

        situation.setCapturedAt(LocalDateTime.now());

        situation.setSource("MANUEL");

        situation.setNotes("Test");

        return situation;

    }

}

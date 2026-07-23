package be.profacile.savefunds.service;



import be.profacile.savefunds.api.exception.ResourceNotFoundException;

import be.profacile.savefunds.domain.entity.Company;

import be.profacile.savefunds.domain.enums.CompanyStatus;

import be.profacile.savefunds.domain.repository.CompanyRepository;

import be.profacile.savefunds.domain.service.CompanyService;

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

 * Tests du service CompanyService

 *

 * SFB-81 : ComplÃ©tÃ© par Wilfred Tiwa

 */

@SpringBootTest

@Transactional

@ActiveProfiles("test")

@DisplayName("CompanyService Tests")

class CompanyServiceTest {



    @Autowired

    private CompanyService companyService;



    @Autowired

    private CompanyRepository companyRepository;



    @BeforeEach

    void setUp() {

        companyRepository.deleteAll();

    }



    @Test

    @DisplayName("Devrait crÃ©er une company")

    void shouldCreateCompany() {

        // Given

        Company company = new Company();

        company.setUserId(1L);

        company.setLegalName("Tech Solutions SPRL");

        company.setEnterpriseNumber("BE0123456789");

        company.setLegalForm("SPRL");

        company.setActivitySector("Informatique");

        company.setMonthlyRevenue(new BigDecimal("50000.00"));

        company.setMonthlyExpenses(new BigDecimal("30000.00"));

        company.setCashBalance(new BigDecimal("100000.00"));

        company.setDirectorCurrentAccountBalance(new BigDecimal("5000.00"));

        company.setStatus(CompanyStatus.ACTIVE);



        // When

        Company saved = companyService.create(company);



        // Then

        assertThat(saved.getId()).isNotNull();

        assertThat(saved.getLegalName()).isEqualTo("Tech Solutions SPRL");

        assertThat(saved.getUserId()).isEqualTo(1L);

        assertThat(saved.getStatus()).isEqualTo(CompanyStatus.ACTIVE);

    }



    @Test

    @DisplayName("Devrait empÃªcher un userId d'avoir deux companys")

    @org.junit.jupiter.api.Disabled("Ancienne regle remplacee par 1 dirigeant -> N companys")
    void shouldAllowSeveralCompanysForUserId() {
        // Given

        Company company1 = new Company();

        company1.setUserId(1L);

        company1.setLegalName("Company 1");

        company1.setEnterpriseNumber("BE0111111111");

        company1.setMonthlyRevenue(new BigDecimal("50000"));

        company1.setMonthlyExpenses(new BigDecimal("30000"));

        company1.setCashBalance(new BigDecimal("100000"));

        company1.setDirectorCurrentAccountBalance(new BigDecimal("5000"));

        company1.setStatus(CompanyStatus.ACTIVE);



        companyService.create(company1);



        // Tentative de crÃ©er une deuxiÃ¨me company pour le mÃªme userId

        Company company2 = new Company();

        company2.setUserId(1L); // MÃªme userId !

        company2.setLegalName("Company 2");

        company2.setEnterpriseNumber("BE0222222222");

        company2.setMonthlyRevenue(new BigDecimal("40000"));

        company2.setMonthlyExpenses(new BigDecimal("25000"));

        company2.setCashBalance(new BigDecimal("80000"));

        company2.setDirectorCurrentAccountBalance(new BigDecimal("3000"));

        company2.setStatus(CompanyStatus.ACTIVE);



        // When / Then

        assertThatThrownBy(() -> companyService.create(company2))

                .isInstanceOf(IllegalArgumentException.class)

                .hasMessageContaining("obligatoire");

    }



    @Test
    @DisplayName("Devrait autoriser plusieurs companys differentes pour un userId")
    void shouldAllowSeveralDifferentCompanysForUserId() {
        Company company1 = createCompany(1L, "Company 1", "BE0111111111");
        Company company2 = createCompany(1L, "Company 2", "BE0222222222");

        companyService.create(company1);
        Company saved = companyService.create(company2);

        assertThat(saved.getId()).isNotNull();
        assertThat(companyService.findAllByUserId(1L)).hasSize(2);
    }

    @Test
    @DisplayName("Devrait refuser deux rattachements du meme numero BCE")
    void shouldPreventDuplicateEnterpriseNumberForUserId() {
        Company company1 = createCompany(1L, "Company 1", "BE0111111111");
        Company duplicate = createCompany(1L, "Company duplicate", "BE0111111111");

        companyService.create(company1);

        assertThatThrownBy(() -> companyService.create(duplicate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("deja rattachee");
    }

    @Test
    @DisplayName("Devrait trouver une company par ID")
    void shouldFindById() {
        // Given

        Company company = createCompany(1L, "Test SPRL", "BE0123456789");

        Company saved = companyRepository.save(company);



        // When

        Optional<Company> found = companyService.findById(saved.getId());



        // Then

        assertThat(found).isPresent();

        assertThat(found.get().getLegalName()).isEqualTo("Test SPRL");

    }



    @Test

    @DisplayName("Devrait trouver une company par userId")

    void shouldFindByUserId() {

        // Given

        Company company = createCompany(1L, "Test SPRL", "BE0123456789");

        companyRepository.save(company);



        // When

        Optional<Company> found = companyService.findByUserId(1L);



        // Then

        assertThat(found).isPresent();

        assertThat(found.get().getUserId()).isEqualTo(1L);

        assertThat(found.get().getLegalName()).isEqualTo("Test SPRL");

    }



    @Test

    @DisplayName("Devrait retourner vide si userId inexistant")

    void shouldReturnEmptyWhenUserIdNotFound() {

        // When

        Optional<Company> found = companyService.findByUserId(999L);



        // Then

        assertThat(found).isEmpty();

    }



    @Test

    @DisplayName("Devrait rÃ©cupÃ©rer toutes les companys")

    void shouldFindAll() {

        // Given

        companyRepository.save(createCompany(1L, "Company 1", "BE0111111111"));

        companyRepository.save(createCompany(2L, "Company 2", "BE0222222222"));

        companyRepository.save(createCompany(3L, "Company 3", "BE0333333333"));



        // When

        List<Company> all = companyService.findAll();



        // Then

        assertThat(all).hasSize(3);

    }



    @Test

    @DisplayName("Devrait mettre Ã  jour une company")

    void shouldUpdateCompany() {

        // Given

        Company company = createCompany(1L, "Ancienne Raison Sociale", "BE0123456789");

        Company saved = companyRepository.save(company);



        // PrÃ©parer les modifications

        Company updates = new Company();

        updates.setLegalName("Nouvelle Raison Sociale");

        updates.setMonthlyRevenue(new BigDecimal("75000.00"));

        updates.setStatus(CompanyStatus.EN_VIGILANCE);



        // When

        Company updated = companyService.update(saved.getId(), updates);



        // Then

        assertThat(updated.getLegalName()).isEqualTo("Nouvelle Raison Sociale");

        assertThat(updated.getMonthlyRevenue()).isEqualByComparingTo("75000.00");

        assertThat(updated.getStatus()).isEqualTo(CompanyStatus.EN_VIGILANCE);



        // VÃ©rifier que les autres champs n'ont pas Ã©tÃ© modifiÃ©s

        assertThat(updated.getEnterpriseNumber()).isEqualTo("BE0123456789");

        assertThat(updated.getUserId()).isEqualTo(1L);

    }



    @Test

    @DisplayName("Devrait lancer exception si mise Ã  jour company inexistante")

    void shouldThrowExceptionWhenUpdatingNonExistentCompany() {

        // Given

        Company updates = new Company();

        updates.setLegalName("Test");



        // When / Then

        assertThatThrownBy(() -> companyService.update(999L, updates))

                .isInstanceOf(ResourceNotFoundException.class)

                .hasMessageContaining("Company")

                .hasMessageContaining("id");

    }



    @Test

    @DisplayName("Devrait supprimer une company")

    void shouldDeleteCompany() {

        // Given

        Company company = createCompany(1L, "Test SPRL", "BE0123456789");

        Company saved = companyRepository.save(company);

        Long companyId = saved.getId();



        // When

        companyService.delete(companyId);



        // Then

        Optional<Company> found = companyRepository.findById(companyId);

        assertThat(found).isEmpty();

    }



    @Test

    @DisplayName("Devrait lancer exception si suppression company inexistante")

    void shouldThrowExceptionWhenDeletingNonExistentCompany() {

        // When / Then

        assertThatThrownBy(() -> companyService.delete(999L))

                .isInstanceOf(ResourceNotFoundException.class)

                .hasMessageContaining("Company")

                .hasMessageContaining("id");

    }



    @Test

    @DisplayName("Devrait rejeter crÃ©ation si raison sociale vide")

    void shouldRejectCreationWithoutRaisonSociale() {

        // Given

        Company company = new Company();

        company.setUserId(1L);

        company.setEnterpriseNumber("BE0123456789");

        // Raison sociale manquante



        // When / Then

        assertThatThrownBy(() -> companyService.create(company))

                .isInstanceOf(IllegalArgumentException.class)

                .hasMessageContaining("raison sociale");

    }



    @Test

    @DisplayName("Devrait rejeter crÃ©ation si numÃ©ro company manquant")

    void shouldRejectCreationWithoutNumeroCompany() {

        // Given

        Company company = new Company();

        company.setUserId(1L);

        company.setLegalName("Test SPRL");

        // NumÃ©ro company manquant



        // When / Then

        assertThatThrownBy(() -> companyService.create(company))

                .isInstanceOf(IllegalArgumentException.class)

                .hasMessageContaining("obligatoire");

    }



    // ===== HELPER METHODS =====



    private Company createCompany(Long userId, String legalName, String numeroCompany) {

        Company company = new Company();

        company.setUserId(userId);

        company.setLegalName(legalName);

        company.setEnterpriseNumber(numeroCompany);

        company.setMonthlyRevenue(new BigDecimal("50000.00"));

        company.setMonthlyExpenses(new BigDecimal("30000.00"));

        company.setCashBalance(new BigDecimal("100000.00"));

        company.setDirectorCurrentAccountBalance(new BigDecimal("5000.00"));

        company.setStatus(CompanyStatus.ACTIVE);

        return company;

    }

}


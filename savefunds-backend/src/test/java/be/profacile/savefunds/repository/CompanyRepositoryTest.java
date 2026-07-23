package be.profacile.savefunds.repository;



import be.profacile.savefunds.domain.entity.Company;

import be.profacile.savefunds.domain.enums.CompanyStatus;

import be.profacile.savefunds.domain.repository.CompanyRepository;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.test.context.ActiveProfiles;



import java.math.BigDecimal;

import java.util.Optional;



import static org.assertj.core.api.Assertions.assertThat;



/**

 * Tests du repository CompanyRepository

 *

 * SFB-67 : ComplÃ©tÃ© par Wilfred Tiwa

 */

@DataJpaTest

@ActiveProfiles("test")

class CompanyRepositoryTest {



    @Autowired

    private CompanyRepository companyRepository;



    @Test

    void shouldSaveCompany() {

        // Given

        Company company = new Company();



        // Informations de base

        company.setUserId(1L);

        company.setLegalName("Tech Solutions SPRL");

        company.setEnterpriseNumber("BE0123456789");

        company.setLegalForm("SPRL");

        company.setActivitySector("Informatique");



        // Données financières

        company.setMonthlyRevenue(new BigDecimal("50000.00"));

        company.setMonthlyExpenses(new BigDecimal("30000.00"));

        company.setCashBalance(new BigDecimal("100000.00"));

        company.setDirectorCurrentAccountBalance(new BigDecimal("5000.00"));



        // Statut

        company.setStatus(CompanyStatus.ACTIVE);



        // When

        Company saved = companyRepository.save(company);



        // Then

        // VÃ©rifier que l'ID a Ã©tÃ© gÃ©nÃ©rÃ©

        assertThat(saved.getId()).isNotNull();



        // VÃ©rifier les Données de base

        assertThat(saved.getLegalName()).isEqualTo("Tech Solutions SPRL");

        assertThat(saved.getEnterpriseNumber()).isEqualTo("BE0123456789");

        assertThat(saved.getUserId()).isEqualTo(1L);



        // VÃ©rifier le status

        assertThat(saved.getStatus()).isEqualTo(CompanyStatus.ACTIVE);



        // VÃ©rifier les Données financières

        assertThat(saved.getMonthlyRevenue()).isEqualByComparingTo("50000.00");

        assertThat(saved.getMonthlyExpenses()).isEqualByComparingTo("30000.00");

        assertThat(saved.getCashBalance()).isEqualByComparingTo("100000.00");

    }



    @Test

    void shouldFindByUserId() {

        // Given

        Company company = new Company();

        company.setUserId(1L);

        company.setLegalName("Consulting SA");

        company.setEnterpriseNumber("BE0987654321");

        company.setMonthlyRevenue(new BigDecimal("40000.00"));

        company.setMonthlyExpenses(new BigDecimal("25000.00"));

        company.setCashBalance(new BigDecimal("80000.00"));

        company.setDirectorCurrentAccountBalance(new BigDecimal("3000.00"));

        company.setStatus(CompanyStatus.ACTIVE);



        // Sauvegarder l'company

        companyRepository.save(company);



        // When

        Optional<Company> found = companyRepository.findByUserId(1L);



        // Then

        // VÃ©rifier que l'company a Ã©tÃ© trouvÃ©e

        assertThat(found).isPresent();



        // VÃ©rifier que le userId correspond

        assertThat(found.get().getUserId()).isEqualTo(1L);



        // VÃ©rifier la raison sociale

        assertThat(found.get().getLegalName()).isEqualTo("Consulting SA");

    }



    @Test

    void shouldReturnEmptyWhenUserIdNotFound() {

        // When

        // Rechercher un userId qui n'existe pas

        Optional<Company> found = companyRepository.findByUserId(999L);



        // Then

        // VÃ©rifier que le rÃ©sultat est vide

        assertThat(found).isEmpty();

    }



    @Test

    void shouldFindById() {

        // Given

        Company company = new Company();

        company.setUserId(2L);

        company.setLegalName("Design Studio SCRL");

        company.setEnterpriseNumber("BE0555666777");

        company.setMonthlyRevenue(new BigDecimal("35000.00"));

        company.setMonthlyExpenses(new BigDecimal("20000.00"));

        company.setCashBalance(new BigDecimal("70000.00"));

        company.setDirectorCurrentAccountBalance(new BigDecimal("2000.00"));

        company.setStatus(CompanyStatus.ACTIVE);



        // Sauvegarder et rÃ©cupÃ©rer l'ID gÃ©nÃ©rÃ©

        Company saved = companyRepository.save(company);

        Long companyId = saved.getId();



        // When

        Optional<Company> found = companyRepository.findById(companyId);



        // Then

        // VÃ©rifier que l'company a Ã©tÃ© trouvÃ©e

        assertThat(found).isPresent();



        // VÃ©rifier que l'ID correspond

        assertThat(found.get().getId()).isEqualTo(companyId);



        // VÃ©rifier la raison sociale

        assertThat(found.get().getLegalName()).isEqualTo("Design Studio SCRL");

    }



    @Test

    void shouldStoreAllFinancialData() {

        // Given

        Company company = new Company();

        company.setUserId(3L);

        company.setLegalName("Finance Corp");

        company.setEnterpriseNumber("BE0111222333");



        // DÃ©finir toutes les Données financières

        company.setMonthlyRevenue(new BigDecimal("75000.50"));

        company.setMonthlyExpenses(new BigDecimal("45000.25"));

        company.setCashBalance(new BigDecimal("150000.75"));

        company.setDirectorCurrentAccountBalance(new BigDecimal("-2500.00")); // Compte dÃ©biteur



        company.setStatus(CompanyStatus.EN_VIGILANCE);



        // When

        Company saved = companyRepository.save(company);



        // Then

        // VÃ©rifier que toutes les Données financières sont correctement sauvegardÃ©es

        assertThat(saved.getMonthlyRevenue()).isEqualByComparingTo("75000.50");

        assertThat(saved.getMonthlyExpenses()).isEqualByComparingTo("45000.25");

        assertThat(saved.getCashBalance()).isEqualByComparingTo("150000.75");

        assertThat(saved.getDirectorCurrentAccountBalance()).isEqualByComparingTo("-2500.00");



        // VÃ©rifier le status

        assertThat(saved.getStatus()).isEqualTo(CompanyStatus.EN_VIGILANCE);

    }



    @Test

    void shouldStoreCompanyWithOptionalFields() {

        // Given

        Company company = new Company();

        company.setUserId(4L);

        company.setLegalName("Startup Innov");

        company.setEnterpriseNumber("BE0444555666");



        // Champs optionnels remplis

        company.setLegalForm("SRL");

        company.setActivitySector("Technologies");



        // Données financières

        company.setMonthlyRevenue(new BigDecimal("20000.00"));

        company.setMonthlyExpenses(new BigDecimal("15000.00"));

        company.setCashBalance(new BigDecimal("50000.00"));

        company.setDirectorCurrentAccountBalance(new BigDecimal("1000.00"));



        company.setStatus(CompanyStatus.ACTIVE);



        // When

        Company saved = companyRepository.save(company);



        // Then

        // VÃ©rifier que les champs optionnels sont bien sauvegardÃ©s

        assertThat(saved.getLegalForm()).isEqualTo("SRL");

        assertThat(saved.getActivitySector()).isEqualTo("Technologies");

    }

}

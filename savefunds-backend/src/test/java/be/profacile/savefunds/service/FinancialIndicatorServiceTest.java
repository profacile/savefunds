package be.profacile.savefunds.service;



import be.profacile.savefunds.domain.entity.Company;

import be.profacile.savefunds.domain.service.FinancialIndicatorService;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;



import java.math.BigDecimal;

import java.time.LocalDate;

import java.util.Map;



import static org.assertj.core.api.Assertions.*;



/**

 * Tests du service FinancialIndicatorService

 *

 * SFB-94 : ComplÃ©tÃ©

 */

@SpringBootTest

@ActiveProfiles("test")

@DisplayName("FinancialIndicatorService Tests")

class FinancialIndicatorServiceTest {



    @Autowired

    private FinancialIndicatorService financialIndicatorService;



    @Test

    @DisplayName("Devrait calculer le ratio CA/Charges")

    void shouldCalculateRatioCACharges() {

        // Given

        BigDecimal ca = new BigDecimal("50000.00");

        BigDecimal charges = new BigDecimal("30000.00");



        // When

        BigDecimal ratio = financialIndicatorService.calculateRevenueExpensesRatio(ca, charges);



        // Then

        assertThat(ratio).isEqualByComparingTo(new BigDecimal("1.67"));

    }



    @Test

    @DisplayName("Devrait lancer exception si charges = 0")

    void shouldThrowExceptionWhenChargesIsZero() {

        // Given

        BigDecimal ca = new BigDecimal("50000.00");

        BigDecimal charges = BigDecimal.ZERO;



        // When / Then

        assertThatThrownBy(() -> financialIndicatorService.calculateRevenueExpensesRatio(ca, charges))

                .isInstanceOf(IllegalArgumentException.class)

                .hasMessageContaining("charges doivent");

    }



    @Test

    @DisplayName("Devrait calculer la trÃ©sorerie en mois")

    void shouldCalculateTresorerieEnMois() {

        // Given

        BigDecimal treso = new BigDecimal("100000.00");

        BigDecimal charges = new BigDecimal("30000.00");



        // When

        BigDecimal mois = financialIndicatorService.calculateCashCoverageMonths(treso, charges);



        // Then

        assertThat(mois).isEqualByComparingTo(new BigDecimal("3.33"));

    }



    @Test

    @DisplayName("Devrait calculer la durÃ©e compte courant dÃ©biteur")

    void shouldCalculateDureeCompteCourantDebiteur() {

        // Given

        BigDecimal solde = new BigDecimal("-5000.00");

        LocalDate dateDebut = LocalDate.now().minusDays(15);



        // When

        int duree = financialIndicatorService.calculateDirectorCurrentAccountDebtorDays(solde, dateDebut);



        // Then

        assertThat(duree).isEqualTo(15);

    }



    @Test

    @DisplayName("Devrait retourner 0 si compte non dÃ©biteur")

    void shouldReturnZeroWhenNotDebiteur() {

        // Given

        BigDecimal solde = new BigDecimal("5000.00");

        LocalDate dateDebut = LocalDate.now().minusDays(15);



        // When

        int duree = financialIndicatorService.calculateDirectorCurrentAccountDebtorDays(solde, dateDebut);



        // Then

        assertThat(duree).isEqualTo(0);

    }



    @Test

    @DisplayName("Devrait calculer le montant max prÃ©levable")

    void shouldCalculateMontantMaxPrelevable() {

        // Given

        BigDecimal treso = new BigDecimal("100000.00");

        BigDecimal charges = new BigDecimal("30000.00");

        int seuil = 3; // 3 mois



        // When

        BigDecimal montantMax = financialIndicatorService.calculateMaximumWithdrawableAmount(treso, charges, seuil);



        // Then

        // 100000 - (30000 * 3) = 10000

        assertThat(montantMax).isEqualByComparingTo(new BigDecimal("10000.00"));

    }



    @Test

    @DisplayName("Devrait retourner 0 si trÃ©sorerie insuffisante")

    void shouldReturnZeroWhenInsufficientTresorerie() {

        // Given

        BigDecimal treso = new BigDecimal("50000.00");

        BigDecimal charges = new BigDecimal("30000.00");

        int seuil = 3;



        // When

        BigDecimal montantMax = financialIndicatorService.calculateMaximumWithdrawableAmount(treso, charges, seuil);



        // Then

        // 50000 - (30000 * 3) = -40000 â†’ retourne 0

        assertThat(montantMax).isEqualByComparingTo(BigDecimal.ZERO);

    }



    @Test

    @DisplayName("Devrait valider que les Données sont complÃ¨tes")

    void shouldValidateCompleteData() {

        // Given

        Company company = new Company();

        company.setMonthlyRevenue(new BigDecimal("50000"));

        company.setMonthlyExpenses(new BigDecimal("30000"));

        company.setCashBalance(new BigDecimal("100000"));

        company.setDirectorCurrentAccountBalance(new BigDecimal("5000"));



        // When

        boolean complete = financialIndicatorService.hasCompleteFinancialData(company);



        // Then

        assertThat(complete).isTrue();

    }



    @Test

    @DisplayName("Devrait dÃ©tecter Données incomplÃ¨tes")

    void shouldDetectIncompleteData() {

        // Given

        Company company = new Company();

        company.setMonthlyExpenses(new BigDecimal("30000"));

        // Manque : CA, trÃ©sorerie, compte courant



        // When

        boolean complete = financialIndicatorService.hasCompleteFinancialData(company);



        // Then

        assertThat(complete).isFalse();

    }



    @Test

    @DisplayName("Devrait calculer tous les indicateurs")

    void shouldCalculateAllIndicators() {

        // Given

        Company company = new Company();

        company.setId(1L);

        company.setMonthlyRevenue(new BigDecimal("50000"));

        company.setMonthlyExpenses(new BigDecimal("30000"));

        company.setCashBalance(new BigDecimal("100000"));

        company.setDirectorCurrentAccountBalance(new BigDecimal("5000"));



        // When

        Map<String, Object> indicateurs = financialIndicatorService.calculateAllFinancialIndicators(company);



        // Then

        assertThat(indicateurs).isNotNull();

        assertThat(indicateurs).containsKeys(

                "ratioCACharges",

                "cashCoverageMonths",

                "directorCurrentAccountDebtorDays",

                "maxWithdrawableAmount"

        );

    }

}

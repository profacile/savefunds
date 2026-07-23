package be.profacile.savefunds.api.mapper;



import be.profacile.savefunds.api.dto.request.CreateFinancialSituationRequest;

import be.profacile.savefunds.api.dto.response.FinancialSituationResponse;

import be.profacile.savefunds.domain.entity.FinancialSituation;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;



import java.math.BigDecimal;

import java.time.LocalDateTime;



import static org.assertj.core.api.Assertions.assertThat;



class FinancialSituationMapperTest {



    private final FinancialSituationMapper mapper = Mappers.getMapper(FinancialSituationMapper.class);



    @Test

    @DisplayName("Devrait mapper FinancialSituation â†’ FinancialSituationResponse")

    void shouldMapEntityToResponse() {

        FinancialSituation situation = new FinancialSituation();

        situation.setId(1L);

        situation.setCompanyId(10L);

        situation.setMonthlyRevenue(new BigDecimal("50000.00"));

        situation.setMonthlyExpenses(new BigDecimal("30000.00"));

        situation.setCashBalance(new BigDecimal("100000.00"));

        situation.setDirectorCurrentAccountBalance(new BigDecimal("5000.00"));

        situation.setRatioCACharges(new BigDecimal("1.67"));

        situation.setCashCoverageMonths(new BigDecimal("3.33"));

        situation.setDirectorCurrentAccountDebtorDays(0);

        situation.setSource("MANUEL");

        situation.setCapturedAt(LocalDateTime.of(2026, 4, 6, 14, 30));



        FinancialSituationResponse response = mapper.toResponse(situation);



        assertThat(response.getId()).isEqualTo(1L);

        assertThat(response.getCompanyId()).isEqualTo(10L);

        assertThat(response.getMonthlyRevenue()).isEqualByComparingTo("50000.00");

        assertThat(response.getRatioCACharges()).isEqualByComparingTo("1.67");

        assertThat(response.getCashCoverageMonths()).isEqualByComparingTo("3.33");

        assertThat(response.getSource()).isEqualTo("MANUEL");

        assertThat(response.getCapturedAt()).isEqualTo(LocalDateTime.of(2026, 4, 6, 14, 30));

    }



    @Test

    @DisplayName("Devrait mapper CreateRequest â†’ Entity en ignorant les champs calculÃ©s")

    void shouldMapCreateRequestToEntityIgnoringCalculatedFields() {

        CreateFinancialSituationRequest request = new CreateFinancialSituationRequest();

        request.setCompanyId(10L);

        request.setMonthlyRevenue(new BigDecimal("50000.00"));

        request.setMonthlyExpenses(new BigDecimal("30000.00"));

        request.setCashBalance(new BigDecimal("100000.00"));

        request.setDirectorCurrentAccountBalance(new BigDecimal("5000.00"));

        request.setSource("MANUEL");



        FinancialSituation entity = mapper.toEntity(request);



        assertThat(entity.getId()).isNull();                        // ignorÃ©

        assertThat(entity.getCapturedAt()).isNotNull(); // dÃ©fini par le constructeur de l'entitÃ©

        assertThat(entity.getRatioCACharges()).isNull();           // ignorÃ© â€” calculÃ©

        assertThat(entity.getCashCoverageMonths()).isNull();         // ignorÃ© â€” calculÃ©

        assertThat(entity.getCompanyId()).isEqualTo(10L);

        assertThat(entity.getMonthlyRevenue()).isEqualByComparingTo("50000.00");

        assertThat(entity.getSource()).isEqualTo("MANUEL");

    }

}

package be.profacile.savefunds.api.mapper;



import be.profacile.savefunds.api.dto.response.AnalysisResultResponse;

import be.profacile.savefunds.domain.entity.WithdrawalAnalysis;

import be.profacile.savefunds.domain.entity.AnalysisResult;

import be.profacile.savefunds.domain.enums.Decision;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;



import java.math.BigDecimal;



import static org.assertj.core.api.Assertions.assertThat;



class AnalysisResultMapperTest {



    private final AnalysisResultMapper mapper = Mappers.getMapper(AnalysisResultMapper.class);



    @Test

    @DisplayName("Devrait mapper AnalysisResult â†’ AnalysisResultResponse")

    void shouldMapEntityToResponse() {

        WithdrawalAnalysis analysis = new WithdrawalAnalysis();

        analysis.setId(5L);



        AnalysisResult result = AnalysisResult.builder()

                .analysis(analysis)

                .globalDecision(Decision.VERT)

                .cashScore(new BigDecimal("3.50"))

                .revenueExpensesRatioScore(new BigDecimal("1.67"))

                .directorCurrentAccountScore(0)

                .cashDecision(Decision.VERT)

                .revenueExpensesRatioDecision(Decision.VERT)

                .directorCurrentAccountDecision(Decision.VERT)

                .globalRecommendation("Situation saine.")

                .build();

        result.setId(1L);



        AnalysisResultResponse response = mapper.toResponse(result);



        assertThat(response.getId()).isEqualTo(1L);

        assertThat(response.getAnalysisId()).isEqualTo(5L);   // analysis.id â†’ analysisId

        assertThat(response.getGlobalDecision()).isEqualTo(Decision.VERT);

        assertThat(response.getCashScore()).isEqualByComparingTo("3.50");

        assertThat(response.getRevenueExpensesRatioScore()).isEqualByComparingTo("1.67");

        assertThat(response.getDirectorCurrentAccountScore()).isEqualTo(0);

        assertThat(response.getGlobalRecommendation()).isEqualTo("Situation saine.");

    }



    @Test

    @DisplayName("Devrait retourner null si entitÃ© null")

    void shouldReturnNullWhenEntityIsNull() {

        assertThat(mapper.toResponse(null)).isNull();

    }



    @Test

    @DisplayName("updateFromEntity ne doit pas modifier id ni analysis")

    void shouldNotOverwriteIdAndAnalyseOnUpdate() {

        WithdrawalAnalysis analysis = new WithdrawalAnalysis();

        analysis.setId(5L);



        AnalysisResult target = AnalysisResult.builder()

                .analysis(analysis)

                .globalDecision(Decision.ORANGE)

                .cashScore(new BigDecimal("2.00"))

                .build();

        target.setId(1L);



        AnalysisResult source = AnalysisResult.builder()

                .globalDecision(Decision.VERT)

                .cashScore(new BigDecimal("4.00"))

                .build();

        source.setId(999L); // ne doit pas Ã©craser



        mapper.updateFromEntity(source, target);



        assertThat(target.getId()).isEqualTo(1L);              // protÃ©gÃ©

        assertThat(target.getAnalysis().getId()).isEqualTo(5L); // protÃ©gÃ©

        assertThat(target.getGlobalDecision()).isEqualTo(Decision.VERT);    // mis Ã  jour

        assertThat(target.getCashScore()).isEqualByComparingTo("4.00"); // mis Ã  jour

    }

}

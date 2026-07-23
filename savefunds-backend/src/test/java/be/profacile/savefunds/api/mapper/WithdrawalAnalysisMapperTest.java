package be.profacile.savefunds.api.mapper;



import be.profacile.savefunds.api.dto.response.WithdrawalAnalysisResponse;

import be.profacile.savefunds.domain.entity.WithdrawalAnalysis;

import be.profacile.savefunds.domain.entity.Company;

import be.profacile.savefunds.domain.enums.AnalysisStatus;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;



import java.math.BigDecimal;



import static org.assertj.core.api.Assertions.assertThat;



class WithdrawalAnalysisMapperTest {



    private final WithdrawalAnalysisMapper mapper = Mappers.getMapper(WithdrawalAnalysisMapper.class);



    @Test

    @DisplayName("Devrait mapper WithdrawalAnalysis â†’ WithdrawalAnalysisResponse")

    void shouldMapEntityToResponse() {

        Company company = new Company();

        company.setId(42L);



        WithdrawalAnalysis analysis = WithdrawalAnalysis.builder()

                .company(company)

                .requestedAmount(new BigDecimal("5000.00"))

                .status(AnalysisStatus.EN_ATTENTE)

                .build();

        // Simuler l'ID gÃ©nÃ©rÃ©

        analysis.setId(1L);



        WithdrawalAnalysisResponse response = mapper.toResponse(analysis);



        assertThat(response.getId()).isEqualTo(1L);

        assertThat(response.getCompanyId()).isEqualTo(42L);    // company.id â†’ companyId

        assertThat(response.getRequestedAmount()).isEqualByComparingTo("5000.00");

        assertThat(response.getStatus()).isEqualTo(AnalysisStatus.EN_ATTENTE);

    }



    @Test

    @DisplayName("Devrait retourner null si entitÃ© null")

    void shouldReturnNullWhenEntityIsNull() {

        assertThat(mapper.toResponse(null)).isNull();

    }



    @Test

    @DisplayName("updateFromEntity ne doit pas Ã©craser les champs null")

    void shouldNotOverwriteWithNullOnUpdate() {

        WithdrawalAnalysis target = WithdrawalAnalysis.builder()

                .requestedAmount(new BigDecimal("5000.00"))

                .status(AnalysisStatus.EN_ATTENTE)

                .build();



        WithdrawalAnalysis source = new WithdrawalAnalysis();

        // source a tout Ã  null



        mapper.updateFromEntity(source, target);



        // Champs null dans source â†’ target inchangÃ©

        assertThat(target.getRequestedAmount()).isEqualByComparingTo("5000.00");

        assertThat(target.getStatus()).isEqualTo(AnalysisStatus.EN_ATTENTE);

    }

}

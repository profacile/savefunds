package be.profacile.savefunds.api.mapper;



import be.profacile.savefunds.api.dto.request.CreateCompanyRequest;

import be.profacile.savefunds.api.dto.request.UpdateCompanyRequest;

import be.profacile.savefunds.api.dto.response.CompanyResponse;

import be.profacile.savefunds.domain.entity.Company;

import be.profacile.savefunds.domain.enums.CompanyStatus;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;



import java.math.BigDecimal;



import static org.assertj.core.api.Assertions.assertThat;



class CompanyMapperTest {



    private final CompanyMapper mapper = Mappers.getMapper(CompanyMapper.class);



    @Test

    @DisplayName("Devrait mapper Company â†’ CompanyResponse")

    void shouldMapEntityToResponse() {

        Company company = new Company();

        company.setId(1L);

        company.setUserId(10L);

        company.setLegalName("Profacile SRL");

        company.setEnterpriseNumber("BE0123456789");

        company.setMonthlyRevenue(new BigDecimal("50000.00"));

        company.setMonthlyExpenses(new BigDecimal("30000.00"));

        company.setCashBalance(new BigDecimal("100000.00"));

        company.setDirectorCurrentAccountBalance(new BigDecimal("5000.00"));

        company.setStatus(CompanyStatus.ACTIVE);



        CompanyResponse response = mapper.toResponse(company);



        assertThat(response.getId()).isEqualTo(1L);

        assertThat(response.getUserId()).isEqualTo(10L);

        assertThat(response.getLegalName()).isEqualTo("Profacile SRL");

        assertThat(response.getEnterpriseNumber()).isEqualTo("BE0123456789");

        assertThat(response.getMonthlyRevenue()).isEqualByComparingTo("50000.00");

        assertThat(response.getMonthlyExpenses()).isEqualByComparingTo("30000.00");

        assertThat(response.getStatus()).isEqualTo(CompanyStatus.ACTIVE);

    }



    @Test

    @DisplayName("Devrait mapper CreateCompanyRequest â†’ Company")

    void shouldMapCreateRequestToEntity() {

        CreateCompanyRequest request = new CreateCompanyRequest();

        request.setUserId(10L);

        request.setLegalName("Profacile SRL");

        request.setEnterpriseNumber("BE0123456789");

        request.setCashBalance(new BigDecimal("100000.00"));



        Company entity = mapper.toEntity(request);



        assertThat(entity.getId()).isNull();          // ignorÃ©

        assertThat(entity.getStatus()).isEqualTo(CompanyStatus.EN_CREATION);

        assertThat(entity.getCreatedAt()).isNull();   // ignorÃ©

        assertThat(entity.getUserId()).isEqualTo(10L);

        assertThat(entity.getLegalName()).isEqualTo("Profacile SRL");

        assertThat(entity.getCashBalance()).isEqualByComparingTo("100000.00");

    }



    @Test

    @DisplayName("Devrait mapper UpdateCompanyRequest â†’ Company sans toucher aux champs protÃ©gÃ©s")

    void shouldMapUpdateRequestToEntityIgnoringProtectedFields() {

        UpdateCompanyRequest request = new UpdateCompanyRequest();

        request.setLegalName("Nouvelle Raison Sociale");

        request.setLegalForm("SA");



        Company entity = mapper.toEntity(request);



        assertThat(entity.getId()).isNull();               // ignorÃ©

        assertThat(entity.getUserId()).isNull();           // ignorÃ©

        assertThat(entity.getEnterpriseNumber()).isNull(); // ignorÃ©

        assertThat(entity.getLegalName()).isEqualTo("Nouvelle Raison Sociale");

        assertThat(entity.getLegalForm()).isEqualTo("SA");

    }



    @Test

    @DisplayName("updateFromEntity ne doit pas Ã©craser les champs protÃ©gÃ©s")

    void shouldNotOverwriteProtectedFieldsOnUpdate() {

        Company target = new Company();

        target.setId(1L);

        target.setUserId(10L);

        target.setEnterpriseNumber("BE0123456789");

        target.setLegalName("Ancienne Raison");



        Company source = new Company();

        source.setLegalName("Nouvelle Raison");

        // id, userId, numeroCompany ignorÃ©s mÃªme si prÃ©sents dans source

        source.setId(999L);

        source.setUserId(999L);



        mapper.updateFromEntity(source, target);



        assertThat(target.getId()).isEqualTo(1L);                    // protÃ©gÃ©

        assertThat(target.getUserId()).isEqualTo(10L);               // protÃ©gÃ©

        assertThat(target.getEnterpriseNumber()).isEqualTo("BE0123456789"); // protÃ©gÃ©

        assertThat(target.getLegalName()).isEqualTo("Nouvelle Raison"); // mis Ã  jour

    }

}

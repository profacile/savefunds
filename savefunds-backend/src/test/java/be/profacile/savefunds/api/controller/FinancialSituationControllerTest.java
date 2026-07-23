package be.profacile.savefunds.api.controller;



import be.profacile.savefunds.domain.entity.Company;

import be.profacile.savefunds.domain.entity.FinancialSituation;

import be.profacile.savefunds.domain.enums.CompanyStatus;

import be.profacile.savefunds.domain.repository.CompanyRepository;

import be.profacile.savefunds.domain.repository.FinancialSituationRepository;

import be.profacile.savefunds.api.dto.request.CreateFinancialSituationRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.web.servlet.MockMvc;



import java.math.BigDecimal;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest

@AutoConfigureMockMvc(addFilters = false)

@ActiveProfiles("test")

@DisplayName("FinancialSituationController Tests")

class FinancialSituationControllerTest {



    @Autowired

    private MockMvc mockMvc;



    @Autowired

    private ObjectMapper objectMapper;



    @Autowired

    private FinancialSituationRepository financialSituationRepository;



    @Autowired

    private CompanyRepository companyRepository;



    private Company company;

    private FinancialSituation situation;



    @BeforeEach

    void setUp() {

        financialSituationRepository.deleteAll();

        companyRepository.deleteAll();



        company = new Company();

        company.setUserId(1L);

        company.setLegalName("Test SRL");

        company.setEnterpriseNumber("BE0123456789");

        company.setStatus(CompanyStatus.ACTIVE);

        company = companyRepository.save(company);



        situation = new FinancialSituation();

        situation.setCompanyId(company.getId());

        situation.setMonthlyRevenue(new BigDecimal("50000.00"));

        situation.setMonthlyExpenses(new BigDecimal("30000.00"));

        situation.setCashBalance(new BigDecimal("100000.00"));

        situation.setDirectorCurrentAccountBalance(new BigDecimal("5000.00"));

        situation = financialSituationRepository.save(situation);

    }



    @Test

    @DisplayName("GET /situations/{id} - rÃ©cupÃ©rer par ID")

    void shouldGetById() throws Exception {

        mockMvc.perform(get("/api/v1/situations/" + situation.getId()))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.id").value(situation.getId()))

                .andExpect(jsonPath("$.companyId").value(company.getId()));

    }



    @Test

    @DisplayName("GET /situations/{id} - 404 si inexistante")

    void shouldReturn404WhenNotFound() throws Exception {

        mockMvc.perform(get("/api/v1/situations/999"))

                .andExpect(status().isNotFound());

    }



    @Test

    @DisplayName("GET /situations/company/{id} - historique")

    void shouldGetByCompanyId() throws Exception {

        mockMvc.perform(get("/api/v1/situations/company/" + company.getId()))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$").isArray())

                .andExpect(jsonPath("$.length()").value(1));

    }



    @Test

    @DisplayName("GET /situations/company/{id}/last - derniÃ¨re situation")

    void shouldGetLast() throws Exception {

        mockMvc.perform(get("/api/v1/situations/company/" + company.getId() + "/last"))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.companyId").value(company.getId()));

    }



    @Test

    @DisplayName("POST /situations - crÃ©er une situation")

    void shouldCreateSituation() throws Exception {

        CreateFinancialSituationRequest request = new CreateFinancialSituationRequest();

        request.setCompanyId(company.getId());

        request.setMonthlyRevenue(new BigDecimal("60000.00"));

        request.setMonthlyExpenses(new BigDecimal("35000.00"));

        request.setCashBalance(new BigDecimal("120000.00"));

        request.setDirectorCurrentAccountBalance(new BigDecimal("8000.00"));



        mockMvc.perform(post("/api/v1/situations")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.id").exists())

                .andExpect(jsonPath("$.companyId").value(company.getId()));

    }



    @Test

    @DisplayName("DELETE /situations/{id} - suppression")

    void shouldDeleteSituation() throws Exception {

        mockMvc.perform(delete("/api/v1/situations/" + situation.getId()))

                .andExpect(status().isNoContent());



        mockMvc.perform(get("/api/v1/situations/" + situation.getId()))

                .andExpect(status().isNotFound());

    }

}

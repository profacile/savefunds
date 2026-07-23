package be.profacile.savefunds.api.controller;



import be.profacile.savefunds.api.dto.request.CreateCompanyRequest;

import be.profacile.savefunds.api.dto.request.UpdateCompanyRequest;

import be.profacile.savefunds.domain.entity.Company;

import be.profacile.savefunds.domain.entity.User;

import be.profacile.savefunds.domain.enums.Role;

import be.profacile.savefunds.domain.enums.CompanyStatus;

import be.profacile.savefunds.domain.repository.CompanyRepository;

import be.profacile.savefunds.domain.repository.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.web.servlet.MockMvc;



import java.math.BigDecimal;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest

@AutoConfigureMockMvc(addFilters = false)

@ActiveProfiles("test")

@DisplayName("CompanyController Tests")

@WithMockUser(username = "dirigeant@example.com")

class CompanyControllerTest {



    @Autowired

    private MockMvc mockMvc;



    @Autowired

    private ObjectMapper objectMapper;



    @Autowired

    private CompanyRepository companyRepository;



    @Autowired

    private UserRepository userRepository;



    private Company company;

    private User user;



    @BeforeEach

    void setUp() {

        companyRepository.deleteAll();

        userRepository.deleteAll();



        user = new User();

        user.setEmail("dirigeant@example.com");

        user.setPasswordHash("hash");

        user.setLastName("Dirigeant");

        user.setFirstName("Test");

        user.setRole(Role.DIRIGEANT);

        user.setEmailVerified(true);

        user = userRepository.save(user);



        company = new Company();

        company.setUserId(user.getId());

        company.setLegalName("Profacile SRL");

        company.setEnterpriseNumber("BE0123456789");

        company.setMonthlyRevenue(new BigDecimal("50000.00"));

        company.setMonthlyExpenses(new BigDecimal("30000.00"));

        company.setCashBalance(new BigDecimal("100000.00"));

        company.setDirectorCurrentAccountBalance(new BigDecimal("5000.00"));

        company.setStatus(CompanyStatus.ACTIVE);

        company = companyRepository.save(company);

    }



    @Test

    @DisplayName("POST /companys - crÃƒÂ©er une company")

    void shouldCreateCompany() throws Exception {

        companyRepository.deleteAll();



        CreateCompanyRequest request = new CreateCompanyRequest();

        request.setUserId(user.getId());

        request.setLegalName("Nouvelle SRL");

        request.setEnterpriseNumber("BE0987654321");

        request.setMonthlyRevenue(new BigDecimal("50000.00"));

        request.setMonthlyExpenses(new BigDecimal("30000.00"));

        request.setCashBalance(new BigDecimal("100000.00"));



        mockMvc.perform(post("/api/v1/companies")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.id").exists())

                .andExpect(jsonPath("$.legalName").value("Nouvelle SRL"));

    }



    @Test

    @DisplayName("POST /companys - 400 si userId a dÃƒÂ©jÃƒÂ  une company")

    void shouldCreateSecondCompanyForSameUser() throws Exception {

        // company avec userId=1 dÃƒÂ©jÃƒÂ  crÃƒÂ©ÃƒÂ©e dans setUp()

        CreateCompanyRequest request = new CreateCompanyRequest();

        request.setUserId(user.getId());

        request.setLegalName("DeuxiÃƒÂ¨me SRL");

        request.setEnterpriseNumber("BE0987654321");

        request.setMonthlyRevenue(new BigDecimal("50000.00"));

        request.setMonthlyExpenses(new BigDecimal("30000.00"));

        request.setCashBalance(new BigDecimal("100000.00"));



        mockMvc.perform(post("/api/v1/companies")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.userId").value(user.getId()))

                .andExpect(jsonPath("$.legalName").exists());

    }



    @Test

    @DisplayName("POST /companys - 400 si meme numero BCE deja rattache")

    void shouldReturn400WhenEnterpriseNumberAlreadyAttachedToUser() throws Exception {

        CreateCompanyRequest request = new CreateCompanyRequest();

        request.setUserId(user.getId());

        request.setLegalName("Duplicat SRL");

        request.setEnterpriseNumber("BE0123456789");

        request.setMonthlyRevenue(new BigDecimal("50000.00"));

        request.setMonthlyExpenses(new BigDecimal("30000.00"));

        request.setCashBalance(new BigDecimal("100000.00"));



        mockMvc.perform(post("/api/v1/companies")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isBadRequest());

    }



    @Test

    @DisplayName("GET /companys/{id} - rÃƒÂ©cupÃƒÂ©rer par ID")

    void shouldGetCompanyById() throws Exception {

        mockMvc.perform(get("/api/v1/companies/" + company.getId()))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.id").value(company.getId()))

                .andExpect(jsonPath("$.legalName").value("Profacile SRL"));

    }



    @Test

    @DisplayName("GET /companys/{id} - 404 si inexistante")

    void shouldReturn404WhenNotFound() throws Exception {

        mockMvc.perform(get("/api/v1/companies/999"))

                .andExpect(status().isNotFound());

    }



    @Test

    @DisplayName("GET /companys/user/{userId} - rÃƒÂ©cupÃƒÂ©rer par userId")

    void shouldGetCompanyByUserId() throws Exception {

        mockMvc.perform(get("/api/v1/companies/user/" + user.getId()))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.userId").value(user.getId()));

    }



    @Test

    @DisplayName("GET /companys/user/{userId}/exists - vÃƒÂ©rifier existence")

    void shouldCheckCompanyExists() throws Exception {

        mockMvc.perform(get("/api/v1/companies/user/" + user.getId() + "/exists"))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$").value(true));



        mockMvc.perform(get("/api/v1/companies/user/999/exists"))

                .andExpect(status().isForbidden());

    }



    @Test

    @DisplayName("PUT /companys/{id} - mise ÃƒÂ  jour")

    void shouldUpdateCompany() throws Exception {

        UpdateCompanyRequest request = new UpdateCompanyRequest();

        request.setLegalName("Profacile SRL Updated");



        mockMvc.perform(put("/api/v1/companies/" + company.getId())

                        .contentType(MediaType.APPLICATION_JSON)

                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.legalName").value("Profacile SRL Updated"));

    }



    @Test

    @DisplayName("DELETE /companys/{id} - suppression")

    void shouldDeleteCompany() throws Exception {

        mockMvc.perform(delete("/api/v1/companies/" + company.getId()))

                .andExpect(status().isNoContent());



        mockMvc.perform(get("/api/v1/companies/" + company.getId()))

                .andExpect(status().isNotFound());

    }

}





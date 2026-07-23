package be.profacile.savefunds.api.controller;



import be.profacile.savefunds.api.dto.request.CreateWithdrawalAnalysisRequest;
import be.profacile.savefunds.domain.entity.WithdrawalAnalysis;
import be.profacile.savefunds.domain.entity.Company;
import be.profacile.savefunds.domain.entity.User;
import be.profacile.savefunds.domain.enums.Role;
import be.profacile.savefunds.domain.enums.AnalysisStatus;
import be.profacile.savefunds.domain.repository.WithdrawalAnalysisRepository;
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



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;    // âœ…

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;   // âœ…

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath; // âœ…

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest

@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("WithdrawalAnalysisController Tests")
@WithMockUser(username = "dirigeant@example.com")
class WithdrawalAnalysisControllerTest {


    @Autowired

    private MockMvc mockMvc;



    @Autowired

    private ObjectMapper objectMapper;



    @Autowired

    private WithdrawalAnalysisRepository withdrawalAnalysisRepository;



    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    private Company company;
    private User user;


    @BeforeEach

    void setUp() {

        withdrawalAnalysisRepository.deleteAll();
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
        company.setLegalName("Test Company");

        company.setEnterpriseNumber("BE0123456789");

        company.setMonthlyRevenue(BigDecimal.valueOf(10000));

        company.setMonthlyExpenses(BigDecimal.valueOf(5000));

        company.setCashBalance(BigDecimal.valueOf(20000));

        company.setDirectorCurrentAccountBalance(BigDecimal.valueOf(1000));

        company = companyRepository.save(company);

    }



    @Test

    @DisplayName("POST /analysiss - crÃ©er une analysis")

    void shouldCreateAnalyse() throws Exception {



        // Le controller attend un CreateWithdrawalAnalysisRequest, pas une entitÃ©

        CreateWithdrawalAnalysisRequest request = new CreateWithdrawalAnalysisRequest();

        request.setCompanyId(company.getId());

        request.setRequestedAmount(BigDecimal.valueOf(1000));



        mockMvc.perform(post("/api/v1/analyses")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.id").exists())

                .andExpect(jsonPath("$.requestedAmount").value(1000))   // â† champ rÃ©el

                .andExpect(jsonPath("$.status").value("EN_ATTENTE"));

    }



    @Test

    @DisplayName("POST /analysiss - 404 si company inexistante")

    void shouldReturn404WhenCompanyNotFound() throws Exception {



        CreateWithdrawalAnalysisRequest request = new CreateWithdrawalAnalysisRequest();

        request.setCompanyId(999L);

        request.setRequestedAmount(BigDecimal.valueOf(1000));



        mockMvc.perform(post("/api/v1/analyses")

                        .contentType(MediaType.APPLICATION_JSON)

                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isNotFound());

    }



    @Test

    @DisplayName("GET /analysiss/company/{id} - liste des analysiss")

    void shouldGetAnalysesByCompany() throws Exception {



        // PrÃ©parer une analysis en base avec les bons champs

        withdrawalAnalysisRepository.save(WithdrawalAnalysis.builder()

                .company(company)

                .requestedAmount(BigDecimal.valueOf(500))

                .status(AnalysisStatus.EN_ATTENTE)

                .build());



        mockMvc.perform(get("/api/v1/analyses/company/" + company.getId()))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$").isArray())

                .andExpect(jsonPath("$.length()").value(1));

    }



    @Test

    @DisplayName("GET /analysiss/{id} - rÃ©cupÃ©rer une analysis")

    void shouldGetAnalyseById() throws Exception {



        WithdrawalAnalysis saved = withdrawalAnalysisRepository.save(WithdrawalAnalysis.builder()

                .company(company)

                .requestedAmount(BigDecimal.valueOf(2000))

                .status(AnalysisStatus.EN_ATTENTE)

                .build());



        mockMvc.perform(get("/api/v1/analyses/" + saved.getId()))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.id").value(saved.getId()))

                .andExpect(jsonPath("$.requestedAmount").value(2000));

    }



    @Test

    @DisplayName("GET /analysiss/{id} - 404 si analysis inexistante")

    void shouldReturn404WhenAnalyseNotFound() throws Exception {

        mockMvc.perform(get("/api/v1/analyses/999"))

                .andExpect(status().isNotFound());

    }



    @Test

    @DisplayName("POST /analysiss/{id}/result - lancer l'analysis")

    void shouldLancerAnalyse() throws Exception {



        WithdrawalAnalysis saved = withdrawalAnalysisRepository.save(WithdrawalAnalysis.builder()

                .company(company)

                .requestedAmount(BigDecimal.valueOf(1000))

                .status(AnalysisStatus.EN_ATTENTE)

                .build());



        mockMvc.perform(post("/api/v1/analyses/" + saved.getId() + "/result"))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.globalDecision").exists())   // â† champ rÃ©el de AnalysisResultResponse

                .andExpect(jsonPath("$.globalDecision").isNotEmpty());

    }



    @Test

    @DisplayName("POST /analysiss/{id}/result - 400 si analysis dÃ©jÃ  TERMINEE")

    void shouldReturn400WhenAnalyseDejaTerminee() throws Exception {



        WithdrawalAnalysis saved = withdrawalAnalysisRepository.save(WithdrawalAnalysis.builder()

                .company(company)

                .requestedAmount(BigDecimal.valueOf(1000))

                .status(AnalysisStatus.TERMINEE)

                .build());



        mockMvc.perform(post("/api/v1/analyses/" + saved.getId() + "/result"))

                .andExpect(status().isBadRequest());

    }

}


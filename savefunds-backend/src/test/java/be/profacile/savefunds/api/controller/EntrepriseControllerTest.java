package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.request.CreateEntrepriseRequest;
import be.profacile.savefunds.api.dto.request.UpdateEntrepriseRequest;
import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.entity.User;
import be.profacile.savefunds.domain.enums.Role;
import be.profacile.savefunds.domain.enums.StatutEntreprise;
import be.profacile.savefunds.domain.repository.EntrepriseRepository;
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
@DisplayName("EntrepriseController Tests")
@WithMockUser(username = "dirigeant@example.com")
class EntrepriseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @Autowired
    private UserRepository userRepository;

    private Entreprise entreprise;
    private User user;

    @BeforeEach
    void setUp() {
        entrepriseRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setEmail("dirigeant@example.com");
        user.setPasswordHash("hash");
        user.setNom("Dirigeant");
        user.setPrenom("Test");
        user.setRole(Role.DIRIGEANT);
        user.setEmailVerified(true);
        user = userRepository.save(user);

        entreprise = new Entreprise();
        entreprise.setUserId(user.getId());
        entreprise.setRaisonSociale("Profacile SRL");
        entreprise.setNumeroEntreprise("BE0123456789");
        entreprise.setChiffreAffairesMensuel(new BigDecimal("50000.00"));
        entreprise.setChargesMensuelles(new BigDecimal("30000.00"));
        entreprise.setTresorerie(new BigDecimal("100000.00"));
        entreprise.setSoldeCompteCourant(new BigDecimal("5000.00"));
        entreprise.setStatut(StatutEntreprise.ACTIVE);
        entreprise = entrepriseRepository.save(entreprise);
    }

    @Test
    @DisplayName("POST /entreprises - crÃ©er une entreprise")
    void shouldCreateEntreprise() throws Exception {
        entrepriseRepository.deleteAll();

        CreateEntrepriseRequest request = new CreateEntrepriseRequest();
        request.setUserId(user.getId());
        request.setRaisonSociale("Nouvelle SRL");
        request.setNumeroEntreprise("BE0987654321");
        request.setChiffreAffairesMensuel(new BigDecimal("50000.00"));
        request.setChargesMensuelles(new BigDecimal("30000.00"));
        request.setTresorerie(new BigDecimal("100000.00"));

        mockMvc.perform(post("/api/v1/entreprises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.raisonSociale").value("Nouvelle SRL"));
    }

    @Test
    @DisplayName("POST /entreprises - 400 si userId a dÃ©jÃ  une entreprise")
    void shouldCreateSecondEntrepriseForSameUser() throws Exception {
        // entreprise avec userId=1 dÃ©jÃ  crÃ©Ã©e dans setUp()
        CreateEntrepriseRequest request = new CreateEntrepriseRequest();
        request.setUserId(user.getId());
        request.setRaisonSociale("DeuxiÃ¨me SRL");
        request.setNumeroEntreprise("BE0987654321");
        request.setChiffreAffairesMensuel(new BigDecimal("50000.00"));
        request.setChargesMensuelles(new BigDecimal("30000.00"));
        request.setTresorerie(new BigDecimal("100000.00"));

        mockMvc.perform(post("/api/v1/entreprises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.raisonSociale").exists());
    }

    @Test
    @DisplayName("POST /entreprises - 400 si meme numero BCE deja rattache")
    void shouldReturn400WhenEnterpriseNumberAlreadyAttachedToUser() throws Exception {
        CreateEntrepriseRequest request = new CreateEntrepriseRequest();
        request.setUserId(user.getId());
        request.setRaisonSociale("Duplicat SRL");
        request.setNumeroEntreprise("BE0123456789");
        request.setChiffreAffairesMensuel(new BigDecimal("50000.00"));
        request.setChargesMensuelles(new BigDecimal("30000.00"));
        request.setTresorerie(new BigDecimal("100000.00"));

        mockMvc.perform(post("/api/v1/entreprises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /entreprises/{id} - rÃ©cupÃ©rer par ID")
    void shouldGetEntrepriseById() throws Exception {
        mockMvc.perform(get("/api/v1/entreprises/" + entreprise.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(entreprise.getId()))
                .andExpect(jsonPath("$.raisonSociale").value("Profacile SRL"));
    }

    @Test
    @DisplayName("GET /entreprises/{id} - 404 si inexistante")
    void shouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/entreprises/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /entreprises/user/{userId} - rÃ©cupÃ©rer par userId")
    void shouldGetEntrepriseByUserId() throws Exception {
        mockMvc.perform(get("/api/v1/entreprises/user/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(user.getId()));
    }

    @Test
    @DisplayName("GET /entreprises/user/{userId}/exists - vÃ©rifier existence")
    void shouldCheckEntrepriseExists() throws Exception {
        mockMvc.perform(get("/api/v1/entreprises/user/" + user.getId() + "/exists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        mockMvc.perform(get("/api/v1/entreprises/user/999/exists"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /entreprises/{id} - mise Ã  jour")
    void shouldUpdateEntreprise() throws Exception {
        UpdateEntrepriseRequest request = new UpdateEntrepriseRequest();
        request.setRaisonSociale("Profacile SRL Updated");

        mockMvc.perform(put("/api/v1/entreprises/" + entreprise.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.raisonSociale").value("Profacile SRL Updated"));
    }

    @Test
    @DisplayName("DELETE /entreprises/{id} - suppression")
    void shouldDeleteEntreprise() throws Exception {
        mockMvc.perform(delete("/api/v1/entreprises/" + entreprise.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/entreprises/" + entreprise.getId()))
                .andExpect(status().isNotFound());
    }
}


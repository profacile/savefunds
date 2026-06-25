package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.entity.SituationFinanciere;
import be.profacile.savefunds.domain.enums.StatutEntreprise;
import be.profacile.savefunds.domain.repository.EntrepriseRepository;
import be.profacile.savefunds.domain.repository.SituationFinanciereRepository;
import be.profacile.savefunds.api.dto.request.CreateSituationFinanciereRequest;
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
@DisplayName("SituationFinanciereController Tests")
class SituationFinanciereControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SituationFinanciereRepository situationRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    private Entreprise entreprise;
    private SituationFinanciere situation;

    @BeforeEach
    void setUp() {
        situationRepository.deleteAll();
        entrepriseRepository.deleteAll();

        entreprise = new Entreprise();
        entreprise.setUserId(1L);
        entreprise.setRaisonSociale("Test SRL");
        entreprise.setNumeroEntreprise("BE0123456789");
        entreprise.setStatut(StatutEntreprise.ACTIVE);
        entreprise = entrepriseRepository.save(entreprise);

        situation = new SituationFinanciere();
        situation.setEntrepriseId(entreprise.getId());
        situation.setChiffreAffairesMensuel(new BigDecimal("50000.00"));
        situation.setChargesMensuelles(new BigDecimal("30000.00"));
        situation.setTresorerie(new BigDecimal("100000.00"));
        situation.setSoldeCompteCourant(new BigDecimal("5000.00"));
        situation = situationRepository.save(situation);
    }

    @Test
    @DisplayName("GET /situations/{id} - récupérer par ID")
    void shouldGetById() throws Exception {
        mockMvc.perform(get("/api/v1/situations/" + situation.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(situation.getId()))
                .andExpect(jsonPath("$.entrepriseId").value(entreprise.getId()));
    }

    @Test
    @DisplayName("GET /situations/{id} - 404 si inexistante")
    void shouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/situations/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /situations/entreprise/{id} - historique")
    void shouldGetByEntrepriseId() throws Exception {
        mockMvc.perform(get("/api/v1/situations/entreprise/" + entreprise.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /situations/entreprise/{id}/last - dernière situation")
    void shouldGetLast() throws Exception {
        mockMvc.perform(get("/api/v1/situations/entreprise/" + entreprise.getId() + "/last"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entrepriseId").value(entreprise.getId()));
    }

    @Test
    @DisplayName("POST /situations - créer une situation")
    void shouldCreateSituation() throws Exception {
        CreateSituationFinanciereRequest request = new CreateSituationFinanciereRequest();
        request.setEntrepriseId(entreprise.getId());
        request.setChiffreAffairesMensuel(new BigDecimal("60000.00"));
        request.setChargesMensuelles(new BigDecimal("35000.00"));
        request.setTresorerie(new BigDecimal("120000.00"));
        request.setSoldeCompteCourant(new BigDecimal("8000.00"));

        mockMvc.perform(post("/api/v1/situations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.entrepriseId").value(entreprise.getId()));
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
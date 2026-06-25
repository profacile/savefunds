package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.request.CreateAnalyseRequest;
import be.profacile.savefunds.domain.entity.AnalysePrelevement;
import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.entity.User;
import be.profacile.savefunds.domain.enums.Role;
import be.profacile.savefunds.domain.enums.StatutAnalyse;
import be.profacile.savefunds.domain.repository.AnalyseRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;    // ✅
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;   // ✅
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath; // ✅
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("AnalyseController Tests")
@WithMockUser(username = "dirigeant@example.com")
class AnalyseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AnalyseRepository analyseRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @Autowired
    private UserRepository userRepository;

    private Entreprise entreprise;
    private User user;

    @BeforeEach
    void setUp() {
        analyseRepository.deleteAll();
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
        entreprise.setRaisonSociale("Test Entreprise");
        entreprise.setNumeroEntreprise("BE0123456789");
        entreprise.setChiffreAffairesMensuel(BigDecimal.valueOf(10000));
        entreprise.setChargesMensuelles(BigDecimal.valueOf(5000));
        entreprise.setTresorerie(BigDecimal.valueOf(20000));
        entreprise.setSoldeCompteCourant(BigDecimal.valueOf(1000));
        entreprise = entrepriseRepository.save(entreprise);
    }

    @Test
    @DisplayName("POST /analyses - créer une analyse")
    void shouldCreateAnalyse() throws Exception {

        // Le controller attend un CreateAnalyseRequest, pas une entité
        CreateAnalyseRequest request = new CreateAnalyseRequest();
        request.setEntrepriseId(entreprise.getId());
        request.setMontantSouhaite(BigDecimal.valueOf(1000));

        mockMvc.perform(post("/api/v1/analyses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.montantSouhaite").value(1000))   // ← champ réel
                .andExpect(jsonPath("$.statut").value("EN_ATTENTE"));
    }

    @Test
    @DisplayName("POST /analyses - 404 si entreprise inexistante")
    void shouldReturn404WhenEntrepriseNotFound() throws Exception {

        CreateAnalyseRequest request = new CreateAnalyseRequest();
        request.setEntrepriseId(999L);
        request.setMontantSouhaite(BigDecimal.valueOf(1000));

        mockMvc.perform(post("/api/v1/analyses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /analyses/entreprise/{id} - liste des analyses")
    void shouldGetAnalysesByEntreprise() throws Exception {

        // Préparer une analyse en base avec les bons champs
        analyseRepository.save(AnalysePrelevement.builder()
                .entreprise(entreprise)
                .montantSouhaite(BigDecimal.valueOf(500))
                .statut(StatutAnalyse.EN_ATTENTE)
                .build());

        mockMvc.perform(get("/api/v1/analyses/entreprise/" + entreprise.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("GET /analyses/{id} - récupérer une analyse")
    void shouldGetAnalyseById() throws Exception {

        AnalysePrelevement saved = analyseRepository.save(AnalysePrelevement.builder()
                .entreprise(entreprise)
                .montantSouhaite(BigDecimal.valueOf(2000))
                .statut(StatutAnalyse.EN_ATTENTE)
                .build());

        mockMvc.perform(get("/api/v1/analyses/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.montantSouhaite").value(2000));
    }

    @Test
    @DisplayName("GET /analyses/{id} - 404 si analyse inexistante")
    void shouldReturn404WhenAnalyseNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/analyses/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /analyses/{id}/analyser - lancer l'analyse")
    void shouldLancerAnalyse() throws Exception {

        AnalysePrelevement saved = analyseRepository.save(AnalysePrelevement.builder()
                .entreprise(entreprise)
                .montantSouhaite(BigDecimal.valueOf(1000))
                .statut(StatutAnalyse.EN_ATTENTE)
                .build());

        mockMvc.perform(post("/api/v1/analyses/" + saved.getId() + "/analyser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decisionGlobale").exists())   // ← champ réel de ResultatAnalyseResponse
                .andExpect(jsonPath("$.decisionGlobale").isNotEmpty());
    }

    @Test
    @DisplayName("POST /analyses/{id}/analyser - 400 si analyse déjà TERMINEE")
    void shouldReturn400WhenAnalyseDejaTerminee() throws Exception {

        AnalysePrelevement saved = analyseRepository.save(AnalysePrelevement.builder()
                .entreprise(entreprise)
                .montantSouhaite(BigDecimal.valueOf(1000))
                .statut(StatutAnalyse.TERMINEE)
                .build());

        mockMvc.perform(post("/api/v1/analyses/" + saved.getId() + "/analyser"))
                .andExpect(status().isBadRequest());
    }
}

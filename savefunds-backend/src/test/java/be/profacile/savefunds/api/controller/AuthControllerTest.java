package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.request.LoginRequest;
import be.profacile.savefunds.api.dto.request.RegisterRequest;
import be.profacile.savefunds.domain.repository.UserRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /auth/register - inscription rÃ©ussie")
    void shouldRegister() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@profacile.be");
        request.setPassword("password123");
        request.setLastName("Doe");
        request.setFirstName("John");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value("test@profacile.be"))
                .andExpect(jsonPath("$.user.lastName").value("Doe"));
    }

    @Test
    @DisplayName("POST /auth/register - 400 si email dÃ©jÃ  utilisÃ©")
    void shouldReturn400OnDuplicateEmail() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@profacile.be");
        request.setPassword("password123");
        request.setLastName("Doe");
        request.setFirstName("John");

        // Premier enregistrement
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // DeuxiÃ¨me avec le mÃªme email
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/register - 400 si Données invalides")
    void shouldReturn400OnInvalidData() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("email-invalide");   // â† format invalide
        request.setPassword("123");           // â† trop court
        request.setLastName("D");                  // â† trop court

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/login - connexion rÃ©ussie")
    void shouldLogin() throws Exception {
        // D'abord s'inscrire
        RegisterRequest register = new RegisterRequest();
        register.setEmail("test@profacile.be");
        register.setPassword("password123");
        register.setLastName("Doe");
        register.setFirstName("John");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        // Puis se connecter
        LoginRequest login = new LoginRequest();
        login.setEmail("test@profacile.be");
        login.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value("test@profacile.be"));
    }

    @Test
    @DisplayName("POST /auth/login - 400 si mot de passe incorrect")
    void shouldReturn400OnWrongPassword() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setEmail("test@profacile.be");
        register.setPassword("correctPassword");
        register.setLastName("Doe");
        register.setFirstName("John");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        LoginRequest login = new LoginRequest();
        login.setEmail("test@profacile.be");
        login.setPassword("wrongPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/login - 400 si email inexistant")
    void shouldReturn400OnUnknownEmail() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setEmail("inconnu@profacile.be");
        login.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isBadRequest());
    }
}

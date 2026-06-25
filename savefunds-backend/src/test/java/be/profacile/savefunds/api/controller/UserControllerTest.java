package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.request.RegisterRequest;
import be.profacile.savefunds.api.dto.request.UpdateUserRequest;
import be.profacile.savefunds.domain.entity.User;
import be.profacile.savefunds.domain.enums.Role;
import be.profacile.savefunds.domain.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("UserController Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user = new User();
        user.setEmail("christian@profacile.be");
        user.setPasswordHash(passwordEncoder.encode("password123"));
        user.setNom("SANDJONG MOTIO");
        user.setPrenom("Christian");
        user.setRole(Role.DIRIGEANT);
        user.setEmailVerified(false);
        user = userRepository.save(user);
    }

    @Test
    @DisplayName("GET /users - lister tous les utilisateurs")
    void shouldListUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].email").value("christian@profacile.be"));
    }

    @Test
    @DisplayName("GET /users/{id} - récupérer par ID")
    void shouldGetUserById() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value("christian@profacile.be"))
                .andExpect(jsonPath("$.nom").value("SANDJONG MOTIO"));
    }

    @Test
    @DisplayName("GET /users/{id} - 404 si inexistant")
    void shouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /users/email/{email} - récupérer par email")
    void shouldGetUserByEmail() throws Exception {
        mockMvc.perform(get("/api/v1/users/email/christian@profacile.be"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("christian@profacile.be"));
    }

    @Test
    @DisplayName("PUT /users/{id} - mise à jour du profil")
    void shouldUpdateUser() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setNom("NOUVEAU NOM");
        request.setPrenom("NouveauPrenom");

        mockMvc.perform(put("/api/v1/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("NOUVEAU NOM"))
                .andExpect(jsonPath("$.prenom").value("NouveauPrenom"))
                // email non modifié
                .andExpect(jsonPath("$.email").value("christian@profacile.be"));
    }

    @Test
    @DisplayName("PUT /users/{id} - 404 si inexistant")
    void shouldReturn404OnUpdateWhenNotFound() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setNom("Test");

        mockMvc.perform(put("/api/v1/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /users/{id} - suppression")
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/v1/users/" + user.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/users/" + user.getId()))
                .andExpect(status().isNotFound());
    }
}
package be.profacile.savefunds.api.controller;

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
import org.springframework.security.test.context.support.WithMockUser;
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
    private User admin;

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

        admin = new User();
        admin.setEmail("admin@profacile.be");
        admin.setPasswordHash(passwordEncoder.encode("password123"));
        admin.setNom("Admin");
        admin.setPrenom("SaveFunds");
        admin.setRole(Role.ADMIN);
        admin.setEmailVerified(true);
        admin = userRepository.save(admin);
    }

    @Test
    @DisplayName("GET /users/me - recuperer le profil connecte")
    @WithMockUser(username = "christian@profacile.be")
    void shouldGetCurrentUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value("christian@profacile.be"));
    }

    @Test
    @DisplayName("GET /users - lister tous les utilisateurs comme admin")
    @WithMockUser(username = "admin@profacile.be")
    void shouldListUsersAsAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /users - refuser un non admin")
    @WithMockUser(username = "christian@profacile.be")
    void shouldRejectListUsersForNonAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /users/{id} - recuperer son propre profil")
    @WithMockUser(username = "christian@profacile.be")
    void shouldGetOwnUserById() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value("christian@profacile.be"))
                .andExpect(jsonPath("$.nom").value("SANDJONG MOTIO"));
    }

    @Test
    @DisplayName("GET /users/{id} - refuser un autre profil")
    @WithMockUser(username = "christian@profacile.be")
    void shouldRejectOtherUserById() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + admin.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /users/{id} - 404 si inexistant pour admin")
    @WithMockUser(username = "admin@profacile.be")
    void shouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /users/email/{email} - recuperer son profil par email")
    @WithMockUser(username = "christian@profacile.be")
    void shouldGetOwnUserByEmail() throws Exception {
        mockMvc.perform(get("/api/v1/users/email/christian@profacile.be"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("christian@profacile.be"));
    }

    @Test
    @DisplayName("PUT /users/{id} - mise a jour de son profil")
    @WithMockUser(username = "christian@profacile.be")
    void shouldUpdateOwnUser() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setNom("NOUVEAU NOM");
        request.setPrenom("NouveauPrenom");

        mockMvc.perform(put("/api/v1/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("NOUVEAU NOM"))
                .andExpect(jsonPath("$.prenom").value("NouveauPrenom"))
                .andExpect(jsonPath("$.email").value("christian@profacile.be"));
    }

    @Test
    @DisplayName("PUT /users/{id} - refuser changement de role par non admin")
    @WithMockUser(username = "christian@profacile.be")
    void shouldRejectRoleUpdateForNonAdmin() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setRole(Role.ADMIN);

        mockMvc.perform(put("/api/v1/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /users/{id} - 404 si inexistant pour admin")
    @WithMockUser(username = "admin@profacile.be")
    void shouldReturn404OnUpdateWhenNotFound() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setNom("Test");

        mockMvc.perform(put("/api/v1/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /users/{id} - suppression par admin")
    @WithMockUser(username = "admin@profacile.be")
    void shouldDeleteUserAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/users/" + user.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /users/{id} - refuser un non admin")
    @WithMockUser(username = "christian@profacile.be")
    void shouldRejectDeleteForNonAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/users/" + admin.getId()))
                .andExpect(status().isForbidden());
    }
}

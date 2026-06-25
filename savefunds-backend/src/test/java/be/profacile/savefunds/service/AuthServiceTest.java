package be.profacile.savefunds.service;

import be.profacile.savefunds.api.dto.request.LoginRequest;
import be.profacile.savefunds.api.dto.request.RegisterRequest;
import be.profacile.savefunds.api.dto.response.AuthResponse;
import be.profacile.savefunds.domain.entity.User;
import be.profacile.savefunds.domain.repository.UserRepository;
import be.profacile.savefunds.domain.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Devrait enregistrer un nouvel utilisateur")
    void shouldRegisterNewUser() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setNom("Doe");
        request.setPrenom("John");

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");   // ← via getUser()
        assertThat(response.getUser().getNom()).isEqualTo("Doe");
        assertThat(response.getUser().getPrenom()).isEqualTo("John");

        // Vérifier que le mot de passe est bien hashé en base
        User user = userRepository.findByEmail("test@example.com").orElseThrow();
        assertThat(user.getPasswordHash()).isNotEqualTo("password123");
    }

    @Test
    @DisplayName("Devrait rejeter un email déjà existant")
    void shouldRejectDuplicateEmail() {
        RegisterRequest request1 = new RegisterRequest();
        request1.setEmail("test@example.com");
        request1.setPassword("password123");
        request1.setNom("Doe");
        request1.setPrenom("John");
        authService.register(request1);

        RegisterRequest request2 = new RegisterRequest();
        request2.setEmail("test@example.com");
        request2.setPassword("password456");
        request2.setNom("Smith");
        request2.setPrenom("Jane");

        assertThatThrownBy(() -> authService.register(request2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email déjà utilisé");
    }

    @Test
    @DisplayName("Devrait connecter un utilisateur existant")
    void shouldLoginExistingUser() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setNom("Doe");
        registerRequest.setPrenom("John");
        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");   // ← via getUser()
    }

    @Test
    @DisplayName("Devrait rejeter un mauvais mot de passe")
    void shouldRejectWrongPassword() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("correctPassword");
        registerRequest.setNom("Doe");
        registerRequest.setPrenom("John");
        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongPassword");

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email ou mot de passe incorrect");
    }

    @Test
    @DisplayName("Devrait rejeter un email inexistant")
    void shouldRejectNonExistentEmail() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("notfound@example.com");
        loginRequest.setPassword("password123");

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
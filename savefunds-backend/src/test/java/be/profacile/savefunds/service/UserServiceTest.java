package be.profacile.savefunds.service;

import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.domain.entity.User;
import be.profacile.savefunds.domain.enums.Role;
import be.profacile.savefunds.domain.repository.UserRepository;
import be.profacile.savefunds.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("UserService Tests")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Devrait créer un utilisateur")
    void shouldCreateUser() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        user.setNom("Dupont");
        user.setPrenom("Jean");
        user.setEmailVerified(false);
        user.setRole(Role.DIRIGEANT);

        // When
        User saved = userService.create(user);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("test@example.com");
        assertThat(saved.getRole()).isEqualTo(Role.DIRIGEANT);
    }

    @Test
    @DisplayName("Devrait empêcher la création avec email dupliqué")
    void shouldPreventDuplicateEmail() {
        // Given
        User user1 = new User();
        user1.setEmail("test@example.com");
        user1.setPasswordHash("hash1");
        user1.setNom("Dupont");
        user1.setPrenom("Jean");
        user1.setRole(Role.DIRIGEANT);

        userService.create(user1);

        User user2 = new User();
        user2.setEmail("test@example.com");  // Même email!
        user2.setPasswordHash("hash2");
        user2.setNom("Martin");
        user2.setPrenom("Marie");
        user2.setRole(Role.DIRIGEANT);

        // When / Then
        assertThatThrownBy(() -> userService.create(user2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email déjà utilisé");
    }

    @Test
    @DisplayName("Devrait trouver un utilisateur par email")
    void shouldFindByEmail() {
        // Given
        User user = createUser("test@example.com", "Dupont", "Jean");
        userRepository.save(user);

        // When
        Optional<User> found = userService.findByEmail("test@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Devrait trouver un utilisateur par ID")
    void shouldFindById() {
        // Given
        User user = createUser("test@example.com", "Dupont", "Jean");
        User saved = userRepository.save(user);

        // When
        Optional<User> found = userService.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("Devrait vérifier l'existence d'un email")
    void shouldCheckEmailExists() {
        // Given
        User user = createUser("test@example.com", "Dupont", "Jean");
        userRepository.save(user);

        // When
        boolean exists = userService.existsByEmail("test@example.com");
        boolean notExists = userService.existsByEmail("autre@example.com");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Devrait récupérer tous les utilisateurs")
    void shouldFindAll() {
        // Given
        userRepository.save(createUser("user1@example.com", "Dupont", "Jean"));
        userRepository.save(createUser("user2@example.com", "Martin", "Marie"));
        userRepository.save(createUser("user3@example.com", "Bernard", "Paul"));

        // When
        List<User> all = userService.findAll();

        // Then
        assertThat(all).hasSize(3);
    }

    @Test
    @DisplayName("Devrait mettre à jour un utilisateur")
    void shouldUpdateUser() {
        // Given
        User user = createUser("test@example.com", "Dupont", "Jean");
        User saved = userRepository.save(user);

        User updates = new User();
        updates.setNom("Martin");
        updates.setPrenom("Marie");
        updates.setEmailVerified(true);

        // When
        User updated = userService.update(saved.getId(), updates);

        // Then
        assertThat(updated.getNom()).isEqualTo("Martin");
        assertThat(updated.getPrenom()).isEqualTo("Marie");
        assertThat(updated.getEmailVerified()).isTrue();
        assertThat(updated.getEmail()).isEqualTo("test@example.com");  // Pas changé
    }

    @Test
    @DisplayName("Devrait supprimer un utilisateur")
    void shouldDeleteUser() {
        // Given
        User user = createUser("test@example.com", "Dupont", "Jean");
        User saved = userRepository.save(user);
        Long userId = saved.getId();

        // When
        userService.delete(userId);

        // Then
        Optional<User> found = userRepository.findById(userId);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Devrait lancer exception si suppression utilisateur inexistant")
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        // When / Then
        assertThatThrownBy(() -> userService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("id");
    }

    // Helper method
    private User createUser(String email, String nom, String prenom) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("hashedPassword");
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmailVerified(false);
        user.setRole(Role.DIRIGEANT);
        return user;
    }
}
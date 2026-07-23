package be.profacile.savefunds.repository;

import be.profacile.savefunds.domain.entity.User;
import be.profacile.savefunds.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests du repository UserRepository
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Nettoyer la base avant chaque test
        userRepository.deleteAll();
    }

    // ==========================================
    // TEST 1 : save() - Sauvegarde utilisateur
    // ==========================================

    @Test
    @DisplayName("Devrait sauvegarder un utilisateur")
    void shouldSaveUser() {
        // Given
        User user = new User();
        // TODO : DÃ©finir les propriÃ©tÃ©s

        // When
        // TODO : Sauvegarder l'utilisateur

        // Then
        // TODO : VÃ©rifier que l'ID n'est pas null

        // TODO : VÃ©rifier que l'email est correct

    }

    // ==========================================
    // TEST 2 : findByEmail() - Trouve par email
    // ==========================================

    @Test
    @DisplayName("Devrait trouver un utilisateur par email")
    void shouldFindByEmail() {
        // Given
        User user = new User();
        // TODO : CrÃ©er un utilisateur avec email = "john@example.com"

        // TODO : Sauvegarder


        // When
        // TODO : Rechercher par email


        // Then
        // TODO : VÃ©rifier que l'Optional est prÃ©sent

        // TODO : VÃ©rifier que le nom est correct

    }

    @Test
    @DisplayName("Devrait retourner vide si email inexistant")
    void shouldReturnEmptyWhenEmailNotFound() {
        // When
        // TODO : Rechercher un email qui n'existe pas

        // Then
        // TODO : VÃ©rifier que l'Optional est vide

    }

    // ==========================================
    // TEST 3 : existsByEmail() - VÃ©rifie existence
    // ==========================================

    @Test
    @DisplayName("Devrait retourner true si email existe")
    void shouldReturnTrueWhenEmailExists() {
        // Given
        User user = new User();
        // TODO : CrÃ©er un utilisateur avec email = "exists@example.com"

        // TODO : Sauvegarder


        // When
        // TODO : VÃ©rifier l'existence


        // Then
        // TODO : VÃ©rifier que le rÃ©sultat est true

    }

    @Test
    @DisplayName("Devrait retourner false si email n'existe pas")
    void shouldReturnFalseWhenEmailDoesNotExist() {
        // When
        // TODO : VÃ©rifier un email qui n'existe pas


        // Then
        // TODO : VÃ©rifier que le rÃ©sultat est false

    }

    // ==========================================
    // TEST 4 : findById() - Trouve par ID
    // ==========================================

    @Test
    @DisplayName("Devrait trouver un utilisateur par ID")
    void shouldFindById() {
        // Given
        User user = new User();
        // TODO : CrÃ©er un utilisateur

        // TODO : Sauvegarder et rÃ©cupÃ©rer l'ID

        // When
        // TODO : Rechercher par ID


        // Then
        // TODO : VÃ©rifier que l'Optional est prÃ©sent

        // TODO : VÃ©rifier que l'ID correspond

        // TODO : VÃ©rifier l'email

    }
}

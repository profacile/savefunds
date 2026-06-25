package be.profacile.savefunds.api.mapper;

import be.profacile.savefunds.api.dto.request.UpdateUserRequest;
import be.profacile.savefunds.api.dto.response.UserResponse;
import be.profacile.savefunds.domain.entity.User;
import be.profacile.savefunds.domain.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    @DisplayName("Devrait mapper User → UserResponse")
    void shouldMapEntityToResponse() {
        User user = new User();
        user.setId(1L);
        user.setEmail("christian@profacile.be");
        user.setNom("SANDJONG MOTIO");
        user.setPrenom("Christian");
        user.setRole(Role.DIRIGEANT);
        user.setEmailVerified(false);
        user.setPasswordHash("$2a$10$hashed");

        UserResponse response = mapper.toResponse(user);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("christian@profacile.be");
        assertThat(response.getNom()).isEqualTo("SANDJONG MOTIO");
        assertThat(response.getPrenom()).isEqualTo("Christian");
        assertThat(response.getRole()).isEqualTo(Role.DIRIGEANT);
        assertThat(response.getEmailVerified()).isFalse();
        // passwordHash ne doit PAS être exposé — UserResponse n'a pas ce champ
    }

    @Test
    @DisplayName("Devrait retourner null si entité null")
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    @DisplayName("updateFromRequest ne doit pas modifier email, passwordHash ni id")
    void shouldNotOverwriteProtectedFieldsOnUpdate() {
        User target = new User();
        target.setId(1L);
        target.setEmail("christian@profacile.be");
        target.setPasswordHash("$2a$10$hashed");
        target.setNom("Ancien Nom");
        target.setRole(Role.DIRIGEANT);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setNom("Nouveau Nom");
        request.setPrenom("Christian");
        // email et passwordHash ne sont pas dans UpdateUserRequest → protégés

        mapper.updateFromRequest(request, target);

        assertThat(target.getId()).isEqualTo(1L);                          // protégé
        assertThat(target.getEmail()).isEqualTo("christian@profacile.be"); // protégé
        assertThat(target.getPasswordHash()).isEqualTo("$2a$10$hashed");   // protégé
        assertThat(target.getNom()).isEqualTo("Nouveau Nom");              // mis à jour
        assertThat(target.getPrenom()).isEqualTo("Christian");             // mis à jour
    }

    @Test
    @DisplayName("updateFromRequest ne doit pas écraser avec null")
    void shouldNotOverwriteWithNullValues() {
        User target = new User();
        target.setNom("Nom Existant");
        target.setPrenom("Prénom Existant");
        target.setRole(Role.DIRIGEANT);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setNom("Nouveau Nom");
        // prenom et role sont null dans la request

        mapper.updateFromRequest(request, target);

        assertThat(target.getNom()).isEqualTo("Nouveau Nom");
        assertThat(target.getPrenom()).isEqualTo("Prénom Existant"); // inchangé
        assertThat(target.getRole()).isEqualTo(Role.DIRIGEANT);      // inchangé
    }
}
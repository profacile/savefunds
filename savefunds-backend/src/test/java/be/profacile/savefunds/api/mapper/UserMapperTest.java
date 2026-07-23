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
    @DisplayName("Devrait mapper User â†’ UserResponse")
    void shouldMapEntityToResponse() {
        User user = new User();
        user.setId(1L);
        user.setEmail("christian@profacile.be");
        user.setLastName("SANDJONG MOTIO");
        user.setFirstName("Christian");
        user.setRole(Role.DIRIGEANT);
        user.setEmailVerified(false);
        user.setPasswordHash("$2a$10$hashed");

        UserResponse response = mapper.toResponse(user);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("christian@profacile.be");
        assertThat(response.getLastName()).isEqualTo("SANDJONG MOTIO");
        assertThat(response.getFirstName()).isEqualTo("Christian");
        assertThat(response.getRole()).isEqualTo(Role.DIRIGEANT);
        assertThat(response.getEmailVerified()).isFalse();
        // passwordHash ne doit PAS Ãªtre exposÃ© â€” UserResponse n'a pas ce champ
    }

    @Test
    @DisplayName("Devrait retourner null si entitÃ© null")
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
        target.setLastName("Ancien Nom");
        target.setRole(Role.DIRIGEANT);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setLastName("Nouveau Nom");
        request.setFirstName("Christian");
        // email et passwordHash ne sont pas dans UpdateUserRequest â†’ protÃ©gÃ©s

        mapper.updateFromRequest(request, target);

        assertThat(target.getId()).isEqualTo(1L);                          // protÃ©gÃ©
        assertThat(target.getEmail()).isEqualTo("christian@profacile.be"); // protÃ©gÃ©
        assertThat(target.getPasswordHash()).isEqualTo("$2a$10$hashed");   // protÃ©gÃ©
        assertThat(target.getLastName()).isEqualTo("Nouveau Nom");              // mis Ã  jour
        assertThat(target.getFirstName()).isEqualTo("Christian");             // mis Ã  jour
    }

    @Test
    @DisplayName("updateFromRequest ne doit pas Ã©craser avec null")
    void shouldNotOverwriteWithNullValues() {
        User target = new User();
        target.setLastName("Nom Existant");
        target.setFirstName("PrÃ©nom Existant");
        target.setRole(Role.DIRIGEANT);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setLastName("Nouveau Nom");
        // prenom et role sont null dans la request

        mapper.updateFromRequest(request, target);

        assertThat(target.getLastName()).isEqualTo("Nouveau Nom");
        assertThat(target.getFirstName()).isEqualTo("PrÃ©nom Existant"); // inchangÃ©
        assertThat(target.getRole()).isEqualTo(Role.DIRIGEANT);      // inchangÃ©
    }
}

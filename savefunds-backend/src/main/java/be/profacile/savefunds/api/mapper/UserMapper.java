package be.profacile.savefunds.api.mapper;

import be.profacile.savefunds.api.dto.request.UpdateUserRequest;
import be.profacile.savefunds.api.dto.response.UserResponse;
import be.profacile.savefunds.domain.entity.User;
import org.mapstruct.*;

/**
 * Mapper MapStruct pour User.
 *
 * Mappings :
 * - Entity → UserResponse
 * - UpdateUserRequest → Entity (mise à jour partielle)
 *
 * Note : La création d'un User est gérée entièrement dans AuthServiceImpl
 * (hachage du mot de passe, assignation du rôle) — pas de toEntity(RegisterRequest).
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    /**
     * Entity → Response
     * Mapping direct — tous les champs ont le même nom.
     */
    UserResponse toResponse(User user);

    /**
     * UpdateUserRequest → Entity (mise à jour partielle sur cible existante).
     *
     * Ignore :
     * - id (non modifiable)
     * - email (non modifiable après création — identifiant de connexion)
     * - passwordHash (géré via ChangePasswordRequest)
     * - createdAt (non modifiable)
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateFromRequest(UpdateUserRequest request, @MappingTarget User user);
}
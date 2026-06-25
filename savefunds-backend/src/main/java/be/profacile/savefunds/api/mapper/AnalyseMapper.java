package be.profacile.savefunds.api.mapper;

import be.profacile.savefunds.api.dto.response.AnalyseResponse;
import be.profacile.savefunds.domain.entity.AnalysePrelevement;
import org.mapstruct.*;

/**
 * Mapper MapStruct pour la conversion entre AnalysePrelevement (Entity) et DTOs.
 * 
 * Mappings :
 * - Entity → AnalyseResponse
 * - CreateAnalyseRequest → Entity (géré manuellement dans le service)
 * 
 * Configuration :
 * - componentModel = "spring" : Injection automatique dans le contexte Spring
 * - unmappedTargetPolicy = IGNORE : Ne pas générer de warnings pour les champs non mappés
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AnalyseMapper {

    /**
     * Convertit une entité AnalysePrelevement en DTO AnalyseResponse.
     * 
     * Mapping automatique des champs :
     * - id → id
     * - montantSouhaite → montantSouhaite
     * - statut → statut
     * - createdAt → createdAt
     * - updatedAt → updatedAt
     * 
     * Mapping personnalisé :
     * - entreprise.id → entrepriseId
     */
    @Mapping(source = "entreprise.id", target = "entrepriseId")
    AnalyseResponse toResponse(AnalysePrelevement analyse);

    /**
     * Mise à jour partielle d'une entité AnalysePrelevement à partir d'une autre.
     * Utilisé pour les updates field-by-field.
     * 
     * Ignore les valeurs null dans la source (ne remplace que les champs non-null).
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromEntity(AnalysePrelevement source, @MappingTarget AnalysePrelevement target);
}
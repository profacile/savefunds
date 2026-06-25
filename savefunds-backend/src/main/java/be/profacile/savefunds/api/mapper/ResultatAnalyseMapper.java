package be.profacile.savefunds.api.mapper;

import be.profacile.savefunds.api.dto.response.ResultatAnalyseResponse;
import be.profacile.savefunds.domain.entity.ResultatAnalyse;
import org.mapstruct.*;

/**
 * Mapper MapStruct pour la conversion entre ResultatAnalyse (Entity) et DTOs.
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
public interface ResultatAnalyseMapper {

    /**
     * Convertit une entité ResultatAnalyse en DTO ResultatAnalyseResponse.
     *
     * analyse.id → analyseId dans le DTO
     */
    @Mapping(source = "analyse.id", target = "analyseId")
    @Mapping(source = "analyse.montantSouhaite", target = "montantSouhaite")
    @Mapping(source = "montantMaxPrelevable", target = "montantMaxPrelevable")
    ResultatAnalyseResponse toResponse(ResultatAnalyse resultat);

    /**
     * Mise à jour partielle field-by-field.
     * On ignore id et la relation analyse (jamais modifiée lors d'un update).
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "analyse", ignore = true)
    void updateFromEntity(ResultatAnalyse source, @MappingTarget ResultatAnalyse target);
}
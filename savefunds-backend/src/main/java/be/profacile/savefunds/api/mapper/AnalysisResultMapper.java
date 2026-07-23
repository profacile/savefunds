package be.profacile.savefunds.api.mapper;

import be.profacile.savefunds.api.dto.response.AnalysisResultResponse;
import be.profacile.savefunds.domain.entity.AnalysisResult;
import org.mapstruct.*;

/**
 * Mapper MapStruct pour la conversion entre AnalysisResult (Entity) et DTOs.
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
public interface AnalysisResultMapper {

    /**
     * Convertit une entité AnalysisResult en DTO AnalysisResultResponse.
     *
     * analysis.id → analysisId dans le DTO
     */
    @Mapping(source = "analysis.id", target = "analysisId")
    @Mapping(source = "analysis.requestedAmount", target = "requestedAmount")
    @Mapping(source = "maxWithdrawableAmount", target = "maxWithdrawableAmount")
    AnalysisResultResponse toResponse(AnalysisResult result);

    /**
     * Mise à jour partielle field-by-field.
     * On ignore id et la relation analysis (jamais modifiée lors d'un update).
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "analysis", ignore = true)
    void updateFromEntity(AnalysisResult source, @MappingTarget AnalysisResult target);
}
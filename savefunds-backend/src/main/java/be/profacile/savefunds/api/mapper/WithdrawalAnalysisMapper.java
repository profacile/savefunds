package be.profacile.savefunds.api.mapper;

import be.profacile.savefunds.api.dto.response.WithdrawalAnalysisResponse;
import be.profacile.savefunds.domain.entity.WithdrawalAnalysis;
import org.mapstruct.*;

/**
 * Mapper MapStruct pour la conversion entre WithdrawalAnalysis (Entity) et DTOs.
 * 
 * Mappings :
 * - Entity → WithdrawalAnalysisResponse
 * - CreateWithdrawalAnalysisRequest → Entity (géré manuellement dans le service)
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
public interface WithdrawalAnalysisMapper {

    /**
     * Convertit une entité WithdrawalAnalysis en DTO WithdrawalAnalysisResponse.
     * 
     * Mapping automatique des champs :
     * - id → id
     * - requestedAmount → requestedAmount
     * - status → status
     * - createdAt → createdAt
     * - updatedAt → updatedAt
     * 
     * Mapping personnalisé :
     * - company.id → companyId
     */
    @Mapping(source = "company.id", target = "companyId")
    WithdrawalAnalysisResponse toResponse(WithdrawalAnalysis analysis);

    /**
     * Mise à jour partielle d'une entité WithdrawalAnalysis à partir d'une autre.
     * Utilisé pour les updates field-by-field.
     * 
     * Ignore les valeurs null dans la source (ne remplace que les champs non-null).
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromEntity(WithdrawalAnalysis source, @MappingTarget WithdrawalAnalysis target);
}
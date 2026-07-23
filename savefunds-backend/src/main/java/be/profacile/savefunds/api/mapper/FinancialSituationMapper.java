package be.profacile.savefunds.api.mapper;

import be.profacile.savefunds.api.dto.request.CreateFinancialSituationRequest;
import be.profacile.savefunds.api.dto.response.FinancialSituationResponse;
import be.profacile.savefunds.domain.entity.FinancialSituation;
import org.mapstruct.*;

/**
 * Mapper MapStruct pour FinancialSituation.
 *
 * Mappings :
 * - Entity → FinancialSituationResponse (mapping direct, tous les champs correspondent)
 * - CreateFinancialSituationRequest → Entity
 *
 * Note : Les indicateurs calculés (ratioCACharges, cashCoverageMonths, directorCurrentAccountDebtorDays)
 * sont ignorés lors du mapping request → entity car ils sont calculés par FinancialIndicatorService,
 * pas fournis par le client.
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface FinancialSituationMapper {

    /**
     * Entity → Response
     * Mapping direct — tous les champs ont le même lastName.
     */
    FinancialSituationResponse toResponse(FinancialSituation situation);

    /**
     * CreateRequest → Entity
     *
     * Ignore :
     * - id (auto-généré)
     * - capturedAt (défini dans le service via LocalDateTime.now())
     * - ratioCACharges (calculé par FinancialIndicatorService)
     * - cashCoverageMonths (calculé par FinancialIndicatorService)
     * - directorCurrentAccountDebtorDays (calculé par FinancialIndicatorService)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "capturedAt", ignore = true)
    @Mapping(target = "ratioCACharges", ignore = true)
    @Mapping(target = "cashCoverageMonths", ignore = true)
    @Mapping(target = "directorCurrentAccountDebtorDays", ignore = true)
    FinancialSituation toEntity(CreateFinancialSituationRequest request);
}
package be.profacile.savefunds.api.mapper;

import be.profacile.savefunds.api.dto.request.CreateSituationFinanciereRequest;
import be.profacile.savefunds.api.dto.response.SituationFinanciereResponse;
import be.profacile.savefunds.domain.entity.SituationFinanciere;
import org.mapstruct.*;

/**
 * Mapper MapStruct pour SituationFinanciere.
 *
 * Mappings :
 * - Entity → SituationFinanciereResponse (mapping direct, tous les champs correspondent)
 * - CreateSituationFinanciereRequest → Entity
 *
 * Note : Les indicateurs calculés (ratioCACharges, tresorerieEnMois, dureeCompteCourantDebiteur)
 * sont ignorés lors du mapping request → entity car ils sont calculés par IndicateurService,
 * pas fournis par le client.
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface SituationFinanciereMapper {

    /**
     * Entity → Response
     * Mapping direct — tous les champs ont le même nom.
     */
    SituationFinanciereResponse toResponse(SituationFinanciere situation);

    /**
     * CreateRequest → Entity
     *
     * Ignore :
     * - id (auto-généré)
     * - capturedAt (défini dans le service via LocalDateTime.now())
     * - ratioCACharges (calculé par IndicateurService)
     * - tresorerieEnMois (calculé par IndicateurService)
     * - dureeCompteCourantDebiteur (calculé par IndicateurService)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "capturedAt", ignore = true)
    @Mapping(target = "ratioCACharges", ignore = true)
    @Mapping(target = "tresorerieEnMois", ignore = true)
    @Mapping(target = "dureeCompteCourantDebiteur", ignore = true)
    SituationFinanciere toEntity(CreateSituationFinanciereRequest request);
}
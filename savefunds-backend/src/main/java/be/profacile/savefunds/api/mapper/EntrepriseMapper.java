package be.profacile.savefunds.api.mapper;

import be.profacile.savefunds.api.dto.request.CreateEntrepriseRequest;
import be.profacile.savefunds.api.dto.request.UpdateEntrepriseRequest;
import be.profacile.savefunds.api.dto.response.EntrepriseResponse;
import be.profacile.savefunds.domain.entity.Entreprise;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface EntrepriseMapper {

    EntrepriseResponse toResponse(Entreprise entreprise);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nom", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "dateDebutDebiteurCC", ignore = true)
    Entreprise toEntity(CreateEntrepriseRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "numeroEntreprise", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "dateDebutDebiteurCC", ignore = true)
    Entreprise toEntity(UpdateEntrepriseRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "numeroEntreprise", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "dateDebutDebiteurCC", ignore = true)
    void updateFromEntity(Entreprise source, @MappingTarget Entreprise target);
}
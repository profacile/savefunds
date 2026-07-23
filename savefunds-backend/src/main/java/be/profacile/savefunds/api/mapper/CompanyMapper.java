package be.profacile.savefunds.api.mapper;

import be.profacile.savefunds.api.dto.request.CreateCompanyRequest;
import be.profacile.savefunds.api.dto.request.UpdateCompanyRequest;
import be.profacile.savefunds.api.dto.response.CompanyResponse;
import be.profacile.savefunds.domain.entity.Company;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CompanyMapper {

    CompanyResponse toResponse(Company company);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "directorCurrentAccountDebitStartDate", ignore = true)
    Company toEntity(CreateCompanyRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "enterpriseNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "directorCurrentAccountDebitStartDate", ignore = true)
    Company toEntity(UpdateCompanyRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "enterpriseNumber", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "directorCurrentAccountDebitStartDate", ignore = true)
    void updateFromEntity(Company source, @MappingTarget Company target);
}
package be.profacile.savefunds.domain.service.company;

import be.profacile.savefunds.api.dto.response.CompanyRegistryCompanyResponse;

import java.util.List;
import java.util.Optional;

public interface CompanyRegistryProvider {
    List<CompanyRegistryCompanyResponse> search(String query);

    Optional<CompanyRegistryCompanyResponse> findByEnterpriseNumber(String enterpriseNumber);
}

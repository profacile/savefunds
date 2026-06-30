package be.profacile.savefunds.domain.service.company;

import be.profacile.savefunds.api.dto.response.CompanyRegistryCompanyResponse;
import be.profacile.savefunds.domain.entity.CompanyRegistryEntry;
import be.profacile.savefunds.domain.repository.CompanyRegistryEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Primary
@Component
@RequiredArgsConstructor
@Slf4j
public class BceOpenDataCompanyRegistryProvider implements CompanyRegistryProvider {

    private final CompanyRegistryEntryRepository companyRegistryEntryRepository;
    private final BcePublicSearchClient bcePublicSearchClient;

    @Override
    public List<CompanyRegistryCompanyResponse> search(String query) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }
        try {
            List<CompanyRegistryCompanyResponse> localResults = companyRegistryEntryRepository.search(query.trim()).stream()
                    .limit(20)
                    .map(this::toResponse)
                    .toList();
            if (!localResults.isEmpty()) {
                return localResults;
            }
        } catch (Exception ex) {
            log.warn("Recherche BCE Open Data locale indisponible, fallback Public Search: {}", ex.getMessage());
        }
        return bcePublicSearchClient.search(query.trim());
    }

    @Override
    public Optional<CompanyRegistryCompanyResponse> findByEnterpriseNumber(String enterpriseNumber) {
        try {
            Optional<CompanyRegistryCompanyResponse> localResult = companyRegistryEntryRepository.findByEnterpriseNumber(normalizeEnterpriseNumber(enterpriseNumber))
                    .map(this::toResponse);
            if (localResult.isPresent()) {
                return localResult;
            }
        } catch (Exception ex) {
            log.warn("Recherche BCE Open Data locale par numero indisponible, fallback Public Search: {}", ex.getMessage());
        }
        return bcePublicSearchClient.findByEnterpriseNumber(enterpriseNumber);
    }

    private CompanyRegistryCompanyResponse toResponse(CompanyRegistryEntry entry) {
        return CompanyRegistryCompanyResponse.builder()
                .enterpriseNumber(entry.getEnterpriseNumber())
                .name(entry.getName())
                .legalForm(entry.getLegalForm())
                .status(entry.getStatus())
                .address(entry.getAddress())
                .postalCode(entry.getPostalCode())
                .city(entry.getCity())
                .naceCode(entry.getNaceCode())
                .activityLabel(entry.getActivityLabel())
                .source(entry.getSource())
                .active(entry.isActive())
                .build();
    }

    private String normalizeEnterpriseNumber(String value) {
        if (value == null) {
            return "";
        }
        String digits = value.replaceAll("\\D", "");
        return digits.length() == 10
                ? "BE " + digits.substring(0, 4) + "." + digits.substring(4, 7) + "." + digits.substring(7)
                : value.trim();
    }
}

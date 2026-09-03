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
        String normalizedQuery = query.trim();
        String digits = normalizedQuery.replaceAll("\\D", "");
        if (!digits.isEmpty() && digits.length() < 10 && normalizedQuery.replaceAll("[\\d\\s.\\-BEbe]", "").isBlank()) {
            return List.of();
        }
        if (digits.length() == 10) {
            return findByEnterpriseNumber(normalizedQuery)
                    .map(List::of)
                    .orElseGet(List::of);
        }

        List<CompanyRegistryCompanyResponse> liveResults = bcePublicSearchClient.search(normalizedQuery);
        if (!liveResults.isEmpty()) {
            return liveResults;
        }

        log.info("Aucun resultat BCE live pour '{}'. Consultation du cache Open Data local.", normalizedQuery);
        return searchLocal(normalizedQuery);
    }

    @Override
    public Optional<CompanyRegistryCompanyResponse> findByEnterpriseNumber(String enterpriseNumber) {
        Optional<CompanyRegistryCompanyResponse> liveResult = bcePublicSearchClient.findByEnterpriseNumber(enterpriseNumber);
        if (liveResult.isPresent()) {
            CompanyRegistryCompanyResponse liveCompany = liveResult.get();
            if (liveCompany.isActive()) {
                return liveResult;
            }

            Optional<CompanyRegistryCompanyResponse> localResult = findLocalByEnterpriseNumber(enterpriseNumber);
            if (localResult.map(CompanyRegistryCompanyResponse::isActive).orElse(false)) {
                log.info("Statut BCE live incomplet pour '{}'. Utilisation du cache Open Data local actif.", enterpriseNumber);
                return localResult;
            }

            return liveResult;
        }

        log.info("Aucun resultat BCE live pour le numero '{}'. Consultation du cache Open Data local.", enterpriseNumber);
        return findLocalByEnterpriseNumber(enterpriseNumber);
    }

    private List<CompanyRegistryCompanyResponse> searchLocal(String query) {
        try {
            return companyRegistryEntryRepository.search(query).stream()
                    .limit(20)
                    .map(this::toResponse)
                    .toList();
        } catch (Exception ex) {
            log.warn("Recherche BCE Open Data locale indisponible: {}", ex.getMessage());
            return List.of();
        }
    }

    private Optional<CompanyRegistryCompanyResponse> findLocalByEnterpriseNumber(String enterpriseNumber) {
        try {
            return companyRegistryEntryRepository.findByEnterpriseNumber(normalizeEnterpriseNumber(enterpriseNumber))
                    .map(this::toResponse);
        } catch (Exception ex) {
            log.warn("Recherche BCE Open Data locale par numero indisponible: {}", ex.getMessage());
            return Optional.empty();
        }
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

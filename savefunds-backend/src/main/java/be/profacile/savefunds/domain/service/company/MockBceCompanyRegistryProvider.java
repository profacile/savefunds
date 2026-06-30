package be.profacile.savefunds.domain.service.company;

import be.profacile.savefunds.api.dto.response.CompanyRegistryCompanyResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
public class MockBceCompanyRegistryProvider implements CompanyRegistryProvider {

    private final List<CompanyRegistryCompanyResponse> companies = List.of(
            CompanyRegistryCompanyResponse.builder()
                    .enterpriseNumber("BE 0123.456.789")
                    .name("Profacile SRL")
                    .legalForm("SRL")
                    .status("ACTIF")
                    .address("Avenue Louise 231")
                    .postalCode("1050")
                    .city("Ixelles")
                    .naceCode("62010")
                    .activityLabel("Programmation informatique")
                    .source("BCE Open Data mock")
                    .active(true)
                    .build(),
            CompanyRegistryCompanyResponse.builder()
                    .enterpriseNumber("BE 0734.221.908")
                    .name("Atelier Verhaegen SRL")
                    .legalForm("SRL")
                    .status("ACTIF")
                    .address("Rue du Commerce 18")
                    .postalCode("1000")
                    .city("Bruxelles")
                    .naceCode("43320")
                    .activityLabel("Travaux de menuiserie")
                    .source("BCE Open Data mock")
                    .active(true)
                    .build(),
            CompanyRegistryCompanyResponse.builder()
                    .enterpriseNumber("BE 0668.441.330")
                    .name("MecaNord SRL")
                    .legalForm("SRL")
                    .status("ACTIF")
                    .address("Chaussée de Mons 91")
                    .postalCode("1070")
                    .city("Anderlecht")
                    .naceCode("25620")
                    .activityLabel("Usinage")
                    .source("BCE Open Data mock")
                    .active(true)
                    .build(),
            CompanyRegistryCompanyResponse.builder()
                    .enterpriseNumber("BE 0444.111.222")
                    .name("Ancienne Consultance SPRL")
                    .legalForm("SPRL")
                    .status("RADIE")
                    .address("Rue Haute 10")
                    .postalCode("1000")
                    .city("Bruxelles")
                    .naceCode("70220")
                    .activityLabel("Conseil pour les affaires")
                    .source("BCE Open Data mock")
                    .active(false)
                    .build()
    );

    @Override
    public List<CompanyRegistryCompanyResponse> search(String query) {
        String normalized = normalize(query);
        return companies.stream()
                .filter(company -> normalize(company.getName()).contains(normalized)
                        || normalize(company.getEnterpriseNumber()).contains(normalized)
                        || normalize(company.getCity()).contains(normalized))
                .limit(10)
                .toList();
    }

    @Override
    public Optional<CompanyRegistryCompanyResponse> findByEnterpriseNumber(String enterpriseNumber) {
        String normalized = normalize(enterpriseNumber);
        return companies.stream()
                .filter(company -> normalize(company.getEnterpriseNumber()).equals(normalized))
                .findFirst();
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }
}

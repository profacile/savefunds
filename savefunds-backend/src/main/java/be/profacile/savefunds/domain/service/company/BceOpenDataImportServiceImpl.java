package be.profacile.savefunds.domain.service.company;

import be.profacile.savefunds.api.dto.response.CompanyRegistryImportResponse;
import be.profacile.savefunds.domain.entity.CompanyRegistryEntry;
import be.profacile.savefunds.domain.repository.CompanyRegistryEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BceOpenDataImportServiceImpl implements CompanyRegistryImportService {

    private final CompanyRegistryEntryRepository companyRegistryEntryRepository;

    @Override
    @Transactional
    public CompanyRegistryImportResponse importCsv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier BCE Open Data est obligatoire");
        }

        int imported = 0;
        int skipped = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String header = reader.readLine();
            if (header == null) {
                throw new IllegalArgumentException("Le fichier CSV est vide");
            }

            Map<String, Integer> columns = columns(header);
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> values = splitCsv(line);
                try {
                    CompanyRegistryEntry entry = toEntry(values, columns);
                    companyRegistryEntryRepository.findByEnterpriseNumber(entry.getEnterpriseNumber())
                            .ifPresent(existing -> entry.setId(existing.getId()));
                    companyRegistryEntryRepository.save(entry);
                    imported++;
                } catch (IllegalArgumentException ex) {
                    skipped++;
                }
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Import BCE impossible: " + ex.getMessage(), ex);
        }

        return CompanyRegistryImportResponse.builder()
                .importedRows(imported)
                .skippedRows(skipped)
                .source("BCE Open Data CSV")
                .build();
    }

    private CompanyRegistryEntry toEntry(List<String> values, Map<String, Integer> columns) {
        String enterpriseNumber = normalizeEnterpriseNumber(value(values, columns,
                "enterpriseNumber", "EnterpriseNumber", "EntityNumber", "entity_number", "numeroEntreprise", "Number"));
        if (enterpriseNumber.isBlank()) {
            throw new IllegalArgumentException("Numero d'entreprise manquant");
        }

        CompanyRegistryEntry entry = companyRegistryEntryRepository.findByEnterpriseNumber(enterpriseNumber)
                .orElseGet(CompanyRegistryEntry::new);

        String name = firstNonBlank(
                value(values, columns, "name", "Name", "denomination", "Denomination", "EntityName"),
                entry.getName(),
                "Entreprise BCE " + enterpriseNumber
        );

        String status = firstNonBlank(value(values, columns, "status", "Status", "statut", "JuridicalSituation"), entry.getStatus(), "ACTIF");
        String legalForm = firstNonBlank(value(values, columns, "legalForm", "LegalForm", "formeJuridique", "JuridicalForm", "JuridicalFormCAC"), entry.getLegalForm(), "");
        String address = firstNonBlank(buildAddress(values, columns), entry.getAddress(), "");
        String postalCode = firstNonBlank(value(values, columns, "postalCode", "Zipcode", "zipcode", "codePostal"), entry.getPostalCode(), "");
        String city = firstNonBlank(value(values, columns, "city", "Municipality", "MunicipalityFR", "MunicipalityNL", "commune"), entry.getCity(), "");
        String naceCode = firstNonBlank(value(values, columns, "naceCode", "NaceCode", "NACE", "NacebelCode"), entry.getNaceCode(), "");
        String activityLabel = firstNonBlank(value(values, columns, "activityLabel", "Activity", "ActivityLabel", "Classification", "activite"), entry.getActivityLabel(), "");

        entry.setEnterpriseNumber(enterpriseNumber);
        entry.setName(name);
        entry.setLegalForm(legalForm);
        entry.setStatus(status);
        entry.setAddress(address);
        entry.setPostalCode(postalCode);
        entry.setCity(city);
        entry.setNaceCode(naceCode);
        entry.setActivityLabel(activityLabel);
        entry.setActive(isActive(status));
        entry.setSource("BCE Open Data CSV");
        return entry;
    }

    private Map<String, Integer> columns(String header) {
        List<String> names = splitCsv(header);
        Map<String, Integer> columns = new HashMap<>();
        for (int index = 0; index < names.size(); index++) {
            String raw = names.get(index).trim();
            columns.put(raw, index);
            columns.put(normalizeColumn(raw), index);
        }
        return columns;
    }

    private String value(List<String> values, Map<String, Integer> columns, String... names) {
        for (String name : names) {
            Integer index = columns.get(name);
            if (index == null) {
                index = columns.get(normalizeColumn(name));
            }
            if (index != null && index < values.size()) {
                return values.get(index).trim();
            }
        }
        return "";
    }

    private List<String> splitCsv(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (character == '"') {
                if (quoted && index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    current.append('"');
                    index++;
                } else {
                    quoted = !quoted;
                }
            } else if (character == ';' && !quoted) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }
        values.add(current.toString());
        return values;
    }

    private boolean isActive(String status) {
        String normalized = status == null ? "" : status.toLowerCase();
        if (normalized.contains("inactif") || normalized.contains("inactive") || normalized.contains("radi") || normalized.contains("stop")) {
            return false;
        }
        return normalized.contains("actif")
                || normalized.contains("active")
                || normalized.equals("1")
                || normalized.equals("ac")
                || normalized.equals("act");
    }

    private String normalizeEnterpriseNumber(String value) {
        String digits = value == null ? "" : value.replaceAll("\\D", "");
        if (digits.length() != 10) {
            return value == null ? "" : value.trim();
        }
        return "BE " + digits.substring(0, 4) + "." + digits.substring(4, 7) + "." + digits.substring(7);
    }

    private String buildAddress(List<String> values, Map<String, Integer> columns) {
        String directAddress = value(values, columns, "address", "Address", "adresse");
        if (!directAddress.isBlank()) {
            return directAddress;
        }

        String street = firstNonBlank(
                value(values, columns, "Street", "StreetFR", "StreetNL", "street", "rue"),
                value(values, columns, "StreetName", "streetName"),
                ""
        );
        String houseNumber = value(values, columns, "HouseNumber", "houseNumber", "numero");
        String box = value(values, columns, "Box", "box", "boite");
        return String.join(" ", List.of(street, houseNumber, box)).trim();
    }

    private String firstNonBlank(String... values) {
        for (String current : values) {
            if (current != null && !current.isBlank()) {
                return current.trim();
            }
        }
        return "";
    }

    private String normalizeColumn(String value) {
        return value == null ? "" : value.toLowerCase().replaceAll("[^a-z0-9]", "");
    }
}

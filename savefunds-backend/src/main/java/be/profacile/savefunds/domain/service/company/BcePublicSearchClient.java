package be.profacile.savefunds.domain.service.company;

import be.profacile.savefunds.api.dto.response.CompanyRegistryCompanyResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class BcePublicSearchClient {

    private static final Pattern ROW_PATTERN = Pattern.compile("<tr[^>]*>(.*?)</tr>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern CELL_PATTERN = Pattern.compile("<td[^>]*>(.*?)</td>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern ENTERPRISE_NUMBER_PATTERN = Pattern.compile("(\\d{4}\\.\\d{3}\\.\\d{3})");
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public List<CompanyRegistryCompanyResponse> search(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://kbopub.economie.fgov.be/kbopub/zoeknaamfonetischform.html"
                    + "?searchWord=" + encodedQuery
                    + "&_oudeBenaming=on"
                    + "&pstcdeNPRP=&postgemeente1="
                    + "&ondNP=true&_ondNP=on"
                    + "&ondRP=true&_ondRP=on"
                    + "&rechtsvormFonetic=ALL"
                    + "&vest=true&_vest=on"
                    + "&filterEnkelActieve=true"
                    + "&actionNPRP=Rechercher";
            return parseSearchRows(fetch(url), query);
        } catch (Exception ex) {
            log.warn("Recherche BCE Public Search impossible pour '{}': {}", query, ex.getMessage());
            return List.of();
        }
    }

    public Optional<CompanyRegistryCompanyResponse> findByEnterpriseNumber(String enterpriseNumber) {
        String digits = enterpriseNumber == null ? "" : enterpriseNumber.replaceAll("\\D", "");
        if (digits.length() != 10) {
            return Optional.empty();
        }
        try {
            String url = "https://kbopub.economie.fgov.be/kbopub/zoeknummerform.html"
                    + "?nummer=" + digits
                    + "&actionLu=Rechercher";
            return parseSearchRows(fetch(url), digits).stream().findFirst();
        } catch (Exception ex) {
            log.warn("Recherche BCE Public Search par numero impossible pour '{}': {}", enterpriseNumber, ex.getMessage());
            return Optional.empty();
        }
    }

    private String fetch(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(8))
                .header("User-Agent", "SaveFunds-TFE/1.0")
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() >= 400) {
            throw new IllegalStateException("HTTP " + response.statusCode());
        }
        return response.body();
    }

    private List<CompanyRegistryCompanyResponse> parseSearchRows(String html, String query) {
        List<CompanyRegistryCompanyResponse> companies = new ArrayList<>();
        Matcher rowMatcher = ROW_PATTERN.matcher(html);
        while (rowMatcher.find()) {
            String row = rowMatcher.group(1);
            Matcher numberMatcher = ENTERPRISE_NUMBER_PATTERN.matcher(row);
            if (!numberMatcher.find()) {
                continue;
            }
            String rowText = stripHtml(row);
            List<String> cells = extractCells(row);
            String name = cells.size() >= 5 ? cells.get(4) : extractName(rowText, query);
            String address = cells.size() >= 6 ? cells.get(5) : extractAddress(rowText);
            String statusText = cells.size() >= 2 ? cells.get(1) : rowText;
            RegistryStatus registryStatus = readRegistryStatus(statusText, rowText);
            companies.add(CompanyRegistryCompanyResponse.builder()
                    .enterpriseNumber("BE " + numberMatcher.group(1))
                    .name(name)
                    .legalForm("")
                    .status(registryStatus.label())
                    .address(address)
                    .postalCode("")
                    .city("")
                    .naceCode("")
                    .activityLabel("")
                    .source("BCE Public Search")
                    .active(registryStatus.active())
                    .build());
        }
        return companies.stream()
                .distinct()
                .limit(10)
                .toList();
    }

    private RegistryStatus readRegistryStatus(String statusText, String rowText) {
        String lowerStatus = normalizeStatusText(statusText);
        String lowerRow = normalizeStatusText(rowText);
        if (containsInactiveStatus(lowerStatus) || containsInactiveStatus(lowerRow)) {
            return new RegistryStatus("INACTIF", false);
        }
        if (containsActiveStatus(lowerStatus) || containsActiveStatus(lowerRow)) {
            return new RegistryStatus("ACTIF", true);
        }
        return new RegistryStatus("A VERIFIER", false);
    }

    private boolean containsActiveStatus(String value) {
        return value.contains("actif") || value.contains("actief") || value.contains("active");
    }

    private boolean containsInactiveStatus(String value) {
        return value.contains("inactif")
                || value.contains("inactief")
                || value.contains("radi")
                || value.contains("stopgezet")
                || value.contains("cess")
                || value.contains("supprim");
    }

    private String normalizeStatusText(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    private record RegistryStatus(String label, boolean active) {
    }

    private List<String> extractCells(String row) {
        List<String> cells = new ArrayList<>();
        Matcher matcher = CELL_PATTERN.matcher(row);
        while (matcher.find()) {
            cells.add(stripHtml(matcher.group(1)));
        }
        return cells;
    }

    private String extractName(String rowText, String query) {
        String normalizedQuery = query == null ? "" : query.replaceAll("\\d", "").trim();
        if (!normalizedQuery.isBlank() && rowText.toLowerCase().contains(normalizedQuery.toLowerCase())) {
            return normalizedQuery.toUpperCase();
        }
        String[] chunks = rowText.split("\\s{2,}");
        return chunks.length > 0 ? chunks[chunks.length - 1].trim() : "Entreprise BCE";
    }

    private String extractAddress(String rowText) {
        Matcher matcher = Pattern.compile("\\b\\d{4}\\s+[A-Za-zÀ-ÿ' -]+").matcher(rowText);
        return matcher.find() ? matcher.group().trim() : "";
    }

    private String stripHtml(String html) {
        return html.replaceAll("(?is)<script.*?</script>", " ")
                .replaceAll("(?is)<style.*?</style>", " ")
                .replaceAll("<[^>]+>", " ")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replaceAll("\\s+", " ")
                .trim();
    }
}

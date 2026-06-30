package be.profacile.savefunds.domain.service.impl;

import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.domain.entity.BnbAnnualAccountsLookup;
import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.entity.FinancialSnapshot;
import be.profacile.savefunds.domain.enums.BnbAnnualAccountsStatus;
import be.profacile.savefunds.domain.enums.FinancialSnapshotSource;
import be.profacile.savefunds.domain.repository.BnbAnnualAccountsLookupRepository;
import be.profacile.savefunds.domain.repository.EntrepriseRepository;
import be.profacile.savefunds.domain.repository.FinancialSnapshotRepository;
import be.profacile.savefunds.domain.service.BnbAnnualAccountsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BnbAnnualAccountsServiceImpl implements BnbAnnualAccountsService {

    private static final String SOURCE = "BNB/CBSO public annual accounts";
    private static final String BNB_BASE_URL = "https://consult.cbso.nbb.be";

    private final EntrepriseRepository entrepriseRepository;
    private final BnbAnnualAccountsLookupRepository lookupRepository;
    private final FinancialSnapshotRepository financialSnapshotRepository;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Override
    public BnbAnnualAccountsLookup search(Long entrepriseId) {
        Entreprise entreprise = entrepriseRepository.findById(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise introuvable: " + entrepriseId));

        String enterpriseNumber = normalizeEnterpriseNumber(entreprise.getNumeroEntreprise());
        String consultUrl = BNB_BASE_URL + "/consult-enterprise";
        String apiUrl = BNB_BASE_URL + "/api/rs-consult/published-deposits"
                + "?page=0&size=10&enterpriseNumber=" + URLEncoder.encode(enterpriseNumber, StandardCharsets.UTF_8)
                + "&allSearchText=&sort=periodEndDate,desc";

        BnbAnnualAccountsLookup lookup = new BnbAnnualAccountsLookup();
        lookup.setEntreprise(entreprise);
        lookup.setEnterpriseNumber(enterpriseNumber);
        lookup.setConsultUrl(consultUrl);
        lookup.setSource(SOURCE);

        try {
            HttpResponse<String> response = httpClient.send(buildRequest(apiUrl), HttpResponse.BodyHandlers.ofString());
            lookup.setRawMetadata("httpStatus=" + response.statusCode() + ";apiUrl=" + apiUrl + ";body=" + response.body());
            if (response.statusCode() >= 200 && response.statusCode() < 400) {
                applyPublishedDepositsResponse(lookup, response.body());
            } else {
                lookup.setStatus(BnbAnnualAccountsStatus.UNAVAILABLE);
                lookup.setMessage("La source BNB/CBSO a repondu avec le statut HTTP " + response.statusCode() + ".");
            }
        } catch (Exception ex) {
            lookup.setStatus(BnbAnnualAccountsStatus.UNAVAILABLE);
            lookup.setMessage("Recherche BNB/CBSO indisponible: " + ex.getMessage());
            lookup.setRawMetadata("url=" + consultUrl + ";error=" + ex.getClass().getSimpleName());
        }

        return lookupRepository.save(lookup);
    }

    @Override
    public Optional<BnbAnnualAccountsLookup> findLatest(Long entrepriseId) {
        return lookupRepository.findTopByEntrepriseIdOrderByCreatedAtDesc(entrepriseId);
    }

    @Override
    public FinancialSnapshot createSnapshotFromLatestDeposit(Long entrepriseId) {
        BnbAnnualAccountsLookup lookup = findLatest(entrepriseId)
                .filter(candidate -> candidate.getStatus() == BnbAnnualAccountsStatus.FOUND)
                .orElseGet(() -> search(entrepriseId));

        if (lookup.getStatus() != BnbAnnualAccountsStatus.FOUND || lookup.getCsvUrl() == null) {
            throw new IllegalStateException("Aucun depot BNB exploitable pour creer un snapshot financier");
        }

        try {
            HttpResponse<String> response = httpClient.send(buildRequest(lookup.getCsvUrl()), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 400) {
                throw new IllegalStateException("BNB CSV HTTP " + response.statusCode());
            }
            Map<String, String> values = parseBnbCsv(response.body());
            FinancialSnapshot snapshot = toBnbSnapshot(lookup, values);
            return financialSnapshotRepository.save(snapshot);
        } catch (Exception ex) {
            throw new IllegalStateException("Import du CSV BNB impossible: " + ex.getMessage(), ex);
        }
    }

    private HttpRequest buildRequest(String url) {
        return HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(8))
                .header("User-Agent", "SaveFunds-TFE/1.0")
                .header("Accept", "application/json,text/csv,*/*")
                .GET()
                .build();
    }

    private void applyPublishedDepositsResponse(BnbAnnualAccountsLookup lookup, String body) throws Exception {
        JsonNode root = objectMapper.readTree(body);
        int totalElements = root.path("totalElements").asInt(root.path("content").size());
        lookup.setDepositsCount(totalElements);

        JsonNode deposits = root.path("content");
        if (!deposits.isArray() || deposits.isEmpty()) {
            lookup.setStatus(BnbAnnualAccountsStatus.NOT_FOUND);
            lookup.setMessage("Aucun depot de comptes annuels BNB trouve pour ce numero d'entreprise.");
            return;
        }

        JsonNode latest = deposits.get(0);
        String depositId = latest.path("id").asText();
        lookup.setStatus(BnbAnnualAccountsStatus.FOUND);
        lookup.setLatestDepositId(depositId);
        lookup.setLatestReference(latest.path("reference").asText());
        lookup.setLatestModelName(latest.path("modelName").asText());
        lookup.setLatestPeriodEndDate(latest.path("periodEndDate").asText());
        lookup.setLatestDepositDate(latest.path("depositDate").asText());
        lookup.setXbrlUrl(BNB_BASE_URL + "/api/external/broker/public/deposits/xbrl/" + depositId);
        lookup.setPdfUrl(BNB_BASE_URL + "/api/external/broker/public/deposits/pdf/" + depositId);
        lookup.setCsvUrl(BNB_BASE_URL + "/api/external/broker/public/deposits/consult/csv/" + depositId);
        lookup.setMessage("Dernier depot BNB trouve: reference " + lookup.getLatestReference()
                + ", exercice " + lookup.getLatestPeriodEndDate()
                + ". Fichiers PDF/XBRL/CSV disponibles via la source officielle.");
    }

    private String normalizeEnterpriseNumber(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }

    private FinancialSnapshot toBnbSnapshot(BnbAnnualAccountsLookup lookup, Map<String, String> values) {
        FinancialSnapshot snapshot = new FinancialSnapshot();
        snapshot.setEntreprise(lookup.getEntreprise());
        snapshot.setSource(FinancialSnapshotSource.BNB_API);
        snapshot.setSourceReference(lookup.getLatestReference());
        snapshot.setSnapshotDate(parseDate(lookup.getLatestPeriodEndDate()).orElse(LocalDate.now()));
        snapshot.setTresorerie(amount(values, "54/58").orElse(null));
        snapshot.setDettesCourtTerme(amount(values, "42/48").orElse(amount(values, "17/49").orElse(null)));
        snapshot.setCreancesClients(amount(values, "40").orElse(amount(values, "40/41").orElse(null)));
        snapshot.setChiffreAffairesMensuel(amount(values, "70").map(value -> divideByTwelve(value)).orElse(null));
        snapshot.setChargesMensuelles(monthlyCharges(values).orElse(null));
        snapshot.setSoldeCompteCourant(null);
        snapshot.setDureeCompteCourantDebiteur(null);
        snapshot.setConfidenceScore(78);
        snapshot.setWarnings(String.join("\n", List.of(
                "Source BNB officielle basee sur le depot annuel " + lookup.getLatestReference(),
                "Tresorerie issue du code 54/58 a la date de cloture, pas un solde bancaire actuel",
                "Compte courant dirigeant non deduit automatiquement depuis les comptes annuels BNB",
                "A completer par un bilan provisoire comptable et/ou un extrait bancaire recent avant une decision de retrait"
        )));
        snapshot.setMissingFields(String.join("\n", List.of(
                "Solde bancaire courant",
                "Compte courant dirigeant detaille",
                "Echeances TVA/ONSS futures"
        )));
        snapshot.setRawMetadata("provider=BNB_CBSO;reference=" + lookup.getLatestReference()
                + ";depositId=" + lookup.getLatestDepositId()
                + ";csvUrl=" + lookup.getCsvUrl());
        return snapshot;
    }

    private Optional<BigDecimal> monthlyCharges(Map<String, String> values) {
        BigDecimal total = BigDecimal.ZERO;
        boolean found = false;
        for (String code : List.of("60/61", "62", "630", "640/8", "65")) {
            Optional<BigDecimal> amount = amount(values, code);
            if (amount.isPresent()) {
                total = total.add(amount.get());
                found = true;
            }
        }
        return found ? Optional.of(divideByTwelve(total)) : Optional.empty();
    }

    private BigDecimal divideByTwelve(BigDecimal value) {
        return value.divide(new BigDecimal("12"), 2, java.math.RoundingMode.HALF_UP);
    }

    private Optional<BigDecimal> amount(Map<String, String> values, String code) {
        String raw = values.get(code);
        if (raw == null || raw.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(new BigDecimal(raw.trim().replace(",", ".")));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private Optional<LocalDate> parseDate(String raw) {
        if (raw == null || raw.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(LocalDate.parse(raw.substring(0, 10)));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private Map<String, String> parseBnbCsv(String csv) {
        Map<String, String> values = new HashMap<>();
        for (String line : csv.split("\\R")) {
            List<String> cells = parseCsvLine(line);
            if (cells.size() >= 2) {
                values.put(cells.get(0), cells.get(1));
            }
        }
        return values;
    }

    private List<String> parseCsvLine(String line) {
        java.util.ArrayList<String> cells = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                quoted = !quoted;
            } else if (c == ',' && !quoted) {
                cells.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        cells.add(current.toString().trim());
        return cells;
    }
}

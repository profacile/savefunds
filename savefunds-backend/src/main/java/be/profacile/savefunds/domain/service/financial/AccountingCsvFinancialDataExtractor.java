package be.profacile.savefunds.domain.service.financial;

import be.profacile.savefunds.domain.enums.FinancialSnapshotSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AccountingCsvFinancialDataExtractor implements FinancialDataExtractor {

    private final BelgianAccountingMapper accountingMapper;

    @Override
    public FinancialSnapshotSource source() {
        return FinancialSnapshotSource.ACCOUNTING_CSV;
    }

    @Override
    public String parserVersion() {
        return "accounting-csv-pcmn-v1";
    }

    @Override
    public ExtractedFinancialData extract(MultipartFile file) {
        Map<String, BigDecimal> balances = new LinkedHashMap<>();
        List<String> warnings = new ArrayList<>();
        int rows = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String header = reader.readLine();
            if (header == null || !header.toLowerCase().contains("account")) {
                throw new IllegalArgumentException("CSV comptable invalide: colonnes attendues accountCode,label,amount");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] columns = splitCsvLine(line);
                if (columns.length < 3) {
                    warnings.add("Ligne comptable ignoree: " + line);
                    continue;
                }
                String accountCode = columns[0].trim().replace("\"", "");
                BigDecimal amount = parseAmount(columns[2]);
                balances.merge(accountCode, amount, BigDecimal::add);
                rows++;
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Impossible de parser le CSV comptable: " + ex.getMessage(), ex);
        }

        BelgianAccountingMapper.AccountingBuckets buckets = accountingMapper.map(balances);
        List<String> missing = new ArrayList<>();
        if (buckets.getRevenue().signum() == 0) {
            missing.add("chiffreAffairesMensuel");
        }
        if (buckets.getExpenses().signum() == 0) {
            missing.add("chargesMensuelles");
        }
        if (buckets.getCash().signum() == 0) {
            missing.add("tresorerie");
        }
        if (!buckets.getIgnoredAccounts().isEmpty()) {
            warnings.add("Comptes non exploites: " + buckets.getIgnoredAccounts().keySet());
        }

        return ExtractedFinancialData.builder()
                .chiffreAffairesMensuel(buckets.getRevenue())
                .chargesMensuelles(buckets.getExpenses())
                .tresorerie(buckets.getCash())
                .soldeCompteCourant(buckets.getCurrentAccount())
                .dettesCourtTerme(buckets.getSupplierDebt())
                .creancesClients(buckets.getCustomerReceivables())
                .dureeCompteCourantDebiteur(buckets.getCurrentAccount().signum() < 0 ? 31 : 0)
                .snapshotDate(LocalDate.now())
                .confidenceScore(missing.isEmpty() ? 85 : 60)
                .warnings(warnings)
                .missingFields(missing)
                .rawMetadata("rows=" + rows + ";mappedAccounts=" + balances.size() + ";ignoredAccounts=" + buckets.getIgnoredAccounts().size())
                .build();
    }

    private String[] splitCsvLine(String line) {
        return line.split(";", -1).length > 1 ? line.split(";", -1) : line.split(",", -1);
    }

    private BigDecimal parseAmount(String value) {
        String normalized = value == null ? "0" : value.trim().replace("\"", "").replace(" ", "").replace(",", ".");
        return normalized.isBlank() ? BigDecimal.ZERO : new BigDecimal(normalized);
    }
}

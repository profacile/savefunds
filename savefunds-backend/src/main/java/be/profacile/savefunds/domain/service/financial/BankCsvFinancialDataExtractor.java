package be.profacile.savefunds.domain.service.financial;

import be.profacile.savefunds.domain.enums.FinancialSnapshotSource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class BankCsvFinancialDataExtractor implements FinancialDataExtractor {

    @Override
    public FinancialSnapshotSource source() {
        return FinancialSnapshotSource.BANK_CSV;
    }

    @Override
    public String parserVersion() {
        return "bank-csv-v1";
    }

    @Override
    public ExtractedFinancialData extract(MultipartFile file) {
        List<String> warnings = new ArrayList<>();
        BigDecimal incoming = BigDecimal.ZERO;
        BigDecimal outgoing = BigDecimal.ZERO;
        BigDecimal finalBalance = null;
        LocalDate lastDate = null;
        int rows = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String header = reader.readLine();
            if (header == null || !header.toLowerCase().contains("amount")) {
                throw new IllegalArgumentException("CSV bancaire invalide: colonnes attendues date,description,amount,balance");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] columns = splitCsvLine(line);
                if (columns.length < 4) {
                    warnings.add("Ligne bancaire ignoree: " + line);
                    continue;
                }

                BigDecimal amount = parseAmount(columns[2]);
                BigDecimal balance = parseAmount(columns[3]);
                LocalDate date = parseDate(columns[0]);

                if (amount.signum() > 0) {
                    incoming = incoming.add(amount);
                } else {
                    outgoing = outgoing.add(amount.abs());
                }
                finalBalance = balance;
                lastDate = date != null ? date : lastDate;
                rows++;
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Impossible de parser le CSV bancaire: " + ex.getMessage(), ex);
        }

        List<String> missing = new ArrayList<>();
        if (finalBalance == null) {
            missing.add("tresorerie");
            finalBalance = BigDecimal.ZERO;
        }
        if (outgoing.signum() == 0) {
            missing.add("chargesMensuelles");
            warnings.add("Aucune sortie bancaire detectee: les charges mensuelles seront a verifier.");
        }

        return ExtractedFinancialData.builder()
                .chiffreAffairesMensuel(incoming)
                .chargesMensuelles(outgoing)
                .tresorerie(finalBalance)
                .soldeCompteCourant(BigDecimal.ZERO)
                .snapshotDate(lastDate != null ? lastDate : LocalDate.now())
                .confidenceScore(missing.isEmpty() ? 80 : 55)
                .warnings(warnings)
                .missingFields(missing)
                .rawMetadata("rows=" + rows + ";incoming=" + incoming + ";outgoing=" + outgoing)
                .build();
    }

    private String[] splitCsvLine(String line) {
        return line.split(";", -1).length > 1 ? line.split(";", -1) : line.split(",", -1);
    }

    private BigDecimal parseAmount(String value) {
        String normalized = value == null ? "0" : value.trim().replace("\"", "").replace(" ", "").replace(",", ".");
        return normalized.isBlank() ? BigDecimal.ZERO : new BigDecimal(normalized);
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value.trim().replace("\"", ""));
        } catch (Exception ignored) {
            return null;
        }
    }
}

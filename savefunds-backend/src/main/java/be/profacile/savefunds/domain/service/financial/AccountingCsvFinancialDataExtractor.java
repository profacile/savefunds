package be.profacile.savefunds.domain.service.financial;

import be.profacile.savefunds.domain.enums.FinancialSnapshotSource;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (!isCsvFile(file)) {
            if (isPdfFile(file)) {
                return extractPdfBalanceSheet(file);
            }
            return acceptedDocumentAwaitingExtraction(file);
        }

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
        String raw = value == null ? "0" : value.trim().replace("\"", "");
        Matcher matcher = Pattern.compile("[-+]?\\d[\\d\\s.]*,\\d{2}|[-+]?\\d[\\d\\s.]*").matcher(raw);
        String amount = null;
        while (matcher.find()) {
            amount = matcher.group();
        }
        String compact = amount == null ? "0" : amount.replace(" ", "");
        String normalized = compact.contains(",")
                ? compact.replace(".", "").replace(",", ".")
                : compact;
        return normalized.isBlank() ? BigDecimal.ZERO : new BigDecimal(normalized);
    }

    private boolean isCsvFile(MultipartFile file) {
        String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        return filename.endsWith(".csv") || filename.endsWith(".txt");
    }

    private boolean isPdfFile(MultipartFile file) {
        String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        return filename.endsWith(".pdf");
    }

    private ExtractedFinancialData extractPdfBalanceSheet(MultipartFile file) {
        String filename = file.getOriginalFilename() == null ? "bilan-provisoire.pdf" : file.getOriginalFilename();
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            String text = new PDFTextStripper().getText(document);
            int months = extractPeriodMonths(text);

            BigDecimal revenue = findAmount(text, List.of(
                    Pattern.compile("(?im)^\\s*Chiffre d'affaires\\s+70\\s+(.+?)\\s*$"),
                    Pattern.compile("(?im)^\\s*700\\d*\\s+.+?\\s+(.+?)\\s*$")
            ));
            BigDecimal operatingExpenses = findAmount(text, List.of(
                    Pattern.compile("(?im)^\\s*Approvisionnements,?.*?60/61\\s+(.+?)\\s*$"),
                    Pattern.compile("(?im)^\\s*Services et biens divers\\s+61\\s+(.+?)\\s*$")
            ));
            BigDecimal financialExpenses = findAmount(text, List.of(
                    Pattern.compile("(?im)^\\s*Charges financières\\s+65/66B\\s+(.+?)\\s*$"),
                    Pattern.compile("(?im)^\\s*Charges financieres\\s+65/66B\\s+(.+?)\\s*$")
            ));
            BigDecimal taxes = findAmount(text, List.of(
                    Pattern.compile("(?im)^\\s*Impôts sur le résultat.*?67/77\\s+(.+?)\\s*$"),
                    Pattern.compile("(?im)^\\s*Impots sur le resultat.*?67/77\\s+(.+?)\\s*$")
            ));
            BigDecimal cash = findAmount(text, List.of(
                    Pattern.compile("(?im)^\\s*Valeurs disponibles\\s+54/58\\s+(.+?)\\s*$"),
                    Pattern.compile("(?im)^\\s*550\\d*\\s+.+?\\s+(.+?)\\s*$")
            ));
            BigDecimal currentAccount = findAmount(text, List.of(
                    Pattern.compile("(?im)^\\s*416\\d*\\s+.+?\\s+(.+?)\\s*$"),
                    Pattern.compile("(?im)^\\s*Autres créances\\s+41\\s+(.+?)\\s*$"),
                    Pattern.compile("(?im)^\\s*Autres creances\\s+41\\s+(.+?)\\s*$")
            ));
            BigDecimal shortTermDebt = findAmount(text, List.of(
                    Pattern.compile("(?im)^\\s*Dettes à un an au plus\\s+42/48\\s+(.+?)\\s*$"),
                    Pattern.compile("(?im)^\\s*Dettes a un an au plus\\s+42/48\\s+(.+?)\\s*$")
            ));
            BigDecimal receivables = findAmount(text, List.of(
                    Pattern.compile("(?im)^\\s*Créances commerciales\\s+40\\s+(.+?)\\s*$"),
                    Pattern.compile("(?im)^\\s*Creances commerciales\\s+40\\s+(.+?)\\s*$")
            ));

            BigDecimal expenses = sumNotNull(operatingExpenses, financialExpenses, taxes);
            List<String> missing = missingFields(revenue, expenses, cash, currentAccount);
            List<String> warnings = new ArrayList<>();
            warnings.add("Bilan provisoire PDF parse automatiquement depuis " + filename);
            warnings.add("Extraction a valider par le comptable: les libelles PDF peuvent varier selon le logiciel comptable.");
            if (months > 1) {
                warnings.add("CA et charges convertis en moyenne mensuelle sur " + months + " mois.");
            }

            return ExtractedFinancialData.builder()
                    .chiffreAffairesMensuel(monthly(revenue, months))
                    .chargesMensuelles(monthly(expenses, months))
                    .tresorerie(cash)
                    .soldeCompteCourant(currentAccount == null ? null : currentAccount.abs().negate())
                    .dettesCourtTerme(shortTermDebt)
                    .creancesClients(receivables)
                    .dureeCompteCourantDebiteur(currentAccount != null && currentAccount.signum() != 0 ? 31 : 0)
                    .snapshotDate(LocalDate.now())
                    .confidenceScore(missing.isEmpty() ? 78 : 55)
                    .warnings(warnings)
                    .missingFields(missing)
                    .rawMetadata("documentReceived=true;filename=" + filename + ";parser=PDF_TEXT_V1;periodMonths=" + months + ";textLength=" + text.length())
                    .build();
        } catch (IOException ex) {
            throw new IllegalArgumentException("Impossible de lire le PDF comptable: " + ex.getMessage(), ex);
        }
    }

    private int extractPeriodMonths(String text) {
        Matcher matcher = Pattern.compile("(?i)du\\s+(\\d{1,2})/(\\d{4})\\s+au\\s+(\\d{1,2})/(\\d{4})").matcher(text);
        if (!matcher.find()) {
            return 12;
        }
        YearMonth from = YearMonth.of(Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(1)));
        YearMonth to = YearMonth.of(Integer.parseInt(matcher.group(4)), Integer.parseInt(matcher.group(3)));
        int months = (to.getYear() - from.getYear()) * 12 + to.getMonthValue() - from.getMonthValue() + 1;
        return Math.max(months, 1);
    }

    private BigDecimal findAmount(String text, List<Pattern> patterns) {
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return parseAmount(matcher.group(1));
            }
        }
        return null;
    }

    private BigDecimal sumNotNull(BigDecimal... values) {
        BigDecimal total = BigDecimal.ZERO;
        boolean found = false;
        for (BigDecimal value : values) {
            if (value != null) {
                total = total.add(value.abs());
                found = true;
            }
        }
        return found ? total : null;
    }

    private BigDecimal monthly(BigDecimal value, int months) {
        if (value == null) {
            return null;
        }
        return value.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
    }

    private List<String> missingFields(BigDecimal revenue, BigDecimal expenses, BigDecimal cash, BigDecimal currentAccount) {
        List<String> missing = new ArrayList<>();
        if (revenue == null) {
            missing.add("chiffreAffairesMensuel");
        }
        if (expenses == null) {
            missing.add("chargesMensuelles");
        }
        if (cash == null) {
            missing.add("tresorerie");
        }
        if (currentAccount == null) {
            missing.add("soldeCompteCourant");
        }
        return missing;
    }

    private ExtractedFinancialData acceptedDocumentAwaitingExtraction(MultipartFile file) {
        String filename = file.getOriginalFilename() == null ? "document-bilan" : file.getOriginalFilename();
        return ExtractedFinancialData.builder()
                .snapshotDate(LocalDate.now())
                .confidenceScore(20)
                .warnings(List.of(
                        "Document bilan provisoire recu: " + filename,
                        "Extraction automatique non encore effectuee pour ce format. Le document doit etre relu par le comptable ou traite par le connecteur IA/OCR."
                ))
                .missingFields(List.of(
                        "chiffreAffairesMensuel",
                        "chargesMensuelles",
                        "tresorerie",
                        "soldeCompteCourant"
                ))
                .rawMetadata("documentReceived=true;filename=" + filename + ";parserStatus=AWAITING_REVIEW;expectedParser=IA_OCR_OR_ACCOUNTANT_VALIDATION")
                .build();
    }
}

package be.profacile.savefunds.domain.service.impl;

import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.domain.entity.BankTransaction;
import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.entity.FinancialSnapshot;
import be.profacile.savefunds.domain.enums.TransactionClassificationType;
import be.profacile.savefunds.domain.repository.BankTransactionRepository;
import be.profacile.savefunds.domain.repository.EntrepriseRepository;
import be.profacile.savefunds.domain.repository.FinancialSnapshotRepository;
import be.profacile.savefunds.domain.service.BankTransactionService;
import be.profacile.savefunds.domain.service.transaction.TransactionClassificationResult;
import be.profacile.savefunds.domain.service.transaction.TransactionClassifierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankTransactionServiceImpl implements BankTransactionService {

    private final EntrepriseRepository entrepriseRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final FinancialSnapshotRepository financialSnapshotRepository;
    private final TransactionClassifierService transactionClassifierService;

    @Override
    public List<BankTransaction> importAndClassify(Long entrepriseId, FinancialSnapshot snapshot, MultipartFile file) {
        Entreprise entreprise = entrepriseRepository.findById(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise introuvable: " + entrepriseId));

        List<BankTransaction> transactions = parseTransactions(entreprise, snapshot, file);
        List<BankTransaction> saved = bankTransactionRepository.saveAll(transactions);
        applyDirectorCurrentAccountImpact(snapshot, saved);
        return saved;
    }

    @Override
    public List<BankTransaction> findByEntreprise(Long entrepriseId) {
        return bankTransactionRepository.findByEntrepriseIdOrderByTransactionDateDescIdDesc(entrepriseId);
    }

    private List<BankTransaction> parseTransactions(Entreprise entreprise, FinancialSnapshot snapshot, MultipartFile file) {
        List<BankTransaction> transactions = new ArrayList<>();
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
                    continue;
                }
                LocalDate date = parseDate(columns[0]);
                String description = clean(columns[1]);
                BigDecimal amount = parseAmount(columns[2]);
                BigDecimal balance = parseAmount(columns[3]);
                TransactionClassificationResult classification = transactionClassifierService.classify(entreprise, date, description, amount);

                BankTransaction transaction = new BankTransaction();
                transaction.setEntreprise(entreprise);
                transaction.setFinancialSnapshot(snapshot);
                transaction.setTransactionDate(date != null ? date : snapshot.getSnapshotDate());
                transaction.setDescription(description);
                transaction.setAmount(amount);
                transaction.setBalance(balance);
                transaction.setClassification(classification.getClassification());
                transaction.setReviewStatus(classification.getReviewStatus());
                transaction.setConfidenceScore(classification.getConfidenceScore());
                transaction.setImpactsDirectorCurrentAccount(classification.isImpactsDirectorCurrentAccount());
                transaction.setDirectorCurrentAccountImpact(currentAccountImpact(amount, classification));
                transaction.setAiReason(classification.getReason());
                transactions.add(transaction);
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Impossible d'importer les transactions bancaires: " + ex.getMessage(), ex);
        }
        return transactions;
    }

    private void applyDirectorCurrentAccountImpact(FinancialSnapshot snapshot, List<BankTransaction> transactions) {
        BigDecimal impact = transactions.stream()
                .map(BankTransaction::getDirectorCurrentAccountImpact)
                .filter(value -> value != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (impact.signum() > 0) {
            snapshot.setSoldeCompteCourant(impact.negate());
            snapshot.setDureeCompteCourantDebiteur(firstDebtorDayEstimate(transactions));
            String warning = "Compte courant dirigeant estime depuis transactions bancaires classees, a valider par le comptable.";
            snapshot.setWarnings(appendLine(snapshot.getWarnings(), warning));
            financialSnapshotRepository.save(snapshot);
        }
    }

    private Integer firstDebtorDayEstimate(List<BankTransaction> transactions) {
        return transactions.stream()
                .filter(BankTransaction::isImpactsDirectorCurrentAccount)
                .map(BankTransaction::getTransactionDate)
                .min(LocalDate::compareTo)
                .map(date -> (int) java.time.temporal.ChronoUnit.DAYS.between(date, LocalDate.now()))
                .orElse(null);
    }

    private BigDecimal currentAccountImpact(BigDecimal amount, TransactionClassificationResult classification) {
        if (!classification.isImpactsDirectorCurrentAccount() || amount == null) {
            return BigDecimal.ZERO;
        }
        if (classification.getClassification() == TransactionClassificationType.REMBOURSEMENT_DIRIGEANT) {
            return amount.abs().negate();
        }
        return amount.signum() < 0 ? amount.abs() : BigDecimal.ZERO;
    }

    private String appendLine(String current, String line) {
        return current == null || current.isBlank() ? line : current + "\n" + line;
    }

    private String[] splitCsvLine(String line) {
        return line.split(";", -1).length > 1 ? line.split(";", -1) : line.split(",", -1);
    }

    private String clean(String value) {
        return value == null ? "" : value.trim().replace("\"", "");
    }

    private BigDecimal parseAmount(String value) {
        String normalized = clean(value).replace(" ", "").replace(",", ".");
        return normalized.isBlank() ? BigDecimal.ZERO : new BigDecimal(normalized);
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(clean(value));
        } catch (Exception ignored) {
            return null;
        }
    }
}

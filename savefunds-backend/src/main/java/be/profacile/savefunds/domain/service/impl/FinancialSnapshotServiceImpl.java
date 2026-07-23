package be.profacile.savefunds.domain.service.impl;

import be.profacile.savefunds.api.dto.request.CreateManualFinancialSnapshotRequest;
import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.domain.entity.Company;
import be.profacile.savefunds.domain.entity.FinancialSnapshot;
import be.profacile.savefunds.domain.entity.ImportJob;
import be.profacile.savefunds.domain.enums.FinancialSnapshotSource;
import be.profacile.savefunds.domain.enums.ImportJobStatus;
import be.profacile.savefunds.domain.repository.CompanyRepository;
import be.profacile.savefunds.domain.repository.FinancialSnapshotRepository;
import be.profacile.savefunds.domain.repository.ImportJobRepository;
import be.profacile.savefunds.domain.service.FinancialSnapshotService;
import be.profacile.savefunds.domain.service.financial.ExtractedFinancialData;
import be.profacile.savefunds.domain.service.financial.ExternalFinancialDataProvider;
import be.profacile.savefunds.domain.service.financial.FinancialDataExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class FinancialSnapshotServiceImpl implements FinancialSnapshotService {

    private final CompanyRepository companyRepository;
    private final FinancialSnapshotRepository snapshotRepository;
    private final ImportJobRepository importJobRepository;
    private final List<FinancialDataExtractor> extractors;
    private final List<ExternalFinancialDataProvider> externalProviders;

    @Override
    @Transactional
    public FinancialSnapshot createManualSnapshot(Long companyId, CreateManualFinancialSnapshotRequest request) {
        Company company = getCompany(companyId);

        FinancialSnapshot snapshot = new FinancialSnapshot();
        snapshot.setCompany(company);
        snapshot.setSource(FinancialSnapshotSource.MANUAL);
        snapshot.setSourceReference("manual-input");
        snapshot.setMonthlyRevenue(request.getMonthlyRevenue());
        snapshot.setMonthlyExpenses(request.getMonthlyExpenses());
        snapshot.setCashBalance(request.getCashBalance());
        snapshot.setDirectorCurrentAccountBalance(request.getDirectorCurrentAccountBalance());
        snapshot.setShortTermDebt(request.getShortTermDebt());
        snapshot.setCustomerReceivables(request.getCustomerReceivables());
        snapshot.setDirectorCurrentAccountDebtorDays(request.getDirectorCurrentAccountDebtorDays());
        snapshot.setSnapshotDate(request.getSnapshotDate() != null ? request.getSnapshotDate() : LocalDate.now());
        snapshot.setConfidenceScore(100);
        snapshot.setWarnings("");
        snapshot.setMissingFields("");
        snapshot.setRawMetadata("createdFrom=manual");
        return snapshotRepository.save(snapshot);
    }

    @Override
    @Transactional
    public FinancialSnapshot importSnapshot(Long companyId, MultipartFile file, FinancialSnapshotSource source, Long userId) {
        Company company = getCompany(companyId);
        FinancialDataExtractor extractor = extractors.stream()
                .filter(candidate -> candidate.source() == source)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aucun extracteur disponible pour la source " + source));

        ImportJob importJob = new ImportJob();
        importJob.setCompany(company);
        importJob.setSource(source);
        importJob.setStatus(ImportJobStatus.UPLOADED);
        importJob.setFileName(file.getOriginalFilename());
        importJob.setParserVersion(extractor.parserVersion());
        importJob.setCreatedByUserId(userId);
        importJob = importJobRepository.save(importJob);

        try {
            ExtractedFinancialData data = extractor.extract(file);
            FinancialSnapshot snapshot = toSnapshot(company, source, file.getOriginalFilename(), data);
            snapshot = snapshotRepository.save(snapshot);

            importJob.setSnapshot(snapshot);
            importJob.setStatus(data.getMissingFields().isEmpty() ? ImportJobStatus.PARSED : ImportJobStatus.PARTIAL);
            importJob.setSummary("Snapshot cree avec " + data.getMissingFields().size() + " champ(s) manquant(s)");
            importJobRepository.save(importJob);

            return snapshot;
        } catch (RuntimeException ex) {
            importJob.setStatus(ImportJobStatus.FAILED);
            importJob.setErrorMessage(ex.getMessage());
            importJobRepository.save(importJob);
            throw ex;
        }
    }

    @Override
    @Transactional
    public FinancialSnapshot createExternalSnapshot(Long companyId, FinancialSnapshotSource source, Long userId) {
        Company company = getCompany(companyId);
        ExternalFinancialDataProvider provider = externalProviders.stream()
                .filter(candidate -> candidate.source() == source)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aucun provider externe disponible pour la source " + source));

        ImportJob importJob = new ImportJob();
        importJob.setCompany(company);
        importJob.setSource(source);
        importJob.setStatus(ImportJobStatus.UPLOADED);
        importJob.setFileName(provider.providerName());
        importJob.setParserVersion(provider.providerVersion());
        importJob.setCreatedByUserId(userId);
        importJob = importJobRepository.save(importJob);

        try {
            ExtractedFinancialData data = provider.fetch(company);
            FinancialSnapshot snapshot = toSnapshot(company, source, provider.providerName(), data);
            snapshot = snapshotRepository.save(snapshot);

            importJob.setSnapshot(snapshot);
            importJob.setStatus(data.getMissingFields().isEmpty() ? ImportJobStatus.PARSED : ImportJobStatus.PARTIAL);
            importJob.setSummary("Snapshot mock externe cree via " + provider.providerName());
            importJobRepository.save(importJob);

            return snapshot;
        } catch (RuntimeException ex) {
            importJob.setStatus(ImportJobStatus.FAILED);
            importJob.setErrorMessage(ex.getMessage());
            importJobRepository.save(importJob);
            throw ex;
        }
    }

    @Override
    public Optional<FinancialSnapshot> findLatest(Long companyId) {
        return snapshotRepository.findTopByCompanyIdOrderBySnapshotDateDescCreatedAtDesc(companyId);
    }

    @Override
    public Optional<FinancialSnapshot> findLatestBySource(Long companyId, FinancialSnapshotSource source) {
        getCompany(companyId);
        return snapshotRepository.findTopByCompanyIdAndSourceOrderBySnapshotDateDescCreatedAtDesc(companyId, source);
    }

    @Override
    public List<FinancialSnapshot> findAll(Long companyId) {
        getCompany(companyId);
        return snapshotRepository.findAllByCompanyIdOrderBySnapshotDateDescCreatedAtDesc(companyId);
    }

    @Override
    public Optional<FinancialSnapshot> buildConsolidatedSnapshot(Long companyId) {
        Company company = getCompany(companyId);
        List<FinancialSnapshot> snapshots = snapshotRepository.findAllByCompanyIdOrderBySnapshotDateDescCreatedAtDesc(companyId);
        if (snapshots.isEmpty()) {
            return Optional.empty();
        }

        FinancialSnapshot consolidated = new FinancialSnapshot();
        consolidated.setCompany(company);
        consolidated.setSource(FinancialSnapshotSource.MANUAL);
        consolidated.setSourceReference("consolidated-hierarchy");
        consolidated.setSnapshotDate(LocalDate.now());
        consolidated.setCashBalance(firstValue(snapshots, FinancialSnapshot::getCashBalance,
                FinancialSnapshotSource.BANK_CSV,
                FinancialSnapshotSource.ACCOUNTING_CSV,
                FinancialSnapshotSource.BALANCE_SHEET_DOCUMENT,
                FinancialSnapshotSource.BNB_API
        ));
        consolidated.setMonthlyRevenue(firstValue(snapshots, FinancialSnapshot::getMonthlyRevenue,
                FinancialSnapshotSource.ACCOUNTING_CSV,
                FinancialSnapshotSource.BALANCE_SHEET_DOCUMENT,
                FinancialSnapshotSource.BNB_API
        ));
        consolidated.setMonthlyExpenses(firstValue(snapshots, FinancialSnapshot::getMonthlyExpenses,
                FinancialSnapshotSource.ACCOUNTING_CSV,
                FinancialSnapshotSource.BALANCE_SHEET_DOCUMENT,
                FinancialSnapshotSource.BNB_API
        ));
        consolidated.setDirectorCurrentAccountBalance(firstValue(snapshots, FinancialSnapshot::getDirectorCurrentAccountBalance,
                FinancialSnapshotSource.BANK_CSV,
                FinancialSnapshotSource.ACCOUNTING_CSV,
                FinancialSnapshotSource.BALANCE_SHEET_DOCUMENT,
                FinancialSnapshotSource.BNB_API
        ));
        consolidated.setDirectorCurrentAccountDebtorDays(firstValue(snapshots, FinancialSnapshot::getDirectorCurrentAccountDebtorDays,
                FinancialSnapshotSource.BANK_CSV,
                FinancialSnapshotSource.ACCOUNTING_CSV,
                FinancialSnapshotSource.BALANCE_SHEET_DOCUMENT,
                FinancialSnapshotSource.BNB_API
        ));
        consolidated.setShortTermDebt(firstValue(snapshots, FinancialSnapshot::getShortTermDebt,
                FinancialSnapshotSource.ACCOUNTING_CSV,
                FinancialSnapshotSource.BALANCE_SHEET_DOCUMENT,
                FinancialSnapshotSource.BNB_API
        ));
        consolidated.setCustomerReceivables(firstValue(snapshots, FinancialSnapshot::getCustomerReceivables,
                FinancialSnapshotSource.ACCOUNTING_CSV,
                FinancialSnapshotSource.BALANCE_SHEET_DOCUMENT,
                FinancialSnapshotSource.BNB_API
        ));
        consolidated.setConfidenceScore(averageConfidence(snapshots));
        consolidated.setWarnings("Snapshot consolide selon la hierarchie SaveFunds: banque > bilan provisoire > BNB annuelle");
        consolidated.setMissingFields("");
        consolidated.setRawMetadata("sourceHierarchy=cashBalance:BANK_CSV>ACCOUNTING_CSV>BNB_API;caCharges:ACCOUNTING_CSV>BNB_API;cc:BANK_CSV>ACCOUNTING_CSV>BNB_API");
        return Optional.of(consolidated);
    }

    private FinancialSnapshot toSnapshot(Company company, FinancialSnapshotSource source, String sourceReference, ExtractedFinancialData data) {
        FinancialSnapshot snapshot = new FinancialSnapshot();
        snapshot.setCompany(company);
        snapshot.setSource(source);
        snapshot.setSourceReference(sourceReference);
        snapshot.setMonthlyRevenue(data.getMonthlyRevenue());
        snapshot.setMonthlyExpenses(data.getMonthlyExpenses());
        snapshot.setCashBalance(data.getCashBalance());
        snapshot.setDirectorCurrentAccountBalance(data.getDirectorCurrentAccountBalance());
        snapshot.setShortTermDebt(data.getShortTermDebt());
        snapshot.setCustomerReceivables(data.getCustomerReceivables());
        snapshot.setDirectorCurrentAccountDebtorDays(data.getDirectorCurrentAccountDebtorDays());
        snapshot.setSnapshotDate(data.getSnapshotDate() != null ? data.getSnapshotDate() : LocalDate.now());
        snapshot.setConfidenceScore(data.getConfidenceScore());
        snapshot.setWarnings(String.join("\n", data.getWarnings()));
        snapshot.setMissingFields(String.join("\n", data.getMissingFields()));
        snapshot.setRawMetadata(data.getRawMetadata());
        return snapshot;
    }

    private Company getCompany(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company introuvable: " + companyId));
    }

    @SafeVarargs
    private <T> T firstValue(
            List<FinancialSnapshot> snapshots,
            Function<FinancialSnapshot, T> getter,
            FinancialSnapshotSource... orderedSources
    ) {
        for (FinancialSnapshotSource source : orderedSources) {
            Optional<T> value = snapshots.stream()
                    .filter(snapshot -> snapshot.getSource() == source)
                    .map(getter)
                    .filter(candidate -> candidate != null)
                    .findFirst();
            if (value.isPresent()) {
                return value.get();
            }
        }
        return null;
    }

    private int averageConfidence(List<FinancialSnapshot> snapshots) {
        return (int) Math.round(snapshots.stream()
                .filter(snapshot -> snapshot.getConfidenceScore() != null)
                .mapToInt(FinancialSnapshot::getConfidenceScore)
                .average()
                .orElse(0));
    }
}

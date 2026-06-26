package be.profacile.savefunds.domain.service.impl;

import be.profacile.savefunds.api.dto.request.CreateManualFinancialSnapshotRequest;
import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.entity.FinancialSnapshot;
import be.profacile.savefunds.domain.entity.ImportJob;
import be.profacile.savefunds.domain.enums.FinancialSnapshotSource;
import be.profacile.savefunds.domain.enums.ImportJobStatus;
import be.profacile.savefunds.domain.repository.EntrepriseRepository;
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

@Service
@RequiredArgsConstructor
public class FinancialSnapshotServiceImpl implements FinancialSnapshotService {

    private final EntrepriseRepository entrepriseRepository;
    private final FinancialSnapshotRepository snapshotRepository;
    private final ImportJobRepository importJobRepository;
    private final List<FinancialDataExtractor> extractors;
    private final List<ExternalFinancialDataProvider> externalProviders;

    @Override
    @Transactional
    public FinancialSnapshot createManualSnapshot(Long entrepriseId, CreateManualFinancialSnapshotRequest request) {
        Entreprise entreprise = getEntreprise(entrepriseId);

        FinancialSnapshot snapshot = new FinancialSnapshot();
        snapshot.setEntreprise(entreprise);
        snapshot.setSource(FinancialSnapshotSource.MANUAL);
        snapshot.setSourceReference("manual-input");
        snapshot.setChiffreAffairesMensuel(request.getChiffreAffairesMensuel());
        snapshot.setChargesMensuelles(request.getChargesMensuelles());
        snapshot.setTresorerie(request.getTresorerie());
        snapshot.setSoldeCompteCourant(request.getSoldeCompteCourant());
        snapshot.setDettesCourtTerme(request.getDettesCourtTerme());
        snapshot.setCreancesClients(request.getCreancesClients());
        snapshot.setDureeCompteCourantDebiteur(request.getDureeCompteCourantDebiteur());
        snapshot.setSnapshotDate(request.getSnapshotDate() != null ? request.getSnapshotDate() : LocalDate.now());
        snapshot.setConfidenceScore(100);
        snapshot.setWarnings("");
        snapshot.setMissingFields("");
        snapshot.setRawMetadata("createdFrom=manual");
        return snapshotRepository.save(snapshot);
    }

    @Override
    @Transactional
    public FinancialSnapshot importSnapshot(Long entrepriseId, MultipartFile file, FinancialSnapshotSource source, Long userId) {
        Entreprise entreprise = getEntreprise(entrepriseId);
        FinancialDataExtractor extractor = extractors.stream()
                .filter(candidate -> candidate.source() == source)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aucun extracteur disponible pour la source " + source));

        ImportJob importJob = new ImportJob();
        importJob.setEntreprise(entreprise);
        importJob.setSource(source);
        importJob.setStatus(ImportJobStatus.UPLOADED);
        importJob.setFileName(file.getOriginalFilename());
        importJob.setParserVersion(extractor.parserVersion());
        importJob.setCreatedByUserId(userId);
        importJob = importJobRepository.save(importJob);

        try {
            ExtractedFinancialData data = extractor.extract(file);
            FinancialSnapshot snapshot = toSnapshot(entreprise, source, file.getOriginalFilename(), data);
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
    public FinancialSnapshot createExternalSnapshot(Long entrepriseId, FinancialSnapshotSource source, Long userId) {
        Entreprise entreprise = getEntreprise(entrepriseId);
        ExternalFinancialDataProvider provider = externalProviders.stream()
                .filter(candidate -> candidate.source() == source)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aucun provider externe disponible pour la source " + source));

        ImportJob importJob = new ImportJob();
        importJob.setEntreprise(entreprise);
        importJob.setSource(source);
        importJob.setStatus(ImportJobStatus.UPLOADED);
        importJob.setFileName(provider.providerName());
        importJob.setParserVersion(provider.providerVersion());
        importJob.setCreatedByUserId(userId);
        importJob = importJobRepository.save(importJob);

        try {
            ExtractedFinancialData data = provider.fetch(entreprise);
            FinancialSnapshot snapshot = toSnapshot(entreprise, source, provider.providerName(), data);
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
    public Optional<FinancialSnapshot> findLatest(Long entrepriseId) {
        return snapshotRepository.findTopByEntrepriseIdOrderBySnapshotDateDescCreatedAtDesc(entrepriseId);
    }

    private FinancialSnapshot toSnapshot(Entreprise entreprise, FinancialSnapshotSource source, String sourceReference, ExtractedFinancialData data) {
        FinancialSnapshot snapshot = new FinancialSnapshot();
        snapshot.setEntreprise(entreprise);
        snapshot.setSource(source);
        snapshot.setSourceReference(sourceReference);
        snapshot.setChiffreAffairesMensuel(data.getChiffreAffairesMensuel());
        snapshot.setChargesMensuelles(data.getChargesMensuelles());
        snapshot.setTresorerie(data.getTresorerie());
        snapshot.setSoldeCompteCourant(data.getSoldeCompteCourant());
        snapshot.setDettesCourtTerme(data.getDettesCourtTerme());
        snapshot.setCreancesClients(data.getCreancesClients());
        snapshot.setDureeCompteCourantDebiteur(data.getDureeCompteCourantDebiteur());
        snapshot.setSnapshotDate(data.getSnapshotDate() != null ? data.getSnapshotDate() : LocalDate.now());
        snapshot.setConfidenceScore(data.getConfidenceScore());
        snapshot.setWarnings(String.join("\n", data.getWarnings()));
        snapshot.setMissingFields(String.join("\n", data.getMissingFields()));
        snapshot.setRawMetadata(data.getRawMetadata());
        return snapshot;
    }

    private Entreprise getEntreprise(Long entrepriseId) {
        return entrepriseRepository.findById(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise introuvable: " + entrepriseId));
    }
}

package be.profacile.savefunds.domain.service;

import be.profacile.savefunds.api.dto.request.CreateManualFinancialSnapshotRequest;
import be.profacile.savefunds.domain.entity.FinancialSnapshot;
import be.profacile.savefunds.domain.enums.FinancialSnapshotSource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.List;

public interface FinancialSnapshotService {
    FinancialSnapshot createManualSnapshot(Long entrepriseId, CreateManualFinancialSnapshotRequest request);

    FinancialSnapshot importSnapshot(Long entrepriseId, MultipartFile file, FinancialSnapshotSource source, Long userId);

    FinancialSnapshot createExternalSnapshot(Long entrepriseId, FinancialSnapshotSource source, Long userId);

    Optional<FinancialSnapshot> findLatest(Long entrepriseId);

    Optional<FinancialSnapshot> findLatestBySource(Long entrepriseId, FinancialSnapshotSource source);

    List<FinancialSnapshot> findAll(Long entrepriseId);

    Optional<FinancialSnapshot> buildConsolidatedSnapshot(Long entrepriseId);
}

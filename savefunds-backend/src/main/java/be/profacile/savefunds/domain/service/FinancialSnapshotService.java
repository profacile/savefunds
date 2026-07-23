package be.profacile.savefunds.domain.service;

import be.profacile.savefunds.api.dto.request.CreateManualFinancialSnapshotRequest;
import be.profacile.savefunds.domain.entity.FinancialSnapshot;
import be.profacile.savefunds.domain.enums.FinancialSnapshotSource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.List;

public interface FinancialSnapshotService {
    FinancialSnapshot createManualSnapshot(Long companyId, CreateManualFinancialSnapshotRequest request);

    FinancialSnapshot importSnapshot(Long companyId, MultipartFile file, FinancialSnapshotSource source, Long userId);

    FinancialSnapshot createExternalSnapshot(Long companyId, FinancialSnapshotSource source, Long userId);

    Optional<FinancialSnapshot> findLatest(Long companyId);

    Optional<FinancialSnapshot> findLatestBySource(Long companyId, FinancialSnapshotSource source);

    List<FinancialSnapshot> findAll(Long companyId);

    Optional<FinancialSnapshot> buildConsolidatedSnapshot(Long companyId);
}

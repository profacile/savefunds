package be.profacile.savefunds.domain.repository;

import be.profacile.savefunds.domain.entity.FinancialSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialSnapshotRepository extends JpaRepository<FinancialSnapshot, Long> {
    Optional<FinancialSnapshot> findTopByCompanyIdOrderBySnapshotDateDescCreatedAtDesc(Long companyId);

    Optional<FinancialSnapshot> findTopByCompanyIdAndSourceOrderBySnapshotDateDescCreatedAtDesc(Long companyId, be.profacile.savefunds.domain.enums.FinancialSnapshotSource source);

    List<FinancialSnapshot> findAllByCompanyIdOrderBySnapshotDateDescCreatedAtDesc(Long companyId);
}

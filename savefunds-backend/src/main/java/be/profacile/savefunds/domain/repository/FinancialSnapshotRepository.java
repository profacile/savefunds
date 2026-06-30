package be.profacile.savefunds.domain.repository;

import be.profacile.savefunds.domain.entity.FinancialSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialSnapshotRepository extends JpaRepository<FinancialSnapshot, Long> {
    Optional<FinancialSnapshot> findTopByEntrepriseIdOrderBySnapshotDateDescCreatedAtDesc(Long entrepriseId);

    Optional<FinancialSnapshot> findTopByEntrepriseIdAndSourceOrderBySnapshotDateDescCreatedAtDesc(Long entrepriseId, be.profacile.savefunds.domain.enums.FinancialSnapshotSource source);

    List<FinancialSnapshot> findAllByEntrepriseIdOrderBySnapshotDateDescCreatedAtDesc(Long entrepriseId);
}

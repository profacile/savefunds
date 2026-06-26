package be.profacile.savefunds.domain.repository;

import be.profacile.savefunds.domain.entity.FinancialObligation;
import be.profacile.savefunds.domain.enums.FinancialObligationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialObligationRepository extends JpaRepository<FinancialObligation, Long> {
    Optional<FinancialObligation> findFirstByEntrepriseIdAndStatusOrderByDueDateAsc(Long entrepriseId, FinancialObligationStatus status);

    List<FinancialObligation> findByEntrepriseIdOrderByDueDateAsc(Long entrepriseId);
}

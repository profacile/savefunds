package be.profacile.savefunds.domain.repository;

import be.profacile.savefunds.domain.entity.ValidationDecision;
import be.profacile.savefunds.domain.enums.ValidationDecisionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValidationDecisionRepository extends JpaRepository<ValidationDecision, Long> {
    long countByCompanyIdAndStatus(Long companyId, ValidationDecisionStatus status);

    List<ValidationDecision> findByCompanyIdOrderByCreatedAtDesc(Long companyId);

    List<ValidationDecision> findByCompanyIdAndStatusOrderByCreatedAtDesc(Long companyId, ValidationDecisionStatus status);
}

package be.profacile.savefunds.domain.repository;

import be.profacile.savefunds.domain.entity.FinancialSituation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FinancialSituationRepository extends JpaRepository<FinancialSituation, Long> {
    List<FinancialSituation> findByCompanyIdOrderByCapturedAtDesc(Long companyId);
    FinancialSituation findFirstByCompanyIdOrderByCapturedAtDesc(Long companyId);
}
package be.profacile.savefunds.domain.repository;

import be.profacile.savefunds.domain.entity.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
    Optional<AnalysisResult> findByAnalysis_Id(Long analysisId);
}

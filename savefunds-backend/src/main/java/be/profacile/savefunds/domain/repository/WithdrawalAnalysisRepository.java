package be.profacile.savefunds.domain.repository;

import be.profacile.savefunds.domain.entity.WithdrawalAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawalAnalysisRepository extends JpaRepository<WithdrawalAnalysis, Long> {
    List<WithdrawalAnalysis> findByCompany_IdOrderByCreatedAtDesc(Long companyId);
}
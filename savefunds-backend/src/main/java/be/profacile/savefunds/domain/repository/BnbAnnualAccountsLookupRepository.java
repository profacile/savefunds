package be.profacile.savefunds.domain.repository;

import be.profacile.savefunds.domain.entity.BnbAnnualAccountsLookup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BnbAnnualAccountsLookupRepository extends JpaRepository<BnbAnnualAccountsLookup, Long> {
    Optional<BnbAnnualAccountsLookup> findTopByEntrepriseIdOrderByCreatedAtDesc(Long entrepriseId);
}

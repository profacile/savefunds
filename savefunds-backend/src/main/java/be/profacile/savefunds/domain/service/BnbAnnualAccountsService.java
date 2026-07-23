package be.profacile.savefunds.domain.service;

import be.profacile.savefunds.domain.entity.BnbAnnualAccountsLookup;
import be.profacile.savefunds.domain.entity.FinancialSnapshot;

import java.util.Optional;

public interface BnbAnnualAccountsService {
    BnbAnnualAccountsLookup search(Long companyId);

    FinancialSnapshot createSnapshotFromLatestDeposit(Long companyId);

    Optional<BnbAnnualAccountsLookup> findLatest(Long companyId);
}

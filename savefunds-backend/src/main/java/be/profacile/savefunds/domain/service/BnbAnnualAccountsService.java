package be.profacile.savefunds.domain.service;

import be.profacile.savefunds.domain.entity.BnbAnnualAccountsLookup;
import be.profacile.savefunds.domain.entity.FinancialSnapshot;

import java.util.Optional;

public interface BnbAnnualAccountsService {
    BnbAnnualAccountsLookup search(Long entrepriseId);

    FinancialSnapshot createSnapshotFromLatestDeposit(Long entrepriseId);

    Optional<BnbAnnualAccountsLookup> findLatest(Long entrepriseId);
}

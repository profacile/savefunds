package be.profacile.savefunds.domain.service.financial;

import be.profacile.savefunds.domain.entity.Company;
import be.profacile.savefunds.domain.enums.FinancialSnapshotSource;

public interface ExternalFinancialDataProvider {
    FinancialSnapshotSource source();

    String providerName();

    String providerVersion();

    ExtractedFinancialData fetch(Company company);
}

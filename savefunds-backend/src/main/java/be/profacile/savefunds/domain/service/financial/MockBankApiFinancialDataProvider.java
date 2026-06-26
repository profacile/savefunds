package be.profacile.savefunds.domain.service.financial;

import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.enums.FinancialSnapshotSource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class MockBankApiFinancialDataProvider implements ExternalFinancialDataProvider {

    @Override
    public FinancialSnapshotSource source() {
        return FinancialSnapshotSource.BANK_API;
    }

    @Override
    public String providerName() {
        return "PSD2/Open Banking mock";
    }

    @Override
    public String providerVersion() {
        return "mock-bank-api-v1";
    }

    @Override
    public ExtractedFinancialData fetch(Entreprise entreprise) {
        // Production target: use PSD2/Open Banking after explicit company consent.
        // The mock keeps the domain flow testable while legal consent and bank connectors are pending.
        return ExtractedFinancialData.builder()
                .chiffreAffairesMensuel(new BigDecimal("36500.00"))
                .chargesMensuelles(new BigDecimal("28750.00"))
                .tresorerie(new BigDecimal("18450.00"))
                .soldeCompteCourant(new BigDecimal("-4800.00"))
                .dettesCourtTerme(new BigDecimal("9100.00"))
                .creancesClients(new BigDecimal("15800.00"))
                .dureeCompteCourantDebiteur(34)
                .snapshotDate(LocalDate.now())
                .confidenceScore(85)
                .warnings(List.of(
                        "Donnees mockees: remplacer par un connecteur bancaire PSD2 en production",
                        "Le consentement explicite du dirigeant sera obligatoire avant toute connexion bancaire"
                ))
                .missingFields(List.of())
                .rawMetadata("provider=BANK_API;mode=mock;enterpriseId=" + entreprise.getId())
                .build();
    }
}

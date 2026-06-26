package be.profacile.savefunds.domain.service.financial;

import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.enums.FinancialSnapshotSource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class MockBnbFinancialDataProvider implements ExternalFinancialDataProvider {

    @Override
    public FinancialSnapshotSource source() {
        return FinancialSnapshotSource.BNB_API;
    }

    @Override
    public String providerName() {
        return "BNB annual accounts mock";
    }

    @Override
    public String providerVersion() {
        return "mock-bnb-v1";
    }

    @Override
    public ExtractedFinancialData fetch(Entreprise entreprise) {
        // Production target: call the official BNB/NBB annual accounts source by enterprise number.
        // MVP target: demonstrate the integration boundary without depending on external credentials.
        return ExtractedFinancialData.builder()
                .chiffreAffairesMensuel(new BigDecimal("42000.00"))
                .chargesMensuelles(new BigDecimal("31000.00"))
                .tresorerie(new BigDecimal("26000.00"))
                .soldeCompteCourant(new BigDecimal("-2500.00"))
                .dettesCourtTerme(new BigDecimal("18500.00"))
                .creancesClients(new BigDecimal("22000.00"))
                .dureeCompteCourantDebiteur(18)
                .snapshotDate(LocalDate.now())
                .confidenceScore(70)
                .warnings(List.of(
                        "Donnees mockees: remplacer par un connecteur BNB officiel en production",
                        "Les comptes annuels BNB peuvent etre moins recents que la tresorerie bancaire"
                ))
                .missingFields(List.of())
                .rawMetadata("provider=BNB_API;mode=mock;enterpriseNumber=" + entreprise.getNumeroEntreprise())
                .build();
    }
}

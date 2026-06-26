package be.profacile.savefunds.domain.service.financial;

import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.enums.FinancialSnapshotSource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class MockBalanceSheetDocumentProvider implements ExternalFinancialDataProvider {

    @Override
    public FinancialSnapshotSource source() {
        return FinancialSnapshotSource.BALANCE_SHEET_DOCUMENT;
    }

    @Override
    public String providerName() {
        return "Balance sheet parser mock";
    }

    @Override
    public String providerVersion() {
        return "mock-balance-sheet-v1";
    }

    @Override
    public ExtractedFinancialData fetch(Entreprise entreprise) {
        // Production target: parse a structured balance sheet PDF/XLSX and map accounting codes to SaveFunds fields.
        // This mock documents the expected output of that parser.
        return ExtractedFinancialData.builder()
                .chiffreAffairesMensuel(new BigDecimal("39000.00"))
                .chargesMensuelles(new BigDecimal("33500.00"))
                .tresorerie(new BigDecimal("12200.00"))
                .soldeCompteCourant(new BigDecimal("-7600.00"))
                .dettesCourtTerme(new BigDecimal("24000.00"))
                .creancesClients(new BigDecimal("31000.00"))
                .dureeCompteCourantDebiteur(47)
                .snapshotDate(LocalDate.now())
                .confidenceScore(65)
                .warnings(List.of(
                        "Donnees mockees: remplacer par un parser PDF/XLSX de bilan",
                        "Le bilan doit etre relu si le parser detecte des champs manquants ou ambigus"
                ))
                .missingFields(List.of())
                .rawMetadata("provider=BALANCE_SHEET_DOCUMENT;mode=mock;enterpriseId=" + entreprise.getId())
                .build();
    }
}

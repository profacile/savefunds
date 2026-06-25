package be.profacile.savefunds.service.financial;

import be.profacile.savefunds.domain.service.financial.AccountingCsvFinancialDataExtractor;
import be.profacile.savefunds.domain.service.financial.BelgianAccountingMapper;
import be.profacile.savefunds.domain.service.financial.ExtractedFinancialData;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class AccountingCsvFinancialDataExtractorTest {

    private final AccountingCsvFinancialDataExtractor extractor = new AccountingCsvFinancialDataExtractor(new BelgianAccountingMapper());

    @Test
    void extractsNormalizedAccountingCsv() {
        String csv = """
                accountCode,label,amount
                70,Ventes,120000
                61,Services et biens divers,-30000
                62,Remunerations,-40000
                550,Banque,15000
                416,Compte courant dirigeant,-3000
                """;
        MockMultipartFile file = new MockMultipartFile("file", "accounting.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        ExtractedFinancialData data = extractor.extract(file);

        assertThat(data.getChiffreAffairesMensuel()).isEqualByComparingTo("120000");
        assertThat(data.getChargesMensuelles()).isEqualByComparingTo("70000");
        assertThat(data.getTresorerie()).isEqualByComparingTo("15000");
        assertThat(data.getSoldeCompteCourant()).isEqualByComparingTo("-3000");
        assertThat(data.getDureeCompteCourantDebiteur()).isEqualTo(31);
    }
}

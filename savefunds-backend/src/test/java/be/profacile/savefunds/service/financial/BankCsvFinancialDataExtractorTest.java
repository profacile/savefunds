package be.profacile.savefunds.service.financial;

import be.profacile.savefunds.domain.service.financial.BankCsvFinancialDataExtractor;
import be.profacile.savefunds.domain.service.financial.ExtractedFinancialData;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class BankCsvFinancialDataExtractorTest {

    private final BankCsvFinancialDataExtractor extractor = new BankCsvFinancialDataExtractor();

    @Test
    void extractsBankCsvTotalsAndFinalBalance() {
        String csv = """
                date,description,amount,balance
                2026-06-01,Client facture 001,5000,12000
                2026-06-05,Loyer,-1200,10800
                2026-06-10,TVA,-2500,8300
                """;
        MockMultipartFile file = new MockMultipartFile("file", "bank.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        ExtractedFinancialData data = extractor.extract(file);

        assertThat(data.getChiffreAffairesMensuel()).isEqualByComparingTo("5000");
        assertThat(data.getChargesMensuelles()).isEqualByComparingTo("3700");
        assertThat(data.getTresorerie()).isEqualByComparingTo("8300");
        assertThat(data.getConfidenceScore()).isEqualTo(80);
    }
}

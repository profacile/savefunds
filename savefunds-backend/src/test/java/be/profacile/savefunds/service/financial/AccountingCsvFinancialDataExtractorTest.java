package be.profacile.savefunds.service.financial;

import be.profacile.savefunds.domain.service.financial.AccountingCsvFinancialDataExtractor;
import be.profacile.savefunds.domain.service.financial.BelgianAccountingMapper;
import be.profacile.savefunds.domain.service.financial.ExtractedFinancialData;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    @Test
    void acceptsNonCsvAccountingDocumentAsPendingReview() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "Situation provisoire au 31-12-2025.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "fake pdf content".getBytes(StandardCharsets.UTF_8)
        );

        ExtractedFinancialData data = extractor.extract(file);

        assertThat(data.getConfidenceScore()).isEqualTo(20);
        assertThat(data.getWarnings()).anySatisfy(warning ->
                assertThat(warning).contains("Document bilan provisoire recu"));
        assertThat(data.getMissingFields()).contains(
                "chiffreAffairesMensuel",
                "chargesMensuelles",
                "tresorerie",
                "soldeCompteCourant"
        );
        assertThat(data.getRawMetadata()).contains("parserStatus=AWAITING_REVIEW");
    }

    @Test
    void extractsHorusLikeProvisionalBalanceSheetPdf() throws Exception {
        String text = """
                PROFACILE MARTINS ACCOUNTING
                Bilan et comptes de résultats du 10/2025 au 12/2025
                Créances commerciales 40 12 632,40
                416101 Compte courant CR Administrateurs/Actionnaires 22 349,89
                Valeurs disponibles 54/58 480,75
                Dettes à un an au plus 42/48 12 937,88
                Chiffre d'affaires 70 33 060,00
                Approvisionnements, marchandises, services et biens divers 60/61 5 961,22
                Charges financières 65/66B 5,37
                Impôts sur le résultat (+)/(-) 67/77 5 722,61
                """;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "Situation provisoire au 31-12-2025.pdf",
                "application/pdf",
                pdfBytes(text)
        );

        ExtractedFinancialData data = extractor.extract(file);

        assertThat(data.getChiffreAffairesMensuel()).isEqualByComparingTo("11020.00");
        assertThat(data.getChargesMensuelles()).isEqualByComparingTo("3896.40");
        assertThat(data.getTresorerie()).isEqualByComparingTo("480.75");
        assertThat(data.getSoldeCompteCourant()).isEqualByComparingTo("-22349.89");
        assertThat(data.getDureeCompteCourantDebiteur()).isEqualTo(31);
        assertThat(data.getDettesCourtTerme()).isEqualByComparingTo("12937.88");
        assertThat(data.getCreancesClients()).isEqualByComparingTo("12632.40");
        assertThat(data.getMissingFields()).isEmpty();
    }

    private byte[] pdfBytes(String text) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                content.setLeading(14);
                content.newLineAtOffset(40, 740);
                for (String line : text.split("\\R")) {
                    content.showText(line);
                    content.newLine();
                }
                content.endText();
            }
            document.save(output);
            return output.toByteArray();
        }
    }
}

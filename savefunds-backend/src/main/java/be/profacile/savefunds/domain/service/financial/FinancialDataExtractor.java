package be.profacile.savefunds.domain.service.financial;

import be.profacile.savefunds.domain.enums.FinancialSnapshotSource;
import org.springframework.web.multipart.MultipartFile;

public interface FinancialDataExtractor {
    FinancialSnapshotSource source();

    String parserVersion();

    ExtractedFinancialData extract(MultipartFile file);
}

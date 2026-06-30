package be.profacile.savefunds.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyRegistryImportResponse {
    private int importedRows;
    private int skippedRows;
    private String source;
}

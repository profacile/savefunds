package be.profacile.savefunds.api.dto.response;

import be.profacile.savefunds.domain.enums.FinancialSnapshotSource;
import be.profacile.savefunds.domain.enums.ImportJobStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ImportJobResponse {
    private Long id;
    private Long entrepriseId;
    private Long snapshotId;
    private FinancialSnapshotSource source;
    private ImportJobStatus status;
    private String fileName;
    private String parserVersion;
    private String summary;
    private String errorMessage;
    private LocalDateTime createdAt;
    private FinancialSnapshotResponse snapshot;
}

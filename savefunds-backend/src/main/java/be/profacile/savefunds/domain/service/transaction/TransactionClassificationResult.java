package be.profacile.savefunds.domain.service.transaction;

import be.profacile.savefunds.domain.enums.TransactionClassificationType;
import be.profacile.savefunds.domain.enums.TransactionReviewStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionClassificationResult {
    private TransactionClassificationType classification;
    private TransactionReviewStatus reviewStatus;
    private Integer confidenceScore;
    private boolean impactsDirectorCurrentAccount;
    private String reason;
}

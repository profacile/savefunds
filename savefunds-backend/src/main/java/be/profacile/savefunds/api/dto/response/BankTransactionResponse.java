package be.profacile.savefunds.api.dto.response;

import be.profacile.savefunds.domain.enums.TransactionClassificationType;
import be.profacile.savefunds.domain.enums.TransactionReviewStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class BankTransactionResponse {
    private Long id;
    private Long entrepriseId;
    private Long financialSnapshotId;
    private LocalDate transactionDate;
    private String description;
    private BigDecimal amount;
    private BigDecimal balance;
    private TransactionClassificationType classification;
    private TransactionReviewStatus reviewStatus;
    private Integer confidenceScore;
    private boolean impactsDirectorCurrentAccount;
    private BigDecimal directorCurrentAccountImpact;
    private String aiReason;
    private LocalDateTime createdAt;
}

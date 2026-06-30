package be.profacile.savefunds.api.mapper;

import be.profacile.savefunds.api.dto.response.BankTransactionResponse;
import be.profacile.savefunds.domain.entity.BankTransaction;
import org.springframework.stereotype.Component;

@Component
public class BankTransactionApiMapper {

    public BankTransactionResponse toResponse(BankTransaction transaction) {
        return BankTransactionResponse.builder()
                .id(transaction.getId())
                .entrepriseId(transaction.getEntreprise().getId())
                .financialSnapshotId(transaction.getFinancialSnapshot() == null ? null : transaction.getFinancialSnapshot().getId())
                .transactionDate(transaction.getTransactionDate())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .balance(transaction.getBalance())
                .classification(transaction.getClassification())
                .reviewStatus(transaction.getReviewStatus())
                .confidenceScore(transaction.getConfidenceScore())
                .impactsDirectorCurrentAccount(transaction.isImpactsDirectorCurrentAccount())
                .directorCurrentAccountImpact(transaction.getDirectorCurrentAccountImpact())
                .aiReason(transaction.getAiReason())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}

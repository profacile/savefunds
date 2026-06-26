package be.profacile.savefunds.api.dto.response;

import be.profacile.savefunds.domain.enums.FinancialDecisionType;
import be.profacile.savefunds.domain.enums.ValidationDecisionStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ValidationDecisionResponse {
    private Long id;
    private Long entrepriseId;
    private FinancialDecisionType decisionType;
    private BigDecimal requestedAmount;
    private ValidationDecisionStatus status;
    private String conditionText;
    private String comment;
    private Long requestedByUserId;
    private Long decidedByAccountantId;
    private LocalDateTime decidedAt;
    private LocalDateTime createdAt;
}

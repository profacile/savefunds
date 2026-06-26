package be.profacile.savefunds.api.dto.request;

import be.profacile.savefunds.domain.enums.FinancialDecisionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateValidationDecisionRequest {
    @NotNull
    private FinancialDecisionType decisionType;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal requestedAmount;

    private String comment;
}

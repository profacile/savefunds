package be.profacile.savefunds.api.dto.request;

import be.profacile.savefunds.domain.enums.ValidationDecisionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DecideValidationRequest {
    @NotNull
    private ValidationDecisionStatus status;

    private String conditionText;
    private String comment;
}

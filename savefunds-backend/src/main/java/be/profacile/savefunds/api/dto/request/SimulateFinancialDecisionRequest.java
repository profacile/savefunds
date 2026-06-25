package be.profacile.savefunds.api.dto.request;

import be.profacile.savefunds.domain.enums.FinancialDecisionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SimulateFinancialDecisionRequest {

    @NotNull
    private FinancialDecisionType type;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    private LocalDate plannedDate;
}

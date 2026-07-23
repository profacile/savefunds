package be.profacile.savefunds.api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateManualFinancialSnapshotRequest {

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal monthlyRevenue;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal monthlyExpenses;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal cashBalance;

    private BigDecimal directorCurrentAccountBalance;
    private BigDecimal shortTermDebt;
    private BigDecimal customerReceivables;
    private Integer directorCurrentAccountDebtorDays;
    private LocalDate snapshotDate;
}

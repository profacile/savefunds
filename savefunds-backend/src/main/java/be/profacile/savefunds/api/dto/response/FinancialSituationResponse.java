package be.profacile.savefunds.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialSituationResponse {

    private Long id;
    private Long companyId;

    private BigDecimal monthlyRevenue;
    private BigDecimal monthlyExpenses;
    private BigDecimal cashBalance;
    private BigDecimal directorCurrentAccountBalance;

    private BigDecimal ratioCACharges;
    private BigDecimal cashCoverageMonths;
    private Integer directorCurrentAccountDebtorDays;

    private LocalDateTime capturedAt;
    private String source;
    private String notes;
}
package be.profacile.savefunds.api.dto.response;

import be.profacile.savefunds.domain.enums.FinancialSnapshotSource;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class FinancialSnapshotResponse {
    private Long id;
    private Long companyId;
    private FinancialSnapshotSource source;
    private String sourceReference;
    private BigDecimal monthlyRevenue;
    private BigDecimal monthlyExpenses;
    private BigDecimal cashBalance;
    private BigDecimal directorCurrentAccountBalance;
    private BigDecimal shortTermDebt;
    private BigDecimal customerReceivables;
    private Integer directorCurrentAccountDebtorDays;
    private LocalDate snapshotDate;
    private Integer confidenceScore;
    private List<String> warnings;
    private List<String> missingFields;
    private String rawMetadata;
    private LocalDateTime createdAt;
}

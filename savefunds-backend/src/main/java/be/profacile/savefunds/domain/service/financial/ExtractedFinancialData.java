package be.profacile.savefunds.domain.service.financial;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ExtractedFinancialData {
    private BigDecimal monthlyRevenue;
    private BigDecimal monthlyExpenses;
    private BigDecimal cashBalance;
    private BigDecimal directorCurrentAccountBalance;
    private BigDecimal shortTermDebt;
    private BigDecimal customerReceivables;
    private Integer directorCurrentAccountDebtorDays;
    private LocalDate snapshotDate;
    private Integer confidenceScore;

    @Builder.Default
    private List<String> warnings = new ArrayList<>();

    @Builder.Default
    private List<String> missingFields = new ArrayList<>();

    private String rawMetadata;
}

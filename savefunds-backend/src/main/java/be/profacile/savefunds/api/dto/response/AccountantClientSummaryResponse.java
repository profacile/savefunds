package be.profacile.savefunds.api.dto.response;

import be.profacile.savefunds.domain.enums.Decision;
import be.profacile.savefunds.domain.enums.FinancialObligationType;
import be.profacile.savefunds.domain.enums.TreasuryTrend;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AccountantClientSummaryResponse {
    private Long entrepriseId;
    private String companyName;
    private String companyNumber;
    private Decision status;
    private String dossierStatus;
    private BigDecimal riskScore;
    private BigDecimal cash;
    private BigDecimal coverageMonths;
    private Integer currentAccountDebtorDays;
    private TreasuryTrend trend;
    private Integer dataAgeDays;
    private FinancialObligationType nextObligationType;
    private LocalDate nextObligationDate;
    private Long pendingValidationCount;
    private String pendingValidationLabel;
    private String lastSource;
    private LocalDateTime lastUpdate;
    private String internalNote;
    private List<String> activity;
}

package be.profacile.savefunds.api.dto.response;

import be.profacile.savefunds.domain.enums.Decision;
import be.profacile.savefunds.domain.enums.FinancialDecisionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class VigilanceResultResponse {
    private Long snapshotId;
    private FinancialDecisionType decisionType;
    private BigDecimal requestedAmount;
    private BigDecimal cashBefore;
    private BigDecimal cashAfter;
    private BigDecimal maxRecommendedAmount;
    private BigDecimal coverageMonthsAfterDecision;
    private Decision globalDecision;
    private String globalExplanation;
    private List<String> recommendations;
    private List<VigilanceIndicatorResponse> indicators;
}

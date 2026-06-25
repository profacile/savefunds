package be.profacile.savefunds.service;

import be.profacile.savefunds.api.dto.request.SimulateFinancialDecisionRequest;
import be.profacile.savefunds.api.dto.response.VigilanceResultResponse;
import be.profacile.savefunds.domain.entity.FinancialSnapshot;
import be.profacile.savefunds.domain.enums.Decision;
import be.profacile.savefunds.domain.enums.FinancialDecisionType;
import be.profacile.savefunds.domain.service.impl.VigilanceEngineImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class VigilanceEngineTest {

    private final VigilanceEngineImpl engine = new VigilanceEngineImpl();

    @Test
    void returnsRedWhenRequestedAmountBreaksCashReserve() {
        FinancialSnapshot snapshot = new FinancialSnapshot();
        snapshot.setId(1L);
        snapshot.setTresorerie(BigDecimal.valueOf(7500));
        snapshot.setChargesMensuelles(BigDecimal.valueOf(2000));
        snapshot.setChiffreAffairesMensuel(BigDecimal.valueOf(10000));
        snapshot.setSoldeCompteCourant(BigDecimal.ZERO);
        snapshot.setDureeCompteCourantDebiteur(0);

        SimulateFinancialDecisionRequest request = new SimulateFinancialDecisionRequest();
        request.setType(FinancialDecisionType.RETRAIT_DIRIGEANT);
        request.setAmount(BigDecimal.valueOf(2000));

        VigilanceResultResponse result = engine.simulate(snapshot, request);

        assertThat(result.getMaxRecommendedAmount()).isEqualByComparingTo("1500");
        assertThat(result.getGlobalDecision()).isEqualTo(Decision.ROUGE);
        assertThat(result.getIndicators()).anyMatch(indicator -> "REQUESTED_AMOUNT".equals(indicator.getCode())
                && indicator.getDecision() == Decision.ROUGE);
    }
}

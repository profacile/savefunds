package be.profacile.savefunds.domain.service;

import be.profacile.savefunds.api.dto.request.SimulateFinancialDecisionRequest;
import be.profacile.savefunds.api.dto.response.VigilanceResultResponse;
import be.profacile.savefunds.domain.entity.FinancialSnapshot;

public interface VigilanceEngine {
    VigilanceResultResponse simulate(FinancialSnapshot snapshot, SimulateFinancialDecisionRequest request);
}

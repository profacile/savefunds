package be.profacile.savefunds.domain.service;

import be.profacile.savefunds.api.dto.request.CreateAccountantNoteRequest;
import be.profacile.savefunds.api.dto.request.CreateValidationDecisionRequest;
import be.profacile.savefunds.api.dto.request.DecideValidationRequest;
import be.profacile.savefunds.api.dto.response.AccountantDashboardResponse;
import be.profacile.savefunds.api.dto.response.AccountantNoteResponse;
import be.profacile.savefunds.api.dto.response.ValidationDecisionResponse;
import be.profacile.savefunds.domain.entity.User;

public interface AccountantDashboardService {
    AccountantDashboardResponse dashboard(User accountant);

    AccountantNoteResponse addNote(User accountant, Long entrepriseId, CreateAccountantNoteRequest request);

    ValidationDecisionResponse createValidationRequest(User requester, Long entrepriseId, CreateValidationDecisionRequest request);

    ValidationDecisionResponse decide(User accountant, Long validationId, DecideValidationRequest request);
}

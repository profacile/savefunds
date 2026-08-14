package be.profacile.savefunds.domain.service;

import be.profacile.savefunds.api.dto.request.CreateAccountantNoteRequest;
import be.profacile.savefunds.api.dto.request.CreateValidationDecisionRequest;
import be.profacile.savefunds.api.dto.request.DecideAccountantClientAccessRequest;
import be.profacile.savefunds.api.dto.request.DecideValidationRequest;
import be.profacile.savefunds.api.dto.request.RequestAccountantClientAccessRequest;
import be.profacile.savefunds.api.dto.response.AccountantClientAccessResponse;
import be.profacile.savefunds.api.dto.response.AccountantDashboardResponse;
import be.profacile.savefunds.api.dto.response.AccountantNoteResponse;
import be.profacile.savefunds.api.dto.response.ValidationDecisionResponse;
import be.profacile.savefunds.domain.entity.User;

import java.util.List;

public interface AccountantDashboardService {
    AccountantDashboardResponse dashboard(User accountant);

    AccountantClientAccessResponse requestClientAccess(User accountant, RequestAccountantClientAccessRequest request);

    List<AccountantClientAccessResponse> myClientAccessRequests(User user);

    AccountantClientAccessResponse decideClientAccess(User director, Long accessId, DecideAccountantClientAccessRequest request);

    AccountantNoteResponse addNote(User accountant, Long companyId, CreateAccountantNoteRequest request);

    ValidationDecisionResponse createValidationRequest(User requester, Long companyId, CreateValidationDecisionRequest request);

    ValidationDecisionResponse decide(User accountant, Long validationId, DecideValidationRequest request);
}

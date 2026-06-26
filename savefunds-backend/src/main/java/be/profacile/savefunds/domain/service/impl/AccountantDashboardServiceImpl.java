package be.profacile.savefunds.domain.service.impl;

import be.profacile.savefunds.api.dto.request.CreateAccountantNoteRequest;
import be.profacile.savefunds.api.dto.request.CreateValidationDecisionRequest;
import be.profacile.savefunds.api.dto.request.DecideValidationRequest;
import be.profacile.savefunds.api.dto.response.AccountantClientSummaryResponse;
import be.profacile.savefunds.api.dto.response.AccountantDashboardResponse;
import be.profacile.savefunds.api.dto.response.AccountantNoteResponse;
import be.profacile.savefunds.api.dto.response.ValidationDecisionResponse;
import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.domain.entity.*;
import be.profacile.savefunds.domain.enums.*;
import be.profacile.savefunds.domain.repository.*;
import be.profacile.savefunds.domain.service.AccountantDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountantDashboardServiceImpl implements AccountantDashboardService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal ONE = BigDecimal.ONE;
    private static final BigDecimal TWO = BigDecimal.valueOf(2);
    private static final BigDecimal THREE = BigDecimal.valueOf(3);
    private static final BigDecimal TEN = BigDecimal.TEN;

    private final EntrepriseRepository entrepriseRepository;
    private final FinancialSnapshotRepository financialSnapshotRepository;
    private final FinancialObligationRepository financialObligationRepository;
    private final AccountantNoteRepository accountantNoteRepository;
    private final ValidationDecisionRepository validationDecisionRepository;

    @Override
    public AccountantDashboardResponse dashboard(User accountant) {
        assertAccountant(accountant);

        List<AccountantClientSummaryResponse> clients = entrepriseRepository.findAll().stream()
                .map(this::toClientSummary)
                .sorted(Comparator.comparing(AccountantClientSummaryResponse::getRiskScore).reversed())
                .toList();

        return AccountantDashboardResponse.builder()
                .totalClients(clients.size())
                .greenClients((int) clients.stream().filter(client -> client.getStatus() == Decision.VERT).count())
                .orangeClients((int) clients.stream().filter(client -> client.getStatus() == Decision.ORANGE).count())
                .redClients((int) clients.stream().filter(client -> client.getStatus() == Decision.ROUGE).count())
                .clients(clients)
                .build();
    }

    @Override
    public AccountantNoteResponse addNote(User accountant, Long entrepriseId, CreateAccountantNoteRequest request) {
        assertAccountant(accountant);
        Entreprise entreprise = findEntreprise(entrepriseId);

        AccountantNote note = new AccountantNote();
        note.setEntreprise(entreprise);
        note.setAccountantId(accountant.getId());
        note.setContent(request.getContent());

        return toNoteResponse(accountantNoteRepository.save(note));
    }

    @Override
    public ValidationDecisionResponse createValidationRequest(User requester, Long entrepriseId, CreateValidationDecisionRequest request) {
        Entreprise entreprise = findEntreprise(entrepriseId);
        if (!requester.getId().equals(entreprise.getUserId()) && requester.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Seul le dirigeant de l'entreprise peut creer une demande de validation");
        }

        ValidationDecision validation = new ValidationDecision();
        validation.setEntreprise(entreprise);
        validation.setRequestedByUserId(requester.getId());
        validation.setDecisionType(request.getDecisionType());
        validation.setRequestedAmount(request.getRequestedAmount());
        validation.setComment(request.getComment());
        validation.setStatus(ValidationDecisionStatus.PENDING);

        return toValidationResponse(validationDecisionRepository.save(validation));
    }

    @Override
    public ValidationDecisionResponse decide(User accountant, Long validationId, DecideValidationRequest request) {
        assertAccountant(accountant);
        if (request.getStatus() == ValidationDecisionStatus.PENDING) {
            throw new IllegalArgumentException("La decision finale ne peut pas rester PENDING");
        }

        ValidationDecision validation = validationDecisionRepository.findById(validationId)
                .orElseThrow(() -> new ResourceNotFoundException("Validation introuvable: " + validationId));
        validation.setStatus(request.getStatus());
        validation.setConditionText(request.getConditionText());
        validation.setComment(request.getComment());
        validation.setDecidedByAccountantId(accountant.getId());
        validation.setDecidedAt(LocalDateTime.now());

        return toValidationResponse(validationDecisionRepository.save(validation));
    }

    private AccountantClientSummaryResponse toClientSummary(Entreprise entreprise) {
        Optional<FinancialSnapshot> latestSnapshot = financialSnapshotRepository
                .findTopByEntrepriseIdOrderBySnapshotDateDescCreatedAtDesc(entreprise.getId());
        Optional<AccountantNote> latestNote = accountantNoteRepository.findFirstByEntrepriseIdOrderByUpdatedAtDescCreatedAtDesc(entreprise.getId());
        FinancialObligationView obligation = nextObligation(entreprise);

        BigDecimal cash = latestSnapshot.map(FinancialSnapshot::getTresorerie).orElse(entreprise.getTresorerie());
        BigDecimal expenses = latestSnapshot.map(FinancialSnapshot::getChargesMensuelles).orElse(entreprise.getChargesMensuelles());
        BigDecimal coverage = divide(cash, expenses);
        Integer debtorDays = latestSnapshot.map(FinancialSnapshot::getDureeCompteCourantDebiteur).orElse(debtorDaysFromEntreprise(entreprise));
        int dataAge = latestSnapshot.map(this::dataAgeDays).orElse(999);
        long pendingCount = validationDecisionRepository.countByEntrepriseIdAndStatus(entreprise.getId(), ValidationDecisionStatus.PENDING);
        String pendingLabel = validationDecisionRepository
                .findByEntrepriseIdAndStatusOrderByCreatedAtDesc(entreprise.getId(), ValidationDecisionStatus.PENDING)
                .stream()
                .findFirst()
                .map(validation -> validation.getDecisionType() + " " + validation.getRequestedAmount() + " EUR")
                .orElse("Aucune");
        BigDecimal riskScore = riskScore(coverage, debtorDays, dataAge, pendingCount);
        Decision status = statusFromRisk(riskScore);

        return AccountantClientSummaryResponse.builder()
                .entrepriseId(entreprise.getId())
                .companyName(entreprise.getRaisonSociale())
                .companyNumber(entreprise.getNumeroEntreprise())
                .status(status)
                .dossierStatus(dossierStatus(status, dataAge, pendingCount))
                .riskScore(riskScore)
                .cash(nullToZero(cash))
                .coverageMonths(coverage)
                .currentAccountDebtorDays(debtorDays)
                .trend(trend(entreprise, latestSnapshot.orElse(null)))
                .dataAgeDays(dataAge)
                .nextObligationType(obligation.type())
                .nextObligationDate(obligation.dueDate())
                .pendingValidationCount(pendingCount)
                .pendingValidationLabel(pendingLabel)
                .lastSource(latestSnapshot.map(snapshot -> snapshot.getSource().name()).orElse("Aucune source"))
                .lastUpdate(latestSnapshot.map(FinancialSnapshot::getCreatedAt).orElse(entreprise.getUpdatedAt()))
                .internalNote(latestNote.map(AccountantNote::getContent).orElse("Aucune note interne"))
                .activity(activity(latestSnapshot.orElse(null), latestNote.orElse(null), pendingCount))
                .build();
    }

    private FinancialObligationView nextObligation(Entreprise entreprise) {
        return financialObligationRepository
                .findFirstByEntrepriseIdAndStatusOrderByDueDateAsc(entreprise.getId(), FinancialObligationStatus.OPEN)
                .map(obligation -> new FinancialObligationView(obligation.getType(), obligation.getDueDate()))
                .orElse(new FinancialObligationView(FinancialObligationType.TVA, nextDefaultVatDate()));
    }

    private LocalDate nextDefaultVatDate() {
        LocalDate now = LocalDate.now();
        LocalDate candidate = LocalDate.of(now.getYear(), ((now.getMonthValue() - 1) / 3 + 1) * 3, 20).plusMonths(1);
        if (!candidate.isAfter(now)) {
            candidate = candidate.plusMonths(3);
        }
        return candidate;
    }

    private BigDecimal riskScore(BigDecimal coverage, Integer debtorDays, int dataAge, long pendingCount) {
        BigDecimal score = ZERO;
        if (coverage.compareTo(ONE) < 0) {
            score = score.add(BigDecimal.valueOf(3.5));
        } else if (coverage.compareTo(TWO) < 0) {
            score = score.add(BigDecimal.valueOf(2));
        } else if (coverage.compareTo(THREE) < 0) {
            score = score.add(ONE);
        }

        int safeDebtorDays = debtorDays == null ? 0 : debtorDays;
        if (safeDebtorDays > 30) {
            score = score.add(BigDecimal.valueOf(3));
        } else if (safeDebtorDays >= 21) {
            score = score.add(BigDecimal.valueOf(1.5));
        }

        if (dataAge > 30) {
            score = score.add(BigDecimal.valueOf(1.5));
        } else if (dataAge > 14) {
            score = score.add(BigDecimal.valueOf(0.75));
        }

        if (pendingCount > 0) {
            score = score.add(BigDecimal.valueOf(Math.min(2, pendingCount)));
        }

        return score.min(TEN).setScale(1, RoundingMode.HALF_UP);
    }

    private Decision statusFromRisk(BigDecimal riskScore) {
        if (riskScore.compareTo(BigDecimal.valueOf(7)) >= 0) {
            return Decision.ROUGE;
        }
        if (riskScore.compareTo(BigDecimal.valueOf(4)) >= 0) {
            return Decision.ORANGE;
        }
        return Decision.VERT;
    }

    private String dossierStatus(Decision status, int dataAge, long pendingCount) {
        if (dataAge > 30) {
            return "Donnees a rafraichir";
        }
        if (pendingCount > 0) {
            return "Validation comptable en attente";
        }
        if (status == Decision.ROUGE) {
            return "Action comptable requise";
        }
        if (status == Decision.ORANGE) {
            return "Surveillance recommandee";
        }
        return "A jour";
    }

    private TreasuryTrend trend(Entreprise entreprise, FinancialSnapshot latestSnapshot) {
        if (latestSnapshot == null || entreprise.getTresorerie() == null || latestSnapshot.getTresorerie() == null) {
            return TreasuryTrend.STABLE;
        }
        int comparison = latestSnapshot.getTresorerie().compareTo(entreprise.getTresorerie());
        if (comparison > 0) {
            return TreasuryTrend.UP;
        }
        if (comparison < 0) {
            return TreasuryTrend.DOWN;
        }
        return TreasuryTrend.STABLE;
    }

    private Integer debtorDaysFromEntreprise(Entreprise entreprise) {
        if (entreprise.getSoldeCompteCourant() == null || entreprise.getSoldeCompteCourant().signum() >= 0 || entreprise.getDateDebutDebiteurCC() == null) {
            return 0;
        }
        return Math.toIntExact(ChronoUnit.DAYS.between(entreprise.getDateDebutDebiteurCC(), LocalDate.now()));
    }

    private int dataAgeDays(FinancialSnapshot snapshot) {
        return Math.toIntExact(ChronoUnit.DAYS.between(snapshot.getCreatedAt().toLocalDate(), LocalDate.now()));
    }

    private List<String> activity(FinancialSnapshot snapshot, AccountantNote note, long pendingCount) {
        String sourceLine = snapshot == null ? "Aucun snapshot financier importe" : "Snapshot " + snapshot.getSource() + " importe le " + snapshot.getSnapshotDate();
        String noteLine = note == null ? "Aucune note comptable interne" : "Note comptable mise a jour";
        String validationLine = pendingCount == 0 ? "Aucune validation en attente" : pendingCount + " validation(s) en attente";
        return List.of(sourceLine, noteLine, validationLine);
    }

    private AccountantNoteResponse toNoteResponse(AccountantNote note) {
        return AccountantNoteResponse.builder()
                .id(note.getId())
                .entrepriseId(note.getEntreprise().getId())
                .accountantId(note.getAccountantId())
                .content(note.getContent())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }

    private ValidationDecisionResponse toValidationResponse(ValidationDecision validation) {
        return ValidationDecisionResponse.builder()
                .id(validation.getId())
                .entrepriseId(validation.getEntreprise().getId())
                .decisionType(validation.getDecisionType())
                .requestedAmount(validation.getRequestedAmount())
                .status(validation.getStatus())
                .conditionText(validation.getConditionText())
                .comment(validation.getComment())
                .requestedByUserId(validation.getRequestedByUserId())
                .decidedByAccountantId(validation.getDecidedByAccountantId())
                .decidedAt(validation.getDecidedAt())
                .createdAt(validation.getCreatedAt())
                .build();
    }

    private Entreprise findEntreprise(Long entrepriseId) {
        return entrepriseRepository.findById(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise introuvable: " + entrepriseId));
    }

    private void assertAccountant(User user) {
        if (user.getRole() != Role.COMPTABLE && user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Acces reserve aux comptables");
        }
    }

    private BigDecimal divide(BigDecimal left, BigDecimal right) {
        if (left == null || right == null || right.compareTo(ZERO) == 0) {
            return ZERO;
        }
        return left.divide(right, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal nullToZero(BigDecimal value) {
        return value == null ? ZERO : value;
    }

    private record FinancialObligationView(FinancialObligationType type, LocalDate dueDate) {
    }
}

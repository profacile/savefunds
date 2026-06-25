package be.profacile.savefunds.domain.service.impl;

import be.profacile.savefunds.api.dto.request.SimulateFinancialDecisionRequest;
import be.profacile.savefunds.api.dto.response.VigilanceIndicatorResponse;
import be.profacile.savefunds.api.dto.response.VigilanceResultResponse;
import be.profacile.savefunds.domain.entity.FinancialSnapshot;
import be.profacile.savefunds.domain.enums.Decision;
import be.profacile.savefunds.domain.service.VigilanceEngine;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class VigilanceEngineImpl implements VigilanceEngine {

    private static final BigDecimal THREE = BigDecimal.valueOf(3);
    private static final BigDecimal RATIO_GREEN = BigDecimal.valueOf(1.3);
    private static final BigDecimal ONE = BigDecimal.ONE;
    private static final BigDecimal SEVENTY_FIVE_PERCENT = BigDecimal.valueOf(0.75);

    @Override
    public VigilanceResultResponse simulate(FinancialSnapshot snapshot, SimulateFinancialDecisionRequest request) {
        BigDecimal cashBefore = positive(snapshot.getTresorerie());
        BigDecimal expenses = positive(snapshot.getChargesMensuelles());
        BigDecimal revenue = positive(snapshot.getChiffreAffairesMensuel());
        BigDecimal requestedAmount = positive(request.getAmount());
        BigDecimal cashAfter = cashBefore.subtract(requestedAmount);
        BigDecimal maxRecommendedAmount = cashBefore.subtract(expenses.multiply(THREE)).max(BigDecimal.ZERO);

        List<VigilanceIndicatorResponse> indicators = new ArrayList<>();
        indicators.add(cashCoverageIndicator(expenses, cashAfter));
        indicators.add(revenueRatioIndicator(revenue, expenses));
        indicators.add(currentAccountIndicator(snapshot));
        indicators.add(requestedAmountIndicator(requestedAmount, maxRecommendedAmount));

        Decision globalDecision = indicators.stream()
                .anyMatch(indicator -> indicator.getDecision() == Decision.ROUGE) ? Decision.ROUGE :
                indicators.stream().anyMatch(indicator -> indicator.getDecision() == Decision.ORANGE) ? Decision.ORANGE :
                        Decision.VERT;

        return VigilanceResultResponse.builder()
                .snapshotId(snapshot.getId())
                .decisionType(request.getType())
                .requestedAmount(requestedAmount)
                .cashBefore(cashBefore)
                .cashAfter(cashAfter)
                .maxRecommendedAmount(maxRecommendedAmount)
                .coverageMonthsAfterDecision(divide(cashAfter, expenses))
                .globalDecision(globalDecision)
                .globalExplanation(globalExplanation(globalDecision))
                .recommendations(recommendations(globalDecision, maxRecommendedAmount))
                .indicators(indicators)
                .build();
    }

    private VigilanceIndicatorResponse cashCoverageIndicator(BigDecimal expenses, BigDecimal cashAfter) {
        BigDecimal coverage = divide(cashAfter, expenses);
        Decision decision = coverage.compareTo(THREE) >= 0 ? Decision.VERT :
                coverage.compareTo(ONE) >= 0 ? Decision.ORANGE : Decision.ROUGE;
        return VigilanceIndicatorResponse.builder()
                .code("CASH_COVERAGE")
                .label("Couverture de tresorerie apres decision")
                .value(coverage)
                .decision(decision)
                .details("Tresorerie projetee / charges mensuelles")
                .recommendation(decision == Decision.ROUGE ? "Reporter la decision ou reduire le montant." : "Surveiller les prochaines echeances.")
                .build();
    }

    private VigilanceIndicatorResponse revenueRatioIndicator(BigDecimal revenue, BigDecimal expenses) {
        BigDecimal ratio = divide(revenue, expenses);
        Decision decision = ratio.compareTo(RATIO_GREEN) >= 0 ? Decision.VERT :
                ratio.compareTo(ONE) >= 0 ? Decision.ORANGE : Decision.ROUGE;
        return VigilanceIndicatorResponse.builder()
                .code("REVENUE_EXPENSE_RATIO")
                .label("Ratio chiffre d'affaires / charges")
                .value(ratio)
                .decision(decision)
                .details("CA mensuel / charges mensuelles")
                .recommendation(decision == Decision.ROUGE ? "Analyser les charges ou attendre un encaissement client." : "Ratio acceptable sous surveillance.")
                .build();
    }

    private VigilanceIndicatorResponse currentAccountIndicator(FinancialSnapshot snapshot) {
        BigDecimal currentAccount = snapshot.getSoldeCompteCourant() == null ? BigDecimal.ZERO : snapshot.getSoldeCompteCourant();
        int debtorDays = snapshot.getDureeCompteCourantDebiteur() == null ? 0 : snapshot.getDureeCompteCourantDebiteur();
        Decision decision = currentAccount.signum() >= 0 ? Decision.VERT :
                debtorDays <= 30 ? Decision.ORANGE : Decision.ROUGE;
        return VigilanceIndicatorResponse.builder()
                .code("DEBTOR_CURRENT_ACCOUNT")
                .label("Compte courant dirigeant")
                .value(BigDecimal.valueOf(debtorDays))
                .decision(decision)
                .details("Duree en jours du compte courant debiteur")
                .recommendation(decision == Decision.ROUGE ? "Faire valider la situation par le comptable." : "Eviter de prolonger une position debitrice.")
                .build();
    }

    private VigilanceIndicatorResponse requestedAmountIndicator(BigDecimal requestedAmount, BigDecimal maxRecommendedAmount) {
        BigDecimal orangeLimit = maxRecommendedAmount.multiply(SEVENTY_FIVE_PERCENT);
        Decision decision = requestedAmount.compareTo(maxRecommendedAmount) > 0 ? Decision.ROUGE :
                requestedAmount.compareTo(orangeLimit) > 0 ? Decision.ORANGE : Decision.VERT;
        return VigilanceIndicatorResponse.builder()
                .code("REQUESTED_AMOUNT")
                .label("Montant demande vs maximum recommande")
                .value(requestedAmount)
                .decision(decision)
                .details("Maximum recommande = tresorerie - 3 mois de charges")
                .recommendation(decision == Decision.ROUGE ? "Limiter le montant a " + maxRecommendedAmount + " EUR ou attendre." : "Montant compatible avec la reserve cible.")
                .build();
    }

    private List<String> recommendations(Decision decision, BigDecimal maxRecommendedAmount) {
        List<String> recommendations = new ArrayList<>();
        if (decision == Decision.ROUGE) {
            recommendations.add("Ne pas executer la decision sans validation comptable.");
            recommendations.add("Montant maximum recommande: " + maxRecommendedAmount + " EUR.");
        } else if (decision == Decision.ORANGE) {
            recommendations.add("Decision possible sous vigilance, avec controle des echeances a 30 jours.");
        } else {
            recommendations.add("Situation compatible avec la decision simulee.");
        }
        return recommendations;
    }

    private String globalExplanation(Decision decision) {
        return switch (decision) {
            case ROUGE -> "Au moins un indicateur est critique: la decision doit etre reportee ou validee.";
            case ORANGE -> "Aucun indicateur critique, mais au moins un signal demande de la vigilance.";
            case VERT -> "Les indicateurs disponibles sont compatibles avec la decision.";
        };
    }

    private BigDecimal positive(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value.max(BigDecimal.ZERO);
    }

    private BigDecimal divide(BigDecimal left, BigDecimal right) {
        if (right == null || right.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return left.divide(right, 2, RoundingMode.HALF_UP);
    }
}

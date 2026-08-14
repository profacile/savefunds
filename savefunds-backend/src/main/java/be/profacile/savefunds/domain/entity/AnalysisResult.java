package be.profacile.savefunds.domain.entity;

import be.profacile.savefunds.domain.enums.Decision;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité représentant le résultat détaillé d'une analysis de prélèvement.
 *
 * Structure :
 * - Décision globale + détails + recommendation
 * - Par critère : score, décision, détails, recommendation
 * - Métadonnées de création
 */
@Entity
@Table(name = "resultats_analyse", indexes = {
        @Index(name = "idx_resultat_analyse", columnList = "analyse_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== RELATION ====================

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "analyse_id", nullable = false)
    private WithdrawalAnalysis analysis;

    // ==================== DÉCISION GLOBALE ====================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50, name = "decision_globale")
    private Decision globalDecision;

    @Column(columnDefinition = "TEXT", name = "details_decision_globale")
    private String globalDecisionDetails;

    @Column(columnDefinition = "TEXT", name = "recommandation_globale")
    private String globalRecommendation;

    // ==================== SCORES (valeurs calculées) ====================

    @Column(precision = 19, scale = 2, name = "score_tresorerie")
    private BigDecimal cashScore;

    @Column(precision = 5, scale = 2, name = "score_ratio_ca_charges")
    private BigDecimal revenueExpensesRatioScore;

    @Column(name = "score_compte_courant_debiteur")
    private Integer directorCurrentAccountScore;

    @Column(precision = 19, scale = 2, name = "montant_max_prelevable")
    private BigDecimal maxWithdrawableAmount;

    // ==================== DÉCISIONS PAR CRITÈRE ====================

    @Enumerated(EnumType.STRING)
    @Column(length = 50, name = "decision_tresorerie")
    private Decision cashDecision;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, name = "decision_ratio_ca_charges")
    private Decision revenueExpensesRatioDecision;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, name = "decision_compte_courant")
    private Decision directorCurrentAccountDecision;

    // ==================== DÉTAILS PAR CRITÈRE ====================

    @Column(columnDefinition = "TEXT", name = "details_tresorerie")
    private String cashDetails;

    @Column(columnDefinition = "TEXT", name = "details_ratio_ca_charges")
    private String revenueExpensesRatioDetails;

    @Column(columnDefinition = "TEXT", name = "details_compte_courant")
    private String directorCurrentAccountDetails;

    // ==================== RECOMMANDATIONS PAR CRITÈRE ====================

    @Column(columnDefinition = "TEXT", name = "recommandation_tresorerie")
    private String cashRecommendation;

    @Column(columnDefinition = "TEXT", name = "recommandation_ratio_ca_charges")
    private String revenueExpensesRatioRecommendation;

    @Column(columnDefinition = "TEXT", name = "recommandation_compte_courant")
    private String directorCurrentAccountRecommendation;

    // ==================== MÉTADONNÉES ====================

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;
}

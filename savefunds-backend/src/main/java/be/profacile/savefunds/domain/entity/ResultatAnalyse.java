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
 * Entité représentant le résultat détaillé d'une analyse de prélèvement.
 *
 * Structure :
 * - Décision globale + détails + recommandation
 * - Par critère : score, décision, détails, recommandation
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
public class ResultatAnalyse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== RELATION ====================

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "analyse_id", nullable = false)
    private AnalysePrelevement analyse;

    // ==================== DÉCISION GLOBALE ====================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50, name = "decision_globale")
    private Decision decisionGlobale;

    @Column(columnDefinition = "TEXT", name = "details_decision_globale")
    private String detailsDecisionGlobale;

    @Column(columnDefinition = "TEXT", name = "recommandation_globale")
    private String recommandationGlobale;

    // ==================== SCORES (valeurs calculées) ====================

    @Column(precision = 19, scale = 2, name = "score_tresorerie")
    private BigDecimal scoreTresorerie;

    @Column(precision = 5, scale = 2, name = "score_ratio_ca_charges")
    private BigDecimal scoreRatioCACharges;

    @Column(name = "score_compte_courant_debiteur")
    private Integer scoreCompteCourantDebiteur;

    @Column(precision = 19, scale = 2, name = "montant_max_prelevable")
    private BigDecimal montantMaxPrelevable;

    // ==================== DÉCISIONS PAR CRITÈRE ====================

    @Enumerated(EnumType.STRING)
    @Column(length = 50, name = "decision_tresorerie")
    private Decision decisionTresorerie;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, name = "decision_ratio_ca_charges")
    private Decision decisionRatioCACharges;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, name = "decision_compte_courant")
    private Decision decisionCompteCourant;

    // ==================== DÉTAILS PAR CRITÈRE ====================

    @Column(columnDefinition = "TEXT", name = "details_tresorerie")
    private String detailsTresorerie;

    @Column(columnDefinition = "TEXT", name = "details_ratio_ca_charges")
    private String detailsRatioCACharges;

    @Column(columnDefinition = "TEXT", name = "details_compte_courant")
    private String detailsCompteCourant;

    // ==================== RECOMMANDATIONS PAR CRITÈRE ====================

    @Column(columnDefinition = "TEXT", name = "recommandation_tresorerie")
    private String recommandationTresorerie;

    @Column(columnDefinition = "TEXT", name = "recommandation_ratio_ca_charges")
    private String recommandationRatioCACharges;

    @Column(columnDefinition = "TEXT", name = "recommandation_compte_courant")
    private String recommandationCompteCourant;

    // ==================== MÉTADONNÉES ====================

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;
}
package be.profacile.savefunds.domain.entity;

import be.profacile.savefunds.domain.enums.AnalysisStatus;
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
 * Entité représentant une demande d'analysis de prélèvement.
 *
 * Relations :
 * - N WithdrawalAnalysis → 1 Company
 * - 1 WithdrawalAnalysis → 1 AnalysisResult (optionnel, créé après analysis)
 */
@Entity
@Table(name = "analyses_prelevement", indexes = {
        @Index(name = "idx_analyse_entreprise", columnList = "entreprise_id"),
        @Index(name = "idx_analyse_statut", columnList = "statut"),
        @Index(name = "idx_analysis_created", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== RELATION ENTREPRISE ====================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Company company;

    // ==================== DONNÉES ANALYSE ====================

    @Column(nullable = false, precision = 19, scale = 2, name = "montant_souhaite")
    private BigDecimal requestedAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 50)
    @Builder.Default
    private AnalysisStatus status = AnalysisStatus.EN_ATTENTE;

    // ==================== MÉTADONNÉES ====================

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    // ==================== RELATION RÉSULTAT ====================

    @OneToOne(mappedBy = "analysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private AnalysisResult result;
}
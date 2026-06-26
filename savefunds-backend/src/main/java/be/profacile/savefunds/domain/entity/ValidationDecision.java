package be.profacile.savefunds.domain.entity;

import be.profacile.savefunds.domain.enums.FinancialDecisionType;
import be.profacile.savefunds.domain.enums.ValidationDecisionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "validation_decisions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    @Column(nullable = false)
    private Long requestedByUserId;

    private Long decidedByAccountantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FinancialDecisionType decisionType;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal requestedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValidationDecisionStatus status = ValidationDecisionStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String conditionText;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private LocalDateTime decidedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

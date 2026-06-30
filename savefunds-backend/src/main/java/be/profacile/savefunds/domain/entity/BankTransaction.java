package be.profacile.savefunds.domain.entity;

import be.profacile.savefunds.domain.enums.TransactionClassificationType;
import be.profacile.savefunds.domain.enums.TransactionReviewStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_transactions")
@Data
public class BankTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_snapshot_id")
    private FinancialSnapshot financialSnapshot;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(precision = 19, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionClassificationType classification;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_status", nullable = false)
    private TransactionReviewStatus reviewStatus;

    @Column(name = "confidence_score")
    private Integer confidenceScore;

    @Column(name = "impacts_director_current_account", nullable = false)
    private boolean impactsDirectorCurrentAccount;

    @Column(name = "director_current_account_impact", precision = 19, scale = 2)
    private BigDecimal directorCurrentAccountImpact;

    @Column(name = "ai_reason", columnDefinition = "TEXT")
    private String aiReason;

    @Column(name = "reviewed_by_user_id")
    private Long reviewedByUserId;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

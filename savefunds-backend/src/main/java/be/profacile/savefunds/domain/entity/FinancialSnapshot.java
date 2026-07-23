package be.profacile.savefunds.domain.entity;

import be.profacile.savefunds.domain.enums.FinancialSnapshotSource;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_snapshots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FinancialSnapshotSource source;

    @Column(name = "source_reference")
    private String sourceReference;

    @Column(name = "chiffre_affaires_mensuel", precision = 19, scale = 2)
    private BigDecimal monthlyRevenue;

    @Column(name = "charges_mensuelles", precision = 19, scale = 2)
    private BigDecimal monthlyExpenses;

    @Column(name = "tresorerie", precision = 19, scale = 2)
    private BigDecimal cashBalance;

    @Column(name = "solde_compte_courant", precision = 19, scale = 2)
    private BigDecimal directorCurrentAccountBalance;

    @Column(name = "dettes_court_terme", precision = 19, scale = 2)
    private BigDecimal shortTermDebt;

    @Column(name = "creances_clients", precision = 19, scale = 2)
    private BigDecimal customerReceivables;

    @Column(name = "duree_compte_courant_debiteur")
    private Integer directorCurrentAccountDebtorDays;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate = LocalDate.now();

    @Column(name = "confidence_score")
    private Integer confidenceScore;

    @Column(name = "warnings", columnDefinition = "TEXT")
    private String warnings;

    @Column(name = "missing_fields", columnDefinition = "TEXT")
    private String missingFields;

    @Column(name = "raw_metadata", columnDefinition = "TEXT")
    private String rawMetadata;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

package be.profacile.savefunds.domain.entity;

import be.profacile.savefunds.domain.enums.BnbAnnualAccountsStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bnb_annual_accounts_lookups")
@Data
public class BnbAnnualAccountsLookup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    @Column(name = "enterprise_number", nullable = false)
    private String enterpriseNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BnbAnnualAccountsStatus status;

    @Column(name = "consult_url", nullable = false, length = 1000)
    private String consultUrl;

    @Column(name = "xbrl_url", length = 1000)
    private String xbrlUrl;

    @Column(name = "pdf_url", length = 1000)
    private String pdfUrl;

    @Column(name = "csv_url", length = 1000)
    private String csvUrl;

    @Column(name = "latest_deposit_id")
    private String latestDepositId;

    @Column(name = "latest_reference")
    private String latestReference;

    @Column(name = "latest_model_name")
    private String latestModelName;

    @Column(name = "latest_period_end_date")
    private String latestPeriodEndDate;

    @Column(name = "latest_deposit_date")
    private String latestDepositDate;

    @Column(name = "deposits_count")
    private Integer depositsCount;

    @Column(nullable = false)
    private String source;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "raw_metadata", columnDefinition = "TEXT")
    private String rawMetadata;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

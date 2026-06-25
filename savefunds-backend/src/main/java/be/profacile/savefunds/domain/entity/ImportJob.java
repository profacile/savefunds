package be.profacile.savefunds.domain.entity;

import be.profacile.savefunds.domain.enums.FinancialSnapshotSource;
import be.profacile.savefunds.domain.enums.ImportJobStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "import_jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snapshot_id")
    private FinancialSnapshot snapshot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FinancialSnapshotSource source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImportJobStatus status;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "parser_version")
    private String parserVersion;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

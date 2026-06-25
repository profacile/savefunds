package be.profacile.savefunds.domain.entity;

import be.profacile.savefunds.domain.enums.StatutAnalyse;
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
 * Entité représentant une demande d'analyse de prélèvement.
 *
 * Relations :
 * - N AnalysePrelevement → 1 Entreprise
 * - 1 AnalysePrelevement → 1 ResultatAnalyse (optionnel, créé après analyse)
 */
@Entity
@Table(name = "analyses_prelevement", indexes = {
        @Index(name = "idx_analyse_entreprise", columnList = "entreprise_id"),
        @Index(name = "idx_analyse_statut", columnList = "statut"),
        @Index(name = "idx_analyse_created", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysePrelevement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== RELATION ENTREPRISE ====================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    // ==================== DONNÉES ANALYSE ====================

    @Column(nullable = false, precision = 19, scale = 2, name = "montant_souhaite")
    private BigDecimal montantSouhaite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private StatutAnalyse statut = StatutAnalyse.EN_ATTENTE;

    // ==================== MÉTADONNÉES ====================

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    // ==================== RELATION RÉSULTAT ====================

    @OneToOne(mappedBy = "analyse", cascade = CascadeType.ALL, orphanRemoval = true)
    private ResultatAnalyse resultat;
}
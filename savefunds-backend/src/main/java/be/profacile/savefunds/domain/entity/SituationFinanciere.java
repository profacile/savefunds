package be.profacile.savefunds.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "situations_financieres")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SituationFinanciere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long entrepriseId;

    // Snapshot des données financières
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal chiffreAffairesMensuel;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal chargesMensuelles;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal tresorerie;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal soldeCompteCourant;

    // Indicateurs calculés
    @Column(precision = 5, scale = 2)
    private BigDecimal ratioCACharges;

    @Column(precision = 19, scale = 2)
    private BigDecimal tresorerieEnMois;

    private Integer dureeCompteCourantDebiteur;

    // Métadonnées
    @Column(nullable = false, updatable = false)
    private LocalDateTime capturedAt = LocalDateTime.now();

    private String source; // "MANUEL", "IMPORT", "API"

    @Column(columnDefinition = "TEXT")
    private String notes;
}
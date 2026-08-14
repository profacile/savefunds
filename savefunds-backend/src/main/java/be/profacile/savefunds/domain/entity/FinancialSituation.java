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
public class FinancialSituation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entreprise_id", nullable = false)
    private Long companyId;

    // Snapshot des données financières
    @Column(name = "chiffre_affaires_mensuel", nullable = false, precision = 19, scale = 2)
    private BigDecimal monthlyRevenue;

    @Column(name = "charges_mensuelles", nullable = false, precision = 19, scale = 2)
    private BigDecimal monthlyExpenses;

    @Column(name = "tresorerie", nullable = false, precision = 19, scale = 2)
    private BigDecimal cashBalance;

    @Column(name = "solde_compte_courant", nullable = false, precision = 19, scale = 2)
    private BigDecimal directorCurrentAccountBalance;

    // FinancialIndicators calculés
    @Column(name = "ratio_ca_charges", precision = 5, scale = 2)
    private BigDecimal ratioCACharges;

    @Column(name = "tresorerie_en_mois", precision = 19, scale = 2)
    private BigDecimal cashCoverageMonths;

    @Column(name = "duree_compte_courant_debiteur")
    private Integer directorCurrentAccountDebtorDays;

    // Métadonnées
    @Column(name = "captured_at", nullable = false, updatable = false)
    private LocalDateTime capturedAt = LocalDateTime.now();

    private String source; // "MANUEL", "IMPORT", "API"

    @Column(columnDefinition = "TEXT")
    private String notes;
}

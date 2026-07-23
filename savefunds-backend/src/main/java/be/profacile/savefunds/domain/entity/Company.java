package be.profacile.savefunds.domain.entity;

import be.profacile.savefunds.domain.enums.CompanyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "entreprises")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom")
    private String name;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "raison_sociale", nullable = false)
    private String legalName;

    @Column(name = "numero_entreprise", nullable = false)
    private String enterpriseNumber;

    @Column(name = "forme_juridique")
    private String legalForm;

    @Column(name = "secteur_activite")
    private String activitySector;

    @Column(name = "chiffre_affaires_mensuel", precision = 19, scale = 2)
    private BigDecimal monthlyRevenue;

    @Column(name = "charges_mensuelles", precision = 19, scale = 2)
    private BigDecimal monthlyExpenses;

    @Column(name = "tresorerie", precision = 19, scale = 2)
    private BigDecimal cashBalance;

    @Column(name = "solde_compte_courant", precision = 19, scale = 2)
    private BigDecimal directorCurrentAccountBalance;

    // Date depuis laquelle le compte courant est débiteur (solde < 0)
    // Null si le compte n'est pas débiteur
    @Column(name = "date_debut_debiteur_cc")
    private LocalDate directorCurrentAccountDebitStartDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private CompanyStatus status = CompanyStatus.EN_CREATION;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
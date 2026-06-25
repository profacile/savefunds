package be.profacile.savefunds.domain.entity;

import be.profacile.savefunds.domain.enums.StatutEntreprise;
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
public class Entreprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String raisonSociale;

    @Column(nullable = false)
    private String numeroEntreprise;

    private String formeJuridique;
    private String secteurActivite;

    @Column(precision = 19, scale = 2)
    private BigDecimal chiffreAffairesMensuel;

    @Column(precision = 19, scale = 2)
    private BigDecimal chargesMensuelles;

    @Column(precision = 19, scale = 2)
    private BigDecimal tresorerie;

    @Column(precision = 19, scale = 2)
    private BigDecimal soldeCompteCourant;

    // Date depuis laquelle le compte courant est débiteur (solde < 0)
    // Null si le compte n'est pas débiteur
    @Column(name = "date_debut_debiteur_cc")
    private LocalDate dateDebutDebiteurCC;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEntreprise statut = StatutEntreprise.EN_CREATION;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
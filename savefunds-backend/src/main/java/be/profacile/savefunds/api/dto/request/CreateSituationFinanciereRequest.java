package be.profacile.savefunds.api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSituationFinanciereRequest {

    @NotNull(message = "L'identifiant de l'entreprise est obligatoire")
    private Long entrepriseId;

    @NotNull(message = "Le chiffre d'affaires mensuel est obligatoire")
    @DecimalMin(value = "0.00", inclusive = true, message = "Le chiffre d'affaires mensuel doit être supérieur ou égal à 0")
    private BigDecimal chiffreAffairesMensuel;

    @NotNull(message = "Les charges mensuelles sont obligatoires")
    @DecimalMin(value = "0.00", inclusive = true, message = "Les charges mensuelles doivent être supérieures ou égales à 0")
    private BigDecimal chargesMensuelles;

    @NotNull(message = "La trésorerie est obligatoire")
    private BigDecimal tresorerie;

    @NotNull(message = "Le solde du compte courant est obligatoire")
    private BigDecimal soldeCompteCourant;

    @PositiveOrZero(message = "La durée du compte courant débiteur doit être positive ou nulle")
    private Integer dureeCompteCourantDebiteur;

    private String source;

    private String notes;
}
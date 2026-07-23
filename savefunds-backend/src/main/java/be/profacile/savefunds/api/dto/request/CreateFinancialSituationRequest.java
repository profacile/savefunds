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
public class CreateFinancialSituationRequest {

    @NotNull(message = "L'identifiant de l'company est obligatoire")
    private Long companyId;

    @NotNull(message = "Le chiffre d'affaires mensuel est obligatoire")
    @DecimalMin(value = "0.00", inclusive = true, message = "Le chiffre d'affaires mensuel doit être supérieur ou égal à 0")
    private BigDecimal monthlyRevenue;

    @NotNull(message = "Les charges mensuelles sont obligatoires")
    @DecimalMin(value = "0.00", inclusive = true, message = "Les charges mensuelles doivent être supérieures ou égales à 0")
    private BigDecimal monthlyExpenses;

    @NotNull(message = "La trésorerie est obligatoire")
    private BigDecimal cashBalance;

    @NotNull(message = "Le solde du compte courant est obligatoire")
    private BigDecimal directorCurrentAccountBalance;

    @PositiveOrZero(message = "La durée du compte courant débiteur doit être positive ou nulle")
    private Integer directorCurrentAccountDebtorDays;

    private String source;

    private String notes;
}
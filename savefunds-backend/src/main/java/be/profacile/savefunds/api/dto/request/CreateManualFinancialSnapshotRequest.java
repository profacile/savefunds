package be.profacile.savefunds.api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateManualFinancialSnapshotRequest {

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal chiffreAffairesMensuel;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal chargesMensuelles;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal tresorerie;

    private BigDecimal soldeCompteCourant;
    private BigDecimal dettesCourtTerme;
    private BigDecimal creancesClients;
    private Integer dureeCompteCourantDebiteur;
    private LocalDate snapshotDate;
}

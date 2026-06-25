package be.profacile.savefunds.api.dto.response;

import be.profacile.savefunds.domain.enums.FinancialSnapshotSource;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class FinancialSnapshotResponse {
    private Long id;
    private Long entrepriseId;
    private FinancialSnapshotSource source;
    private String sourceReference;
    private BigDecimal chiffreAffairesMensuel;
    private BigDecimal chargesMensuelles;
    private BigDecimal tresorerie;
    private BigDecimal soldeCompteCourant;
    private BigDecimal dettesCourtTerme;
    private BigDecimal creancesClients;
    private Integer dureeCompteCourantDebiteur;
    private LocalDate snapshotDate;
    private Integer confidenceScore;
    private List<String> warnings;
    private List<String> missingFields;
    private LocalDateTime createdAt;
}

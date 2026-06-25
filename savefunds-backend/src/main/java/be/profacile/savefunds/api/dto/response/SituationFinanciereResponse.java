package be.profacile.savefunds.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SituationFinanciereResponse {

    private Long id;
    private Long entrepriseId;

    private BigDecimal chiffreAffairesMensuel;
    private BigDecimal chargesMensuelles;
    private BigDecimal tresorerie;
    private BigDecimal soldeCompteCourant;

    private BigDecimal ratioCACharges;
    private BigDecimal tresorerieEnMois;
    private Integer dureeCompteCourantDebiteur;

    private LocalDateTime capturedAt;
    private String source;
    private String notes;
}
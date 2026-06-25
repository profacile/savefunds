package be.profacile.savefunds.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO pour la mise à jour partielle d'une analyse.
 * Seuls montantSouhaite et statut sont modifiables.
 * Une analyse TERMINEE ne peut plus être modifiée (validé côté service).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour mettre à jour une analyse de prélèvement (mise à jour partielle)")
public class UpdateAnalyseRequest {

    @DecimalMin(value = "0.01", message = "Le montant souhaité doit être supérieur à 0")
    @DecimalMax(value = "999999999.99", message = "Le montant souhaité est trop élevé")
    @Digits(integer = 9, fraction = 2, message = "Le montant ne peut avoir que 2 décimales maximum")
    @Schema(description = "Nouveau montant souhaité (EUR)", example = "6000.00")
    private BigDecimal montantSouhaite;
}
package be.profacile.savefunds.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO pour la création d'une nouvelle analyse de prélèvement.
 * 
 * Règles de validation :
 * - entrepriseId : obligatoire, > 0
 * - montantSouhaite : obligatoire, > 0, max 2 décimales
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour créer une nouvelle analyse de prélèvement")
public class CreateAnalyseRequest {

    @NotNull(message = "L'ID de l'entreprise est obligatoire")
    @Positive(message = "L'ID de l'entreprise doit être positif")
    @Schema(description = "ID de l'entreprise pour laquelle l'analyse est demandée", 
            example = "1", 
            required = true)
    private Long entrepriseId;

    @NotNull(message = "Le montant souhaité est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant souhaité doit être supérieur à 0")
    @DecimalMax(value = "999999999.99", message = "Le montant souhaité est trop élevé")
    @Digits(integer = 9, fraction = 2, message = "Le montant ne peut avoir que 2 décimales maximum")
    @Schema(description = "Montant que le dirigeant souhaite prélever (en EUR)", 
            example = "5000.00", 
            required = true,
            minimum = "0.01",
            maximum = "999999999.99")
    private BigDecimal montantSouhaite;
}
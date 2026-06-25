package be.profacile.savefunds.api.dto.response;

import be.profacile.savefunds.domain.enums.StatutAnalyse;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour une analyse de prélèvement.
 * 
 * Contient :
 * - Identifiants (id, entrepriseId)
 * - Montant souhaité
 * - Statut (EN_ATTENTE, TERMINEE, ANNULEE)
 * - Dates de création et de dernière mise à jour
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Détails d'une analyse de prélèvement")
public class AnalyseResponse {

    @Schema(description = "ID unique de l'analyse", example = "1")
    private Long id;

    @Schema(description = "ID de l'entreprise concernée", example = "1")
    private Long entrepriseId;

    @Schema(description = "Montant que le dirigeant souhaite prélever (EUR)", example = "5000.00")
    private BigDecimal montantSouhaite;

    @Schema(description = "Statut de l'analyse", example = "EN_ATTENTE", 
            allowableValues = {"EN_ATTENTE", "TERMINEE", "ANNULEE"})
    private StatutAnalyse statut;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Date et heure de création de l'analyse", 
            example = "2026-04-06T14:30:00")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Date et heure de dernière modification", 
            example = "2026-04-06T14:35:00")
    private LocalDateTime updatedAt;
}
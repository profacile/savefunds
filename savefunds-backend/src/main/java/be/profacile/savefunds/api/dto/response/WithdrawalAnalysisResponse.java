package be.profacile.savefunds.api.dto.response;

import be.profacile.savefunds.domain.enums.AnalysisStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour une analysis de prélèvement.
 * 
 * Contient :
 * - Identifiants (id, companyId)
 * - Montant souhaité
 * - Statut (EN_ATTENTE, TERMINEE, ANNULEE)
 * - Dates de création et de dernière mise à jour
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Détails d'une analysis de prélèvement")
public class WithdrawalAnalysisResponse {

    @Schema(description = "ID unique de l'analysis", example = "1")
    private Long id;

    @Schema(description = "ID de l'company concernée", example = "1")
    private Long companyId;

    @Schema(description = "Montant que le dirigeant souhaite prélever (EUR)", example = "5000.00")
    private BigDecimal requestedAmount;

    @Schema(description = "Statut de l'analysis", example = "EN_ATTENTE", 
            allowableValues = {"EN_ATTENTE", "TERMINEE", "ANNULEE"})
    private AnalysisStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Date et heure de création de l'analysis", 
            example = "2026-04-06T14:30:00")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Date et heure de dernière modification", 
            example = "2026-04-06T14:35:00")
    private LocalDateTime updatedAt;
}
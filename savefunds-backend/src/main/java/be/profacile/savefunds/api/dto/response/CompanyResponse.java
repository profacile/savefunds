package be.profacile.savefunds.api.dto.response;

import be.profacile.savefunds.domain.enums.CompanyStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Détails d'une company")
public class CompanyResponse {

    @Schema(description = "ID unique de l'company", example = "1")
    private Long id;

    @Schema(description = "ID de l'utilisateur propriétaire", example = "1")
    private Long userId;

    @Schema(description = "Raison sociale", example = "Profacile SRL")
    private String legalName;

    @Schema(description = "Numéro d'company belge", example = "BE0123456789")
    private String enterpriseNumber;

    @Schema(description = "Forme juridique", example = "SRL")
    private String legalForm;

    @Schema(description = "Secteur d'activité", example = "Informatique")
    private String activitySector;

    @Schema(description = "Chiffre d'affaires mensuel (EUR)", example = "50000.00")
    private BigDecimal monthlyRevenue;

    @Schema(description = "Charges mensuelles (EUR)", example = "30000.00")
    private BigDecimal monthlyExpenses;

    @Schema(description = "Trésorerie disponible (EUR)", example = "100000.00")
    private BigDecimal cashBalance;

    @Schema(description = "Solde du compte courant (EUR)", example = "5000.00")
    private BigDecimal directorCurrentAccountBalance;

    @Schema(description = "Statut de l'company", example = "ACTIVE")
    private CompanyStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Date de création", example = "2026-04-06T14:30:00")
    private LocalDateTime createdAt;
}
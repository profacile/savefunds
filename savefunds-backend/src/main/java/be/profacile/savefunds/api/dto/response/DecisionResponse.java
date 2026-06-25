package be.profacile.savefunds.api.dto.response;


import be.profacile.savefunds.domain.enums.Decision;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionResponse {
    private String critere;           // "TRESORERIE", "RATIO_CA_CHARGES", etc.
    private String valeur;             // "2.5 mois", "1.25", "15 jours"
    private Decision decision;         // VERT, ORANGE, ROUGE
    private String explication;        // Message détaillé
}
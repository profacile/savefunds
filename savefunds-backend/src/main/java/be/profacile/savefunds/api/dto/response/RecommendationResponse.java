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
public class RecommendationResponse {
    private String criterion;           // "TRESORERIE", "RATIO", etc.
    private Decision decision;         // VERT, ORANGE, ROUGE
    private String message;            // Recommendation personnalisée
}
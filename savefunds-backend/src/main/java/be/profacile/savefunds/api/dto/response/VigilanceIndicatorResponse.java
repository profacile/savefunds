package be.profacile.savefunds.api.dto.response;

import be.profacile.savefunds.domain.enums.Decision;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class VigilanceIndicatorResponse {
    private String code;
    private String label;
    private BigDecimal value;
    private Decision decision;
    private String details;
    private String recommendation;
}

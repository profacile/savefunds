package be.profacile.savefunds.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateEntrepriseFromRegistryRequest {
    @NotBlank
    private String enterpriseNumber;

    private String name;
    private String legalForm;
    private String activityLabel;
}

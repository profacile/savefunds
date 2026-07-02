package be.profacile.savefunds.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateEntrepriseFromRegistryRequest {
    @NotBlank
    private String enterpriseNumber;

    private String name;
    private String legalForm;
    private String status;
    private String address;
    private String postalCode;
    private String city;
    private String naceCode;
    private String activityLabel;
    private String source;
    private Boolean active;
}

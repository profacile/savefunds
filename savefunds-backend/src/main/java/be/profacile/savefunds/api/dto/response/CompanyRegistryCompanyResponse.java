package be.profacile.savefunds.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyRegistryCompanyResponse {
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
    private boolean active;
}

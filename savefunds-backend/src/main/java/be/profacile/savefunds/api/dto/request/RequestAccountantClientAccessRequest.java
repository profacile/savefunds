package be.profacile.savefunds.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestAccountantClientAccessRequest {

    @NotBlank(message = "Le numero d'entreprise est obligatoire")
    private String enterpriseNumber;

    @Size(max = 1000, message = "La note ne peut pas depasser 1000 caracteres")
    private String requestNote;
}

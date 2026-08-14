package be.profacile.savefunds.api.dto.request;

import be.profacile.savefunds.domain.enums.AccountantClientAccessStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DecideAccountantClientAccessRequest {

    @NotNull(message = "Le statut est obligatoire")
    private AccountantClientAccessStatus status;

    @Size(max = 1000, message = "La note ne peut pas depasser 1000 caracteres")
    private String responseNote;
}

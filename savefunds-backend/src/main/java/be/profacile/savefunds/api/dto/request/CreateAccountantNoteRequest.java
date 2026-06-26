package be.profacile.savefunds.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAccountantNoteRequest {
    @NotBlank
    @Size(max = 2000)
    private String content;
}

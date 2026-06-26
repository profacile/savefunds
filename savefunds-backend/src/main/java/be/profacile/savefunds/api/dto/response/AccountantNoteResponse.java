package be.profacile.savefunds.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AccountantNoteResponse {
    private Long id;
    private Long entrepriseId;
    private Long accountantId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

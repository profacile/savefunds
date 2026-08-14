package be.profacile.savefunds.api.dto.response;

import be.profacile.savefunds.domain.enums.AccountantClientAccessStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AccountantClientAccessResponse {
    private Long id;
    private Long accountantId;
    private String accountantName;
    private String accountantEmail;
    private Long companyId;
    private String companyName;
    private String enterpriseNumber;
    private AccountantClientAccessStatus status;
    private String requestNote;
    private String responseNote;
    private LocalDateTime decidedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

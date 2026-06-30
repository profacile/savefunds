package be.profacile.savefunds.api.dto.response;

import be.profacile.savefunds.domain.enums.BnbAnnualAccountsStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BnbAnnualAccountsLookupResponse {
    private Long id;
    private Long entrepriseId;
    private String enterpriseNumber;
    private BnbAnnualAccountsStatus status;
    private String consultUrl;
    private String xbrlUrl;
    private String pdfUrl;
    private String csvUrl;
    private String latestDepositId;
    private String latestReference;
    private String latestModelName;
    private String latestPeriodEndDate;
    private String latestDepositDate;
    private Integer depositsCount;
    private String source;
    private String message;
    private String rawMetadata;
    private LocalDateTime createdAt;
}

package be.profacile.savefunds.api.mapper;

import be.profacile.savefunds.api.dto.response.BnbAnnualAccountsLookupResponse;
import be.profacile.savefunds.domain.entity.BnbAnnualAccountsLookup;
import org.springframework.stereotype.Component;

@Component
public class BnbAnnualAccountsLookupMapper {

    public BnbAnnualAccountsLookupResponse toResponse(BnbAnnualAccountsLookup lookup) {
        return BnbAnnualAccountsLookupResponse.builder()
                .id(lookup.getId())
                .entrepriseId(lookup.getEntreprise().getId())
                .enterpriseNumber(lookup.getEnterpriseNumber())
                .status(lookup.getStatus())
                .consultUrl(lookup.getConsultUrl())
                .xbrlUrl(lookup.getXbrlUrl())
                .pdfUrl(lookup.getPdfUrl())
                .csvUrl(lookup.getCsvUrl())
                .latestDepositId(lookup.getLatestDepositId())
                .latestReference(lookup.getLatestReference())
                .latestModelName(lookup.getLatestModelName())
                .latestPeriodEndDate(lookup.getLatestPeriodEndDate())
                .latestDepositDate(lookup.getLatestDepositDate())
                .depositsCount(lookup.getDepositsCount())
                .source(lookup.getSource())
                .message(lookup.getMessage())
                .rawMetadata(lookup.getRawMetadata())
                .createdAt(lookup.getCreatedAt())
                .build();
    }
}

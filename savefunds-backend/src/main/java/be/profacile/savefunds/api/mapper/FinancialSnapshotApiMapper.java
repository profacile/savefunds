package be.profacile.savefunds.api.mapper;

import be.profacile.savefunds.api.dto.response.FinancialSnapshotResponse;
import be.profacile.savefunds.domain.entity.FinancialSnapshot;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class FinancialSnapshotApiMapper {

    public FinancialSnapshotResponse toResponse(FinancialSnapshot snapshot) {
        return FinancialSnapshotResponse.builder()
                .id(snapshot.getId())
                .companyId(snapshot.getCompany().getId())
                .source(snapshot.getSource())
                .sourceReference(snapshot.getSourceReference())
                .monthlyRevenue(snapshot.getMonthlyRevenue())
                .monthlyExpenses(snapshot.getMonthlyExpenses())
                .cashBalance(snapshot.getCashBalance())
                .directorCurrentAccountBalance(snapshot.getDirectorCurrentAccountBalance())
                .shortTermDebt(snapshot.getShortTermDebt())
                .customerReceivables(snapshot.getCustomerReceivables())
                .directorCurrentAccountDebtorDays(snapshot.getDirectorCurrentAccountDebtorDays())
                .snapshotDate(snapshot.getSnapshotDate())
                .confidenceScore(snapshot.getConfidenceScore())
                .warnings(split(snapshot.getWarnings()))
                .missingFields(split(snapshot.getMissingFields()))
                .rawMetadata(snapshot.getRawMetadata())
                .createdAt(snapshot.getCreatedAt())
                .build();
    }

    private List<String> split(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split("\\R"))
                .filter(line -> !line.isBlank())
                .toList();
    }
}

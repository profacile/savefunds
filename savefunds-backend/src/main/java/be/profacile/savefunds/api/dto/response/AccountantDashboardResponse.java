package be.profacile.savefunds.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AccountantDashboardResponse {
    private int totalClients;
    private int greenClients;
    private int orangeClients;
    private int redClients;
    private List<AccountantClientSummaryResponse> clients;
}

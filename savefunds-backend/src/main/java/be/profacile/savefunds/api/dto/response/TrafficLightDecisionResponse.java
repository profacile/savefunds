package be.profacile.savefunds.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrafficLightDecisionResponse {
    private Map<String, String> cashBalance;
    private Map<String, String> ratioCACharges;
    private Map<String, String> compteCourant;
    private Map<String, String> globalDecision;
}
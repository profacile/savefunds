package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.request.CreateFinancialSituationRequest;
import be.profacile.savefunds.api.dto.response.FinancialSituationResponse;
import be.profacile.savefunds.api.mapper.FinancialSituationMapper;
import be.profacile.savefunds.domain.entity.FinancialSituation;
import be.profacile.savefunds.domain.service.FinancialSituationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/situations")   // ← cohérence avec /api/v1/...
@RequiredArgsConstructor
@Tag(name = "Situations Financières", description = "Gestion de l'historique financier")
public class FinancialSituationController {

    private final FinancialSituationService situationService;
    private final FinancialSituationMapper situationMapper;

    @GetMapping("/{id}")
    public ResponseEntity<FinancialSituationResponse> getById(@PathVariable Long id) {
        return situationService.findById(id)
                .map(situationMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<FinancialSituationResponse>> getByCompanyId(
            @PathVariable Long companyId) {
        List<FinancialSituationResponse> responses = situationService
                .findByCompanyId(companyId)
                .stream()
                .map(situationMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/company/{companyId}/last")
    public ResponseEntity<FinancialSituationResponse> getLast(@PathVariable Long companyId) {
        return ResponseEntity.ok(
                situationMapper.toResponse(situationService.findLastByCompanyId(companyId))
        );
    }

    @PostMapping
    public ResponseEntity<FinancialSituationResponse> create(
            @Valid @RequestBody CreateFinancialSituationRequest request) {
        FinancialSituation entity = situationMapper.toEntity(request);
        FinancialSituation created = situationService.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(situationMapper.toResponse(created));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        situationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
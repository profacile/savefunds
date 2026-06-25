package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.request.CreateSituationFinanciereRequest;
import be.profacile.savefunds.api.dto.response.SituationFinanciereResponse;
import be.profacile.savefunds.api.mapper.SituationFinanciereMapper;
import be.profacile.savefunds.domain.entity.SituationFinanciere;
import be.profacile.savefunds.domain.service.SituationFinanciereService;
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
public class SituationFinanciereController {

    private final SituationFinanciereService situationService;
    private final SituationFinanciereMapper situationMapper;

    @GetMapping("/{id}")
    public ResponseEntity<SituationFinanciereResponse> getById(@PathVariable Long id) {
        return situationService.findById(id)
                .map(situationMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/entreprise/{entrepriseId}")
    public ResponseEntity<List<SituationFinanciereResponse>> getByEntrepriseId(
            @PathVariable Long entrepriseId) {
        List<SituationFinanciereResponse> responses = situationService
                .findByEntrepriseId(entrepriseId)
                .stream()
                .map(situationMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/entreprise/{entrepriseId}/last")
    public ResponseEntity<SituationFinanciereResponse> getLast(@PathVariable Long entrepriseId) {
        return ResponseEntity.ok(
                situationMapper.toResponse(situationService.findLastByEntrepriseId(entrepriseId))
        );
    }

    @PostMapping
    public ResponseEntity<SituationFinanciereResponse> create(
            @Valid @RequestBody CreateSituationFinanciereRequest request) {
        SituationFinanciere entity = situationMapper.toEntity(request);
        SituationFinanciere created = situationService.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(situationMapper.toResponse(created));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        situationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
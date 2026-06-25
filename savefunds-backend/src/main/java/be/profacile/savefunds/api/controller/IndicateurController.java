package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.exception.ResourceNotFoundException;
import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.service.EntrepriseService;
import be.profacile.savefunds.domain.service.IndicateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Controller pour les calculs d'indicateurs financiers.
 *
 * Deux types d'endpoints :
 * 1. Calculs unitaires (valeurs brutes passées en @RequestParam) — utiles pour le frontend
 *    afin de simuler sans créer d'analyse
 * 2. Calculs sur une entreprise existante (via entrepriseId) — sécurisé, pas d'entité exposée
 */
@RestController
@RequestMapping("/api/v1/indicateurs")
@RequiredArgsConstructor
@Tag(name = "Indicateurs", description = "Calculs d'indicateurs financiers")
public class IndicateurController {

    private final IndicateurService indicateurService;
    private final EntrepriseService entrepriseService;

    /**
     * Calcul unitaire du ratio CA/Charges.
     * Utile pour simulation frontend sans passer par une analyse.
     */
    @GetMapping("/ratio-ca-charges")
    @Operation(summary = "Calculer le ratio CA/Charges",
            description = "Calcule le ratio chiffre d'affaires / charges à partir de valeurs brutes")
    public ResponseEntity<BigDecimal> calculerRatioCACharges(
            @Parameter(description = "Chiffre d'affaires mensuel", example = "50000")
            @RequestParam BigDecimal chiffreAffaires,
            @Parameter(description = "Charges mensuelles", example = "30000")
            @RequestParam BigDecimal charges) {

        return ResponseEntity.ok(
                indicateurService.calculerRatioCACharges(chiffreAffaires, charges));
    }

    /**
     * Calcul unitaire de la trésorerie en mois.
     */
    @GetMapping("/tresorerie-en-mois")
    @Operation(summary = "Calculer la trésorerie en mois",
            description = "Calcule le nombre de mois de charges couverts par la trésorerie")
    public ResponseEntity<BigDecimal> calculerTresorerieEnMois(
            @Parameter(description = "Trésorerie disponible", example = "100000")
            @RequestParam BigDecimal tresorerie,
            @Parameter(description = "Charges mensuelles", example = "30000")
            @RequestParam BigDecimal chargesMensuelles) {

        return ResponseEntity.ok(
                indicateurService.calculerTresorerieEnMois(tresorerie, chargesMensuelles));
    }

    /**
     * Calcul unitaire de la durée du compte courant débiteur.
     */
    @GetMapping("/duree-compte-courant-debiteur")
    @Operation(summary = "Calculer la durée du compte courant débiteur",
            description = "Calcule le nombre de jours consécutifs en compte courant débiteur")
    public ResponseEntity<Integer> calculerDureeCompteCourantDebiteur(
            @Parameter(description = "Solde du compte courant", example = "-5000")
            @RequestParam BigDecimal soldeCompteCourant,
            @Parameter(description = "Date de début du débit (optionnel)", example = "2026-03-01")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebutDebiteur) {

        return ResponseEntity.ok(
                indicateurService.calculerDureeCompteCourantDebiteur(
                        soldeCompteCourant, dateDebutDebiteur));
    }

    /**
     * Calcul du montant maximum prélevable.
     */
    @GetMapping("/montant-maximum-prelevable")
    @Operation(summary = "Calculer le montant maximum prélevable",
            description = "Calcule le montant maximum qui peut être prélevé en respectant le seuil de sécurité")
    public ResponseEntity<BigDecimal> calculerMontantMaximumPrelevable(
            @Parameter(description = "Trésorerie disponible", example = "100000")
            @RequestParam BigDecimal tresorerie,
            @Parameter(description = "Charges mensuelles", example = "30000")
            @RequestParam BigDecimal chargesMensuelles,
            @Parameter(description = "Seuil de sécurité en mois", example = "3")
            @RequestParam(defaultValue = "3") int seuilSecurite) {

        return ResponseEntity.ok(
                indicateurService.calculerMontantMaximumPrelevable(
                        tresorerie, chargesMensuelles, seuilSecurite));
    }

    /**
     * Calcul de TOUS les indicateurs pour une entreprise existante.
     * Utilise entrepriseId — jamais l'entité directement.
     */
    @GetMapping("/entreprise/{entrepriseId}/tous")
    @Operation(summary = "Calculer tous les indicateurs d'une entreprise",
            description = "Calcule l'ensemble des indicateurs financiers pour une entreprise existante")
    public ResponseEntity<Map<String, Object>> calculerTousIndicateurs(
            @Parameter(description = "ID de l'entreprise")
            @PathVariable Long entrepriseId) {

        Entreprise entreprise = entrepriseService.findById(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Entreprise non trouvée avec l'ID: " + entrepriseId));

        return ResponseEntity.ok(indicateurService.calculerTousIndicateurs(entreprise));
    }

    /**
     * Vérifie si les données financières d'une entreprise sont complètes.
     */
    @GetMapping("/entreprise/{entrepriseId}/donnees-completes")
    @Operation(summary = "Vérifier la complétude des données",
            description = "Vérifie si toutes les données financières nécessaires sont renseignées")
    public ResponseEntity<Boolean> donneesCompletesPresentes(
            @Parameter(description = "ID de l'entreprise")
            @PathVariable Long entrepriseId) {

        Entreprise entreprise = entrepriseService.findById(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Entreprise non trouvée avec l'ID: " + entrepriseId));

        return ResponseEntity.ok(indicateurService.donneesCompletesPresentes(entreprise));
    }
}
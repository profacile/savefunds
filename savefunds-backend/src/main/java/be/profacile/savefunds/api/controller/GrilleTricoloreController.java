package be.profacile.savefunds.api.controller;

import be.profacile.savefunds.api.dto.response.DecisionResponse;
import be.profacile.savefunds.api.dto.response.GrilleTricoloreResponse;
import be.profacile.savefunds.api.dto.response.RecommandationResponse;
import be.profacile.savefunds.domain.enums.Decision;
import be.profacile.savefunds.domain.service.GrilleTricoloreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * REST Controller pour la visualisation et le test de la grille tricolore.
 *
 * Permet de :
 * - Consulter les seuils de décision configurés
 * - Tester une décision sur des valeurs données (utile pour le frontend)
 * - Générer des recommandations pour chaque critère
 *
 * Endpoints:
 * - GET /api/v1/grille : Récupérer tous les seuils
 * - GET /api/v1/grille/decision/tresorerie : Tester décision trésorerie
 * - GET /api/v1/grille/decision/ratio : Tester décision ratio CA/Charges
 * - GET /api/v1/grille/decision/compte-courant : Tester décision compte courant
 * - GET /api/v1/grille/decision/globale : Calculer décision globale
 * - GET /api/v1/grille/recommandation : Générer une recommandation
 */
@RestController
@RequestMapping("/api/v1/grille")
@RequiredArgsConstructor
@Tag(name = "Grille Tricolore", description = "Visualisation et test des seuils de décision")
public class GrilleTricoloreController {

    private final GrilleTricoloreService grilleService;

    /**
     * Récupérer tous les seuils de la grille tricolore.
     * Utile pour afficher les règles à l'utilisateur dans le frontend.
     */
    @GetMapping
    @Operation(summary = "Récupérer la grille tricolore complète",
            description = "Retourne tous les seuils de décision configurés (trésorerie, ratio, compte courant)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Grille récupérée avec succès")
    })
    public ResponseEntity<GrilleTricoloreResponse> recupererGrille() {

        GrilleTricoloreResponse response = GrilleTricoloreResponse.builder()
                .tresorerie(Map.of(
                        "VERT", "≥ 3 mois de charges",
                        "ORANGE", "1 à 3 mois de charges",
                        "ROUGE", "< 1 mois de charges"
                ))
                .ratioCACharges(Map.of(
                        "VERT", "≥ 1.3 (30% de marge)",
                        "ORANGE", "1.0 à 1.3 (0-30% de marge)",
                        "ROUGE", "< 1.0 (déficitaire)"
                ))
                .compteCourant(Map.of(
                        "VERT", "0 jour en débiteur",
                        "ORANGE", "1 à 30 jours en débiteur",
                        "ROUGE", "> 30 jours en débiteur"
                ))
                .decisionGlobale(Map.of(
                        "ROUGE", "Au moins 1 critère ROUGE",
                        "ORANGE", "Au moins 1 critère ORANGE (et aucun ROUGE)",
                        "VERT", "Tous les critères VERTS"
                ))
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Tester la décision pour la trésorerie.
     * Permet au frontend de simuler une décision avant de créer l'analyse.
     */
    @GetMapping("/decision/tresorerie")
    @Operation(summary = "Tester la décision trésorerie",
            description = "Calcule la décision (VERT/ORANGE/ROUGE) pour une valeur de trésorerie en mois")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Décision calculée avec succès"),
            @ApiResponse(responseCode = "400", description = "Valeur invalide")
    })
    public ResponseEntity<DecisionResponse> testerDecisionTresorerie(
            @Parameter(description = "Trésorerie en nombre de mois de charges", example = "2.5")
            @RequestParam BigDecimal tresorerieEnMois) {

        if (tresorerieEnMois == null || tresorerieEnMois.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La trésorerie en mois doit être positive");
        }

        Decision decision = grilleService.calculerDecisionTresorerie(tresorerieEnMois);
        String explication = getExplicationTresorerie(decision, tresorerieEnMois);

        DecisionResponse response = DecisionResponse.builder()
                .critere("TRESORERIE")
                .valeur(tresorerieEnMois.toString() + " mois")
                .decision(decision)
                .explication(explication)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Tester la décision pour le ratio CA/Charges.
     */
    @GetMapping("/decision/ratio")
    @Operation(summary = "Tester la décision ratio CA/Charges",
            description = "Calcule la décision (VERT/ORANGE/ROUGE) pour un ratio CA/Charges")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Décision calculée avec succès"),
            @ApiResponse(responseCode = "400", description = "Valeur invalide")
    })
    public ResponseEntity<DecisionResponse> testerDecisionRatio(
            @Parameter(description = "Ratio CA/Charges", example = "1.25")
            @RequestParam BigDecimal ratio) {

        if (ratio == null || ratio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le ratio doit être positif");
        }

        Decision decision = grilleService.calculerDecisionRatioCACharges(ratio);
        String explication = getExplicationRatio(decision, ratio);

        DecisionResponse response = DecisionResponse.builder()
                .critere("RATIO_CA_CHARGES")
                .valeur(ratio.toString())
                .decision(decision)
                .explication(explication)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Tester la décision pour le compte courant.
     */
    @GetMapping("/decision/compte-courant")
    @Operation(summary = "Tester la décision compte courant",
            description = "Calcule la décision (VERT/ORANGE/ROUGE) pour une durée en débiteur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Décision calculée avec succès"),
            @ApiResponse(responseCode = "400", description = "Valeur invalide")
    })
    public ResponseEntity<DecisionResponse> testerDecisionCompteCourant(
            @Parameter(description = "Nombre de jours en débiteur", example = "15")
            @RequestParam int dureeDebiteur) {

        if (dureeDebiteur < 0) {
            throw new IllegalArgumentException("La durée en débiteur doit être positive ou nulle");
        }

        Decision decision = grilleService.calculerDecisionCompteCourant(dureeDebiteur);
        String explication = getExplicationCompteCourant(decision, dureeDebiteur);

        DecisionResponse response = DecisionResponse.builder()
                .critere("COMPTE_COURANT")
                .valeur(dureeDebiteur + " jours")
                .decision(decision)
                .explication(explication)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Calculer la décision globale à partir de 3 décisions individuelles.
     * Utile pour simuler le résultat avant de créer l'analyse.
     */
    @GetMapping("/decision/globale")
    @Operation(summary = "Calculer la décision globale",
            description = "Calcule la décision finale à partir des 3 décisions (trésorerie, ratio, compte courant)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Décision globale calculée avec succès"),
            @ApiResponse(responseCode = "400", description = "Paramètres invalides")
    })
    public ResponseEntity<DecisionResponse> calculerDecisionGlobale(
            @Parameter(description = "Décision trésorerie", example = "VERT")
            @RequestParam Decision decisionTresorerie,
            @Parameter(description = "Décision ratio CA/Charges", example = "ORANGE")
            @RequestParam Decision decisionRatio,
            @Parameter(description = "Décision compte courant", example = "VERT")
            @RequestParam Decision decisionCompteCourant,
            @Parameter(description = "Décision montant souhaité", example = "VERT")
            @RequestParam Decision decisionMontant) {

        Decision decisionGlobale = grilleService.calculerDecisionGlobale(
                decisionTresorerie,
                decisionRatio,
                decisionCompteCourant,
                decisionMontant
        );

        String explication = getExplicationGlobale(
                decisionGlobale,
                decisionTresorerie,
                decisionRatio,
                decisionCompteCourant
        );

        DecisionResponse response = DecisionResponse.builder()
                .critere("DECISION_GLOBALE")
                .valeur(String.format("Tréso: %s, Ratio: %s, CC: %s",
                        decisionTresorerie, decisionRatio, decisionCompteCourant))
                .decision(decisionGlobale)
                .explication(explication)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Générer une recommandation pour un critère donné.
     */
    @GetMapping("/recommandation")
    @Operation(summary = "Générer une recommandation",
            description = "Génère un message de recommandation personnalisé pour un critère et une décision")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommandation générée avec succès"),
            @ApiResponse(responseCode = "400", description = "Paramètres invalides")
    })
    public ResponseEntity<RecommandationResponse> genererRecommandation(
            @Parameter(description = "Décision du critère", example = "ROUGE")
            @RequestParam Decision decision,
            @Parameter(description = "Critère concerné", example = "tresorerie")
            @RequestParam String critere) {

        if (critere == null || critere.isBlank()) {
            throw new IllegalArgumentException("Le critère ne peut pas être vide");
        }

        String recommandation = grilleService.genererRecommandation(decision, critere.toLowerCase());

        RecommandationResponse response = RecommandationResponse.builder()
                .critere(critere.toUpperCase())
                .decision(decision)
                .message(recommandation)
                .build();

        return ResponseEntity.ok(response);
    }

    // ==================== MÉTHODES PRIVÉES (EXPLICATIONS) ====================

    private String getExplicationTresorerie(Decision decision, BigDecimal valeur) {
        switch (decision) {
            case VERT:
                return String.format("Excellente trésorerie ! Vous avez %.2f mois de charges en réserve, bien au-dessus du seuil de sécurité (3 mois).",
                        valeur);
            case ORANGE:
                return String.format("Trésorerie acceptable mais limitée. Vous avez %.2f mois de charges. Visez au moins 3 mois pour plus de sécurité.",
                        valeur);
            case ROUGE:
                return String.format("⚠️ Trésorerie critique ! Seulement %.2f mois de charges disponibles. Risque de difficulté si imprévus.",
                        valeur);
            default:
                return "";
        }
    }

    private String getExplicationRatio(Decision decision, BigDecimal valeur) {
        BigDecimal marge = valeur.subtract(BigDecimal.ONE).multiply(new BigDecimal("100"));

        switch (decision) {
            case VERT:
                return String.format("Excellente rentabilité ! Votre marge est de %.1f%%, bien au-dessus du seuil minimum (30%%).",
                        marge);
            case ORANGE:
                return String.format("Rentabilité fragile. Votre marge est de %.1f%%. Améliorez votre efficacité ou vos prix.",
                        marge);
            case ROUGE:
                return String.format("⚠️ Situation déficitaire ! Vos charges dépassent vos revenus (marge: %.1f%%). Action urgente requise.",
                        marge);
            default:
                return "";
        }
    }

    private String getExplicationCompteCourant(Decision decision, int duree) {
        switch (decision) {
            case VERT:
                return "Parfait ! Votre compte courant est créditeur ou à l'équilibre. Aucun dépassement.";
            case ORANGE:
                return String.format("Compte courant débiteur depuis %d jours. Restez vigilant et remboursez rapidement.",
                        duree);
            case ROUGE:
                return String.format("⚠️ Compte courant débiteur depuis %d jours ! Risque de blocage bancaire. Régularisez au plus vite.",
                        duree);
            default:
                return "";
        }
    }

    private String getExplicationGlobale(Decision globale, Decision treso, Decision ratio, Decision cc) {
        long rouges = countDecisions(Decision.ROUGE, treso, ratio, cc);
        long oranges = countDecisions(Decision.ORANGE, treso, ratio, cc);

        switch (globale) {
            case VERT:
                return "✅ Situation financière saine ! Tous vos indicateurs sont au vert. Prélèvement autorisé.";
            case ORANGE:
                return String.format("⚠️ Situation nécessitant une vigilance. %d indicateur(s) en ORANGE. Prélèvement possible mais limité.",
                        oranges);
            case ROUGE:
                return String.format("🛑 Situation critique ! %d indicateur(s) en ROUGE. Prélèvement fortement déconseillé.",
                        rouges);
            default:
                return "";
        }
    }

    private long countDecisions(Decision target, Decision... decisions) {
        long count = 0;
        for (Decision d : decisions) {
            if (d == target) count++;
        }
        return count;
    }
}
package be.profacile.savefunds.service;

import be.profacile.savefunds.domain.enums.Decision;
import be.profacile.savefunds.domain.service.GrilleTricoloreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests du service GrilleTricoloreService
 * 
 * SFB-82 : Complété par Nganang
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("GrilleTricoloreService Tests")
class GrilleTricoloreServiceTest {
    
    @Autowired
    private GrilleTricoloreService grilleService;
    
    // ========== TESTS DÉCISION TRÉSORERIE ==========
    
    @Test
    @DisplayName("Devrait retourner VERT si trésorerie >= 3 mois")
    void shouldReturnVertWhenTresorerieAbove3Months() {
        // Given
        BigDecimal tresorerie = new BigDecimal("5.0"); // 5 mois
        
        // When
        Decision decision = grilleService.calculerDecisionTresorerie(tresorerie);
        
        // Then
        assertThat(decision).isEqualTo(Decision.VERT);
    }
    
    @Test
    @DisplayName("Devrait retourner VERT si trésorerie exactement 3 mois")
    void shouldReturnVertWhenTresorerieExactly3Months() {
        // Given
        BigDecimal tresorerie = new BigDecimal("3.0");
        
        // When
        Decision decision = grilleService.calculerDecisionTresorerie(tresorerie);
        
        // Then
        assertThat(decision).isEqualTo(Decision.VERT);
    }
    
    @Test
    @DisplayName("Devrait retourner ORANGE si trésorerie entre 1 et 3 mois")
    void shouldReturnOrangeWhenTresorerieBetween1And3Months() {
        // Given
        BigDecimal tresorerie = new BigDecimal("2.0");
        
        // When
        Decision decision = grilleService.calculerDecisionTresorerie(tresorerie);
        
        // Then
        assertThat(decision).isEqualTo(Decision.ORANGE);
    }
    
    @Test
    @DisplayName("Devrait retourner ORANGE si trésorerie exactement 1 mois")
    void shouldReturnOrangeWhenTresorerieExactly1Month() {
        // Given
        BigDecimal tresorerie = new BigDecimal("1.0");
        
        // When
        Decision decision = grilleService.calculerDecisionTresorerie(tresorerie);
        
        // Then
        assertThat(decision).isEqualTo(Decision.ORANGE);
    }
    
    @Test
    @DisplayName("Devrait retourner ROUGE si trésorerie < 1 mois")
    void shouldReturnRougeWhenTresorerieBelow1Month() {
        // Given
        BigDecimal tresorerie = new BigDecimal("0.5");
        
        // When
        Decision decision = grilleService.calculerDecisionTresorerie(tresorerie);
        
        // Then
        assertThat(decision).isEqualTo(Decision.ROUGE);
    }
    
    // ========== TESTS DÉCISION RATIO CA/CHARGES ==========
    
    @Test
    @DisplayName("Devrait retourner VERT si ratio >= 1.3")
    void shouldReturnVertWhenRatioAbove1_3() {
        // Given
        BigDecimal ratio = new BigDecimal("1.5");
        
        // When
        Decision decision = grilleService.calculerDecisionRatioCACharges(ratio);
        
        // Then
        assertThat(decision).isEqualTo(Decision.VERT);
    }
    
    @Test
    @DisplayName("Devrait retourner VERT si ratio exactement 1.3")
    void shouldReturnVertWhenRatioExactly1_3() {
        // Given
        BigDecimal ratio = new BigDecimal("1.3");
        
        // When
        Decision decision = grilleService.calculerDecisionRatioCACharges(ratio);
        
        // Then
        assertThat(decision).isEqualTo(Decision.VERT);
    }
    
    @Test
    @DisplayName("Devrait retourner ORANGE si ratio entre 1.0 et 1.3")
    void shouldReturnOrangeWhenRatioBetween1_0And1_3() {
        // Given
        BigDecimal ratio = new BigDecimal("1.15");
        
        // When
        Decision decision = grilleService.calculerDecisionRatioCACharges(ratio);
        
        // Then
        assertThat(decision).isEqualTo(Decision.ORANGE);
    }
    
    @Test
    @DisplayName("Devrait retourner ORANGE si ratio exactement 1.0")
    void shouldReturnOrangeWhenRatioExactly1_0() {
        // Given
        BigDecimal ratio = new BigDecimal("1.0");
        
        // When
        Decision decision = grilleService.calculerDecisionRatioCACharges(ratio);
        
        // Then
        assertThat(decision).isEqualTo(Decision.ORANGE);
    }
    
    @Test
    @DisplayName("Devrait retourner ROUGE si ratio < 1.0")
    void shouldReturnRougeWhenRatioBelow1_0() {
        // Given
        BigDecimal ratio = new BigDecimal("0.85");
        
        // When
        Decision decision = grilleService.calculerDecisionRatioCACharges(ratio);
        
        // Then
        assertThat(decision).isEqualTo(Decision.ROUGE);
    }
    
    // ========== TESTS DÉCISION COMPTE COURANT ==========
    
    @Test
    @DisplayName("Devrait retourner VERT si jamais débiteur (0 jours)")
    void shouldReturnVertWhenNeverDebiteur() {
        // Given
        int duree = 0;
        
        // When
        Decision decision = grilleService.calculerDecisionCompteCourant(duree);
        
        // Then
        assertThat(decision).isEqualTo(Decision.VERT);
    }
    
    @Test
    @DisplayName("Devrait retourner ORANGE si débiteur 1-30 jours")
    void shouldReturnOrangeWhenDebiteur1To30Days() {
        // Given
        int duree = 15;
        
        // When
        Decision decision = grilleService.calculerDecisionCompteCourant(duree);
        
        // Then
        assertThat(decision).isEqualTo(Decision.ORANGE);
    }
    
    @Test
    @DisplayName("Devrait retourner ORANGE si débiteur exactement 30 jours")
    void shouldReturnOrangeWhenDebiteurExactly30Days() {
        // Given
        int duree = 30;
        
        // When
        Decision decision = grilleService.calculerDecisionCompteCourant(duree);
        
        // Then
        assertThat(decision).isEqualTo(Decision.ORANGE);
    }
    
    @Test
    @DisplayName("Devrait retourner ROUGE si débiteur > 30 jours")
    void shouldReturnRougeWhenDebiteurAbove30Days() {
        // Given
        int duree = 45;
        
        // When
        Decision decision = grilleService.calculerDecisionCompteCourant(duree);
        
        // Then
        assertThat(decision).isEqualTo(Decision.ROUGE);
    }
    
    // ========== TESTS DÉCISION GLOBALE ==========

    @Test
    @DisplayName("Devrait retourner VERT si tous les critères sont VERT")
    void shouldReturnVertWhenAllCriteriaVert() {
        Decision decision = grilleService.calculerDecisionGlobale(
                Decision.VERT, Decision.VERT, Decision.VERT, Decision.VERT);

        assertThat(decision).isEqualTo(Decision.VERT);
    }

    @Test
    @DisplayName("Devrait retourner ORANGE si au moins 1 ORANGE (pas de ROUGE)")
    void shouldReturnOrangeWhenAtLeastOneOrange() {
        Decision decision = grilleService.calculerDecisionGlobale(
                Decision.VERT, Decision.ORANGE, Decision.VERT, Decision.VERT);

        assertThat(decision).isEqualTo(Decision.ORANGE);
    }

    @Test
    @DisplayName("Devrait retourner ROUGE si au moins 1 ROUGE")
    void shouldReturnRougeWhenAtLeastOneRouge() {
        Decision decision = grilleService.calculerDecisionGlobale(
                Decision.VERT, Decision.ORANGE, Decision.ROUGE, Decision.VERT);

        assertThat(decision).isEqualTo(Decision.ROUGE);
    }

    @Test
    @DisplayName("Devrait retourner ROUGE si plusieurs ROUGE")
    void shouldReturnRougeWhenMultipleRouge() {
        Decision decision = grilleService.calculerDecisionGlobale(
                Decision.ROUGE, Decision.ROUGE, Decision.VERT, Decision.VERT);

        assertThat(decision).isEqualTo(Decision.ROUGE);
    }

    @Test
    @DisplayName("Devrait retourner ROUGE si montant dépasse le prélevable")
    void shouldReturnRougeWhenMontantRouge() {
        Decision decision = grilleService.calculerDecisionGlobale(
                Decision.VERT, Decision.VERT, Decision.VERT, Decision.ROUGE);

        assertThat(decision).isEqualTo(Decision.ROUGE);
    }

    @Test
    @DisplayName("Devrait retourner ORANGE si montant en zone orange")
    void shouldReturnOrangeWhenMontantOrange() {
        Decision decision = grilleService.calculerDecisionGlobale(
                Decision.VERT, Decision.VERT, Decision.VERT, Decision.ORANGE);

        assertThat(decision).isEqualTo(Decision.ORANGE);
    }
    
    // ========== TESTS GÉNÉRATION RECOMMANDATIONS ==========
    
    @Test
    @DisplayName("Devrait générer recommandation pour trésorerie VERT")
    void shouldGenerateRecommendationForTresorerieVert() {
        // When
        String recommandation = grilleService.genererRecommandation(Decision.VERT, "tresorerie");
        
        // Then
        assertThat(recommandation).contains("excellente", "saine");
    }
    
    @Test
    @DisplayName("Devrait générer recommandation pour trésorerie ROUGE")
    void shouldGenerateRecommandationForTresorerieRouge() {
        // When
        String recommandation = grilleService.genererRecommandation(Decision.ROUGE, "tresorerie");
        
        // Then
        assertThat(recommandation).contains("critique", "déconseillé");
    }
    
    @Test
    @DisplayName("Devrait générer recommandation pour ratio ORANGE")
    void shouldGenerateRecommandationForRatioOrange() {
        // When
        String recommandation = grilleService.genererRecommandation(Decision.ORANGE, "ratio");
        
        // Then
        assertThat(recommandation).contains("serré", "Attention");
    }
    
    @Test
    @DisplayName("Devrait générer recommandation pour compte courant ROUGE")
    void shouldGenerateRecommandationForCompteCourantRouge() {
        // When
        String recommandation = grilleService.genererRecommandation(Decision.ROUGE, "compte_courant");
        
        // Then
        assertThat(recommandation).contains("trop longtemps", "Risque");
    }
    
    // ========== TESTS VALIDATION ==========
    
    @Test
    @DisplayName("Devrait lancer exception si trésorerie null")
    void shouldThrowExceptionWhenTresorerieNull() {
        // When / Then
        assertThatThrownBy(() -> grilleService.calculerDecisionTresorerie(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("trésorerie");
    }
    
    @Test
    @DisplayName("Devrait lancer exception si ratio null")
    void shouldThrowExceptionWhenRatioNull() {
        // When / Then
        assertThatThrownBy(() -> grilleService.calculerDecisionRatioCACharges(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ratio");
    }
}
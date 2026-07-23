package be.profacile.savefunds.service;

import be.profacile.savefunds.domain.enums.Decision;
import be.profacile.savefunds.domain.service.TrafficLightDecisionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests du service TrafficLightDecisionService
 * 
 * SFB-82 : ComplÃ©tÃ© par Nganang
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("TrafficLightDecisionService Tests")
class TrafficLightDecisionServiceTest {
    
    @Autowired
    private TrafficLightDecisionService trafficLightDecisionService;
    
    // ========== TESTS DÃ‰CISION TRÃ‰SORERIE ==========
    
    @Test
    @DisplayName("Devrait retourner VERT si trÃ©sorerie >= 3 mois")
    void shouldReturnVertWhenTresorerieAbove3Months() {
        // Given
        BigDecimal cashBalance = new BigDecimal("5.0"); // 5 mois
        
        // When
        Decision decision = trafficLightDecisionService.calculateCashDecision(cashBalance);
        
        // Then
        assertThat(decision).isEqualTo(Decision.VERT);
    }
    
    @Test
    @DisplayName("Devrait retourner VERT si trÃ©sorerie exactement 3 mois")
    void shouldReturnVertWhenTresorerieExactly3Months() {
        // Given
        BigDecimal cashBalance = new BigDecimal("3.0");
        
        // When
        Decision decision = trafficLightDecisionService.calculateCashDecision(cashBalance);
        
        // Then
        assertThat(decision).isEqualTo(Decision.VERT);
    }
    
    @Test
    @DisplayName("Devrait retourner ORANGE si trÃ©sorerie entre 1 et 3 mois")
    void shouldReturnOrangeWhenTresorerieBetween1And3Months() {
        // Given
        BigDecimal cashBalance = new BigDecimal("2.0");
        
        // When
        Decision decision = trafficLightDecisionService.calculateCashDecision(cashBalance);
        
        // Then
        assertThat(decision).isEqualTo(Decision.ORANGE);
    }
    
    @Test
    @DisplayName("Devrait retourner ORANGE si trÃ©sorerie exactement 1 mois")
    void shouldReturnOrangeWhenTresorerieExactly1Month() {
        // Given
        BigDecimal cashBalance = new BigDecimal("1.0");
        
        // When
        Decision decision = trafficLightDecisionService.calculateCashDecision(cashBalance);
        
        // Then
        assertThat(decision).isEqualTo(Decision.ORANGE);
    }
    
    @Test
    @DisplayName("Devrait retourner ROUGE si trÃ©sorerie < 1 mois")
    void shouldReturnRougeWhenTresorerieBelow1Month() {
        // Given
        BigDecimal cashBalance = new BigDecimal("0.5");
        
        // When
        Decision decision = trafficLightDecisionService.calculateCashDecision(cashBalance);
        
        // Then
        assertThat(decision).isEqualTo(Decision.ROUGE);
    }
    
    // ========== TESTS DÃ‰CISION RATIO CA/CHARGES ==========
    
    @Test
    @DisplayName("Devrait retourner VERT si ratio >= 1.3")
    void shouldReturnVertWhenRatioAbove1_3() {
        // Given
        BigDecimal ratio = new BigDecimal("1.5");
        
        // When
        Decision decision = trafficLightDecisionService.calculateRevenueExpensesRatioDecision(ratio);
        
        // Then
        assertThat(decision).isEqualTo(Decision.VERT);
    }
    
    @Test
    @DisplayName("Devrait retourner VERT si ratio exactement 1.3")
    void shouldReturnVertWhenRatioExactly1_3() {
        // Given
        BigDecimal ratio = new BigDecimal("1.3");
        
        // When
        Decision decision = trafficLightDecisionService.calculateRevenueExpensesRatioDecision(ratio);
        
        // Then
        assertThat(decision).isEqualTo(Decision.VERT);
    }
    
    @Test
    @DisplayName("Devrait retourner ORANGE si ratio entre 1.0 et 1.3")
    void shouldReturnOrangeWhenRatioBetween1_0And1_3() {
        // Given
        BigDecimal ratio = new BigDecimal("1.15");
        
        // When
        Decision decision = trafficLightDecisionService.calculateRevenueExpensesRatioDecision(ratio);
        
        // Then
        assertThat(decision).isEqualTo(Decision.ORANGE);
    }
    
    @Test
    @DisplayName("Devrait retourner ORANGE si ratio exactement 1.0")
    void shouldReturnOrangeWhenRatioExactly1_0() {
        // Given
        BigDecimal ratio = new BigDecimal("1.0");
        
        // When
        Decision decision = trafficLightDecisionService.calculateRevenueExpensesRatioDecision(ratio);
        
        // Then
        assertThat(decision).isEqualTo(Decision.ORANGE);
    }
    
    @Test
    @DisplayName("Devrait retourner ROUGE si ratio < 1.0")
    void shouldReturnRougeWhenRatioBelow1_0() {
        // Given
        BigDecimal ratio = new BigDecimal("0.85");
        
        // When
        Decision decision = trafficLightDecisionService.calculateRevenueExpensesRatioDecision(ratio);
        
        // Then
        assertThat(decision).isEqualTo(Decision.ROUGE);
    }
    
    // ========== TESTS DÃ‰CISION COMPTE COURANT ==========
    
    @Test
    @DisplayName("Devrait retourner VERT si jamais dÃ©biteur (0 jours)")
    void shouldReturnVertWhenNeverDebiteur() {
        // Given
        int duree = 0;
        
        // When
        Decision decision = trafficLightDecisionService.calculateDirectorCurrentAccountDecision(duree);
        
        // Then
        assertThat(decision).isEqualTo(Decision.VERT);
    }
    
    @Test
    @DisplayName("Devrait retourner ORANGE si dÃ©biteur 1-30 jours")
    void shouldReturnOrangeWhenDebiteur1To30Days() {
        // Given
        int duree = 15;
        
        // When
        Decision decision = trafficLightDecisionService.calculateDirectorCurrentAccountDecision(duree);
        
        // Then
        assertThat(decision).isEqualTo(Decision.ORANGE);
    }
    
    @Test
    @DisplayName("Devrait retourner ORANGE si dÃ©biteur exactement 30 jours")
    void shouldReturnOrangeWhenDebiteurExactly30Days() {
        // Given
        int duree = 30;
        
        // When
        Decision decision = trafficLightDecisionService.calculateDirectorCurrentAccountDecision(duree);
        
        // Then
        assertThat(decision).isEqualTo(Decision.ORANGE);
    }
    
    @Test
    @DisplayName("Devrait retourner ROUGE si dÃ©biteur > 30 jours")
    void shouldReturnRougeWhenDebiteurAbove30Days() {
        // Given
        int duree = 45;
        
        // When
        Decision decision = trafficLightDecisionService.calculateDirectorCurrentAccountDecision(duree);
        
        // Then
        assertThat(decision).isEqualTo(Decision.ROUGE);
    }
    
    // ========== TESTS DÃ‰CISION GLOBALE ==========

    @Test
    @DisplayName("Devrait retourner VERT si tous les critÃ¨res sont VERT")
    void shouldReturnVertWhenAllCriteriaVert() {
        Decision decision = trafficLightDecisionService.calculateGlobalDecision(
                Decision.VERT, Decision.VERT, Decision.VERT, Decision.VERT);

        assertThat(decision).isEqualTo(Decision.VERT);
    }

    @Test
    @DisplayName("Devrait retourner ORANGE si au moins 1 ORANGE (pas de ROUGE)")
    void shouldReturnOrangeWhenAtLeastOneOrange() {
        Decision decision = trafficLightDecisionService.calculateGlobalDecision(
                Decision.VERT, Decision.ORANGE, Decision.VERT, Decision.VERT);

        assertThat(decision).isEqualTo(Decision.ORANGE);
    }

    @Test
    @DisplayName("Devrait retourner ROUGE si au moins 1 ROUGE")
    void shouldReturnRougeWhenAtLeastOneRouge() {
        Decision decision = trafficLightDecisionService.calculateGlobalDecision(
                Decision.VERT, Decision.ORANGE, Decision.ROUGE, Decision.VERT);

        assertThat(decision).isEqualTo(Decision.ROUGE);
    }

    @Test
    @DisplayName("Devrait retourner ROUGE si plusieurs ROUGE")
    void shouldReturnRougeWhenMultipleRouge() {
        Decision decision = trafficLightDecisionService.calculateGlobalDecision(
                Decision.ROUGE, Decision.ROUGE, Decision.VERT, Decision.VERT);

        assertThat(decision).isEqualTo(Decision.ROUGE);
    }

    @Test
    @DisplayName("Devrait retourner ROUGE si montant dÃ©passe le prÃ©levable")
    void shouldReturnRougeWhenMontantRouge() {
        Decision decision = trafficLightDecisionService.calculateGlobalDecision(
                Decision.VERT, Decision.VERT, Decision.VERT, Decision.ROUGE);

        assertThat(decision).isEqualTo(Decision.ROUGE);
    }

    @Test
    @DisplayName("Devrait retourner ORANGE si montant en zone orange")
    void shouldReturnOrangeWhenMontantOrange() {
        Decision decision = trafficLightDecisionService.calculateGlobalDecision(
                Decision.VERT, Decision.VERT, Decision.VERT, Decision.ORANGE);

        assertThat(decision).isEqualTo(Decision.ORANGE);
    }
    
    // ========== TESTS GÃ‰NÃ‰RATION RECOMMANDATIONS ==========
    
    @Test
    @DisplayName("Devrait gÃ©nÃ©rer recommandation pour trÃ©sorerie VERT")
    void shouldGenerateRecommendationForTresorerieVert() {
        // When
        String recommandation = trafficLightDecisionService.generateRecommendation(Decision.VERT, "cashBalance");
        
        // Then
        assertThat(recommandation).contains("excellente", "saine");
    }
    
    @Test
    @DisplayName("Devrait gÃ©nÃ©rer recommandation pour trÃ©sorerie ROUGE")
    void shouldGenerateRecommandationForTresorerieRouge() {
        // When
        String recommandation = trafficLightDecisionService.generateRecommendation(Decision.ROUGE, "cashBalance");
        
        // Then
        assertThat(recommandation).contains("critique");
    }
    
    @Test
    @DisplayName("Devrait gÃ©nÃ©rer recommandation pour ratio ORANGE")
    void shouldGenerateRecommandationForRatioOrange() {
        // When
        String recommandation = trafficLightDecisionService.generateRecommendation(Decision.ORANGE, "ratio");
        
        // Then
        assertThat(recommandation).contains("Attention");
    }
    
    @Test
    @DisplayName("Devrait gÃ©nÃ©rer recommandation pour compte courant ROUGE")
    void shouldGenerateRecommandationForCompteCourantRouge() {
        // When
        String recommandation = trafficLightDecisionService.generateRecommendation(Decision.ROUGE, "compte_courant");
        
        // Then
        assertThat(recommandation).contains("trop longtemps", "Risque");
    }
    
    // ========== TESTS VALIDATION ==========
    
    @Test
    @DisplayName("Devrait lancer exception si trÃ©sorerie null")
    void shouldThrowExceptionWhenTresorerieNull() {
        // When / Then
        assertThatThrownBy(() -> trafficLightDecisionService.calculateCashDecision(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("sorerie");
    }
    
    @Test
    @DisplayName("Devrait lancer exception si ratio null")
    void shouldThrowExceptionWhenRatioNull() {
        // When / Then
        assertThatThrownBy(() -> trafficLightDecisionService.calculateRevenueExpensesRatioDecision(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ratio");
    }
}

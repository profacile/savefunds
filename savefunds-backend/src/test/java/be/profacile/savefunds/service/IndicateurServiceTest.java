package be.profacile.savefunds.service;

import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.service.IndicateurService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests du service IndicateurService
 *
 * SFB-94 : Complété
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("IndicateurService Tests")
class IndicateurServiceTest {

    @Autowired
    private IndicateurService indicateurService;

    @Test
    @DisplayName("Devrait calculer le ratio CA/Charges")
    void shouldCalculateRatioCACharges() {
        // Given
        BigDecimal ca = new BigDecimal("50000.00");
        BigDecimal charges = new BigDecimal("30000.00");

        // When
        BigDecimal ratio = indicateurService.calculerRatioCACharges(ca, charges);

        // Then
        assertThat(ratio).isEqualByComparingTo(new BigDecimal("1.67"));
    }

    @Test
    @DisplayName("Devrait lancer exception si charges = 0")
    void shouldThrowExceptionWhenChargesIsZero() {
        // Given
        BigDecimal ca = new BigDecimal("50000.00");
        BigDecimal charges = BigDecimal.ZERO;

        // When / Then
        assertThatThrownBy(() -> indicateurService.calculerRatioCACharges(ca, charges))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("charges doivent être strictement positives");
    }

    @Test
    @DisplayName("Devrait calculer la trésorerie en mois")
    void shouldCalculateTresorerieEnMois() {
        // Given
        BigDecimal treso = new BigDecimal("100000.00");
        BigDecimal charges = new BigDecimal("30000.00");

        // When
        BigDecimal mois = indicateurService.calculerTresorerieEnMois(treso, charges);

        // Then
        assertThat(mois).isEqualByComparingTo(new BigDecimal("3.33"));
    }

    @Test
    @DisplayName("Devrait calculer la durée compte courant débiteur")
    void shouldCalculateDureeCompteCourantDebiteur() {
        // Given
        BigDecimal solde = new BigDecimal("-5000.00");
        LocalDate dateDebut = LocalDate.now().minusDays(15);

        // When
        int duree = indicateurService.calculerDureeCompteCourantDebiteur(solde, dateDebut);

        // Then
        assertThat(duree).isEqualTo(15);
    }

    @Test
    @DisplayName("Devrait retourner 0 si compte non débiteur")
    void shouldReturnZeroWhenNotDebiteur() {
        // Given
        BigDecimal solde = new BigDecimal("5000.00");
        LocalDate dateDebut = LocalDate.now().minusDays(15);

        // When
        int duree = indicateurService.calculerDureeCompteCourantDebiteur(solde, dateDebut);

        // Then
        assertThat(duree).isEqualTo(0);
    }

    @Test
    @DisplayName("Devrait calculer le montant max prélevable")
    void shouldCalculateMontantMaxPrelevable() {
        // Given
        BigDecimal treso = new BigDecimal("100000.00");
        BigDecimal charges = new BigDecimal("30000.00");
        int seuil = 3; // 3 mois

        // When
        BigDecimal montantMax = indicateurService.calculerMontantMaximumPrelevable(treso, charges, seuil);

        // Then
        // 100000 - (30000 * 3) = 10000
        assertThat(montantMax).isEqualByComparingTo(new BigDecimal("10000.00"));
    }

    @Test
    @DisplayName("Devrait retourner 0 si trésorerie insuffisante")
    void shouldReturnZeroWhenInsufficientTresorerie() {
        // Given
        BigDecimal treso = new BigDecimal("50000.00");
        BigDecimal charges = new BigDecimal("30000.00");
        int seuil = 3;

        // When
        BigDecimal montantMax = indicateurService.calculerMontantMaximumPrelevable(treso, charges, seuil);

        // Then
        // 50000 - (30000 * 3) = -40000 → retourne 0
        assertThat(montantMax).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Devrait valider que les données sont complètes")
    void shouldValidateCompleteData() {
        // Given
        Entreprise entreprise = new Entreprise();
        entreprise.setChiffreAffairesMensuel(new BigDecimal("50000"));
        entreprise.setChargesMensuelles(new BigDecimal("30000"));
        entreprise.setTresorerie(new BigDecimal("100000"));
        entreprise.setSoldeCompteCourant(new BigDecimal("5000"));

        // When
        boolean complete = indicateurService.donneesCompletesPresentes(entreprise);

        // Then
        assertThat(complete).isTrue();
    }

    @Test
    @DisplayName("Devrait détecter données incomplètes")
    void shouldDetectIncompleteData() {
        // Given
        Entreprise entreprise = new Entreprise();
        entreprise.setChargesMensuelles(new BigDecimal("30000"));
        // Manque : CA, trésorerie, compte courant

        // When
        boolean complete = indicateurService.donneesCompletesPresentes(entreprise);

        // Then
        assertThat(complete).isFalse();
    }

    @Test
    @DisplayName("Devrait calculer tous les indicateurs")
    void shouldCalculateAllIndicators() {
        // Given
        Entreprise entreprise = new Entreprise();
        entreprise.setId(1L);
        entreprise.setChiffreAffairesMensuel(new BigDecimal("50000"));
        entreprise.setChargesMensuelles(new BigDecimal("30000"));
        entreprise.setTresorerie(new BigDecimal("100000"));
        entreprise.setSoldeCompteCourant(new BigDecimal("5000"));

        // When
        Map<String, Object> indicateurs = indicateurService.calculerTousIndicateurs(entreprise);

        // Then
        assertThat(indicateurs).isNotNull();
        assertThat(indicateurs).containsKeys(
                "ratioCACharges",
                "tresorerieEnMois",
                "dureeCompteCourantDebiteur",
                "montantMaxPrelevable"
        );
    }
}
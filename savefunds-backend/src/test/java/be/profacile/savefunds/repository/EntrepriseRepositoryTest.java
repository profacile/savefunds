package be.profacile.savefunds.repository;

import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.enums.StatutEntreprise;
import be.profacile.savefunds.domain.repository.EntrepriseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests du repository EntrepriseRepository
 *
 * SFB-67 : Complété par Wilfred Tiwa
 */
@DataJpaTest
@ActiveProfiles("test")
class EntrepriseRepositoryTest {

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @Test
    void shouldSaveEntreprise() {
        // Given
        Entreprise entreprise = new Entreprise();

        // Informations de base
        entreprise.setUserId(1L);
        entreprise.setRaisonSociale("Tech Solutions SPRL");
        entreprise.setNumeroEntreprise("BE0123456789");
        entreprise.setFormeJuridique("SPRL");
        entreprise.setSecteurActivite("Informatique");

        // Données financières
        entreprise.setChiffreAffairesMensuel(new BigDecimal("50000.00"));
        entreprise.setChargesMensuelles(new BigDecimal("30000.00"));
        entreprise.setTresorerie(new BigDecimal("100000.00"));
        entreprise.setSoldeCompteCourant(new BigDecimal("5000.00"));

        // Statut
        entreprise.setStatut(StatutEntreprise.ACTIVE);

        // When
        Entreprise saved = entrepriseRepository.save(entreprise);

        // Then
        // Vérifier que l'ID a été généré
        assertThat(saved.getId()).isNotNull();

        // Vérifier les données de base
        assertThat(saved.getRaisonSociale()).isEqualTo("Tech Solutions SPRL");
        assertThat(saved.getNumeroEntreprise()).isEqualTo("BE0123456789");
        assertThat(saved.getUserId()).isEqualTo(1L);

        // Vérifier le statut
        assertThat(saved.getStatut()).isEqualTo(StatutEntreprise.ACTIVE);

        // Vérifier les données financières
        assertThat(saved.getChiffreAffairesMensuel()).isEqualByComparingTo("50000.00");
        assertThat(saved.getChargesMensuelles()).isEqualByComparingTo("30000.00");
        assertThat(saved.getTresorerie()).isEqualByComparingTo("100000.00");
    }

    @Test
    void shouldFindByUserId() {
        // Given
        Entreprise entreprise = new Entreprise();
        entreprise.setUserId(1L);
        entreprise.setRaisonSociale("Consulting SA");
        entreprise.setNumeroEntreprise("BE0987654321");
        entreprise.setChiffreAffairesMensuel(new BigDecimal("40000.00"));
        entreprise.setChargesMensuelles(new BigDecimal("25000.00"));
        entreprise.setTresorerie(new BigDecimal("80000.00"));
        entreprise.setSoldeCompteCourant(new BigDecimal("3000.00"));
        entreprise.setStatut(StatutEntreprise.ACTIVE);

        // Sauvegarder l'entreprise
        entrepriseRepository.save(entreprise);

        // When
        Optional<Entreprise> found = entrepriseRepository.findByUserId(1L);

        // Then
        // Vérifier que l'entreprise a été trouvée
        assertThat(found).isPresent();

        // Vérifier que le userId correspond
        assertThat(found.get().getUserId()).isEqualTo(1L);

        // Vérifier la raison sociale
        assertThat(found.get().getRaisonSociale()).isEqualTo("Consulting SA");
    }

    @Test
    void shouldReturnEmptyWhenUserIdNotFound() {
        // When
        // Rechercher un userId qui n'existe pas
        Optional<Entreprise> found = entrepriseRepository.findByUserId(999L);

        // Then
        // Vérifier que le résultat est vide
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindById() {
        // Given
        Entreprise entreprise = new Entreprise();
        entreprise.setUserId(2L);
        entreprise.setRaisonSociale("Design Studio SCRL");
        entreprise.setNumeroEntreprise("BE0555666777");
        entreprise.setChiffreAffairesMensuel(new BigDecimal("35000.00"));
        entreprise.setChargesMensuelles(new BigDecimal("20000.00"));
        entreprise.setTresorerie(new BigDecimal("70000.00"));
        entreprise.setSoldeCompteCourant(new BigDecimal("2000.00"));
        entreprise.setStatut(StatutEntreprise.ACTIVE);

        // Sauvegarder et récupérer l'ID généré
        Entreprise saved = entrepriseRepository.save(entreprise);
        Long entrepriseId = saved.getId();

        // When
        Optional<Entreprise> found = entrepriseRepository.findById(entrepriseId);

        // Then
        // Vérifier que l'entreprise a été trouvée
        assertThat(found).isPresent();

        // Vérifier que l'ID correspond
        assertThat(found.get().getId()).isEqualTo(entrepriseId);

        // Vérifier la raison sociale
        assertThat(found.get().getRaisonSociale()).isEqualTo("Design Studio SCRL");
    }

    @Test
    void shouldStoreAllFinancialData() {
        // Given
        Entreprise entreprise = new Entreprise();
        entreprise.setUserId(3L);
        entreprise.setRaisonSociale("Finance Corp");
        entreprise.setNumeroEntreprise("BE0111222333");

        // Définir toutes les données financières
        entreprise.setChiffreAffairesMensuel(new BigDecimal("75000.50"));
        entreprise.setChargesMensuelles(new BigDecimal("45000.25"));
        entreprise.setTresorerie(new BigDecimal("150000.75"));
        entreprise.setSoldeCompteCourant(new BigDecimal("-2500.00")); // Compte débiteur

        entreprise.setStatut(StatutEntreprise.EN_VIGILANCE);

        // When
        Entreprise saved = entrepriseRepository.save(entreprise);

        // Then
        // Vérifier que toutes les données financières sont correctement sauvegardées
        assertThat(saved.getChiffreAffairesMensuel()).isEqualByComparingTo("75000.50");
        assertThat(saved.getChargesMensuelles()).isEqualByComparingTo("45000.25");
        assertThat(saved.getTresorerie()).isEqualByComparingTo("150000.75");
        assertThat(saved.getSoldeCompteCourant()).isEqualByComparingTo("-2500.00");

        // Vérifier le statut
        assertThat(saved.getStatut()).isEqualTo(StatutEntreprise.EN_VIGILANCE);
    }

    @Test
    void shouldStoreEntrepriseWithOptionalFields() {
        // Given
        Entreprise entreprise = new Entreprise();
        entreprise.setUserId(4L);
        entreprise.setRaisonSociale("Startup Innov");
        entreprise.setNumeroEntreprise("BE0444555666");

        // Champs optionnels remplis
        entreprise.setFormeJuridique("SRL");
        entreprise.setSecteurActivite("Technologies");

        // Données financières
        entreprise.setChiffreAffairesMensuel(new BigDecimal("20000.00"));
        entreprise.setChargesMensuelles(new BigDecimal("15000.00"));
        entreprise.setTresorerie(new BigDecimal("50000.00"));
        entreprise.setSoldeCompteCourant(new BigDecimal("1000.00"));

        entreprise.setStatut(StatutEntreprise.ACTIVE);

        // When
        Entreprise saved = entrepriseRepository.save(entreprise);

        // Then
        // Vérifier que les champs optionnels sont bien sauvegardés
        assertThat(saved.getFormeJuridique()).isEqualTo("SRL");
        assertThat(saved.getSecteurActivite()).isEqualTo("Technologies");
    }
}
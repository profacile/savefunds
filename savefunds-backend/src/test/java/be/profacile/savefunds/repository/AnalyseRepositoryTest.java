package be.profacile.savefunds.repository;

import be.profacile.savefunds.domain.entity.AnalysePrelevement;
import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.enums.StatutAnalyse;
import be.profacile.savefunds.domain.enums.StatutEntreprise;
import be.profacile.savefunds.domain.repository.AnalyseRepository;
import be.profacile.savefunds.domain.repository.EntrepriseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

@DataJpaTest
@ActiveProfiles("test")
class AnalyseRepositoryTest {

    @Autowired
    private AnalyseRepository analyseRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    private Entreprise entreprise;

    @BeforeEach
    void setUp() {
        Entreprise e = new Entreprise();
        e.setUserId(1L);
        e.setRaisonSociale("Tech Solutions SPRL");
        e.setNumeroEntreprise("BE0123456789");
        e.setFormeJuridique("SPRL");
        e.setSecteurActivite("Informatique");
        e.setChiffreAffairesMensuel(new BigDecimal("50000.00"));
        e.setChargesMensuelles(new BigDecimal("30000.00"));
        e.setTresorerie(new BigDecimal("100000.00"));
        e.setSoldeCompteCourant(new BigDecimal("5000.00"));
        e.setStatut(StatutEntreprise.ACTIVE);

        this.entreprise = entrepriseRepository.save(e);  // ✅ champ de classe
    }

    @Test
    @DisplayName("Devrait sauvegarder une analyse")
    void shouldSaveAnalysePrelevement() {
        AnalysePrelevement analyse = AnalysePrelevement.builder()
                .entreprise(entreprise)
                .montantSouhaite(new BigDecimal("1500.00"))
                .statut(StatutAnalyse.EN_ATTENTE)
                .build();

        AnalysePrelevement saved = analyseRepository.save(analyse);

        assertNotNull(saved.getId());
        assertEquals(entreprise.getId(), saved.getEntreprise().getId());
        assertEquals(new BigDecimal("1500.00"), saved.getMontantSouhaite());
        assertEquals(StatutAnalyse.EN_ATTENTE, saved.getStatut());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    @DisplayName("Devrait trouver les analyses d'une entreprise triées par date desc")
    void shouldFindByEntreprise_IdOrderByCreatedAtDesc() {
        analyseRepository.save(AnalysePrelevement.builder()
                .entreprise(entreprise)
                .montantSouhaite(new BigDecimal("100.00"))
                .statut(StatutAnalyse.EN_ATTENTE)
                .build());

        analyseRepository.save(AnalysePrelevement.builder()
                .entreprise(entreprise)
                .montantSouhaite(new BigDecimal("200.00"))
                .statut(StatutAnalyse.EN_ATTENTE)
                .build());

        analyseRepository.save(AnalysePrelevement.builder()
                .entreprise(entreprise)
                .montantSouhaite(new BigDecimal("300.00"))
                .statut(StatutAnalyse.EN_ATTENTE)
                .build());

        List<AnalysePrelevement> result =
                analyseRepository.findByEntreprise_IdOrderByCreatedAtDesc(entreprise.getId()); // ✅ underscore

        assertNotNull(result);
        assertEquals(3, result.size());
        result.forEach(a -> assertEquals(entreprise.getId(), a.getEntreprise().getId()));
    }

    @Test
    @DisplayName("Devrait retourner une liste vide si aucune analyse")
    void shouldReturnEmptyListWhenNoAnalyseExists() {
        List<AnalysePrelevement> result =
                analyseRepository.findByEntreprise_IdOrderByCreatedAtDesc(999L); // ✅ underscore

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
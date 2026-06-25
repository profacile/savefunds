package be.profacile.savefunds.repository;

import be.profacile.savefunds.domain.entity.SituationFinanciere;
import be.profacile.savefunds.domain.repository.SituationFinanciereRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests du repository SituationFinanciereRepository
 *
 */
@DataJpaTest
@ActiveProfiles("test")
class SituationFinanciereRepositoryTest {
    
    @Autowired
    private SituationFinanciereRepository situationRepository;
    
    @Test
    void shouldSaveSituation() {
        // Given
        SituationFinanciere situation = new SituationFinanciere();
        // TODO : Définir les propriétés
        // entrepriseId, chiffreAffairesMensuel ("50000.00"), chargesMensuelles ("30000.00")
        // tresorerie ("100000.00"), soldeCompteCourant ("20000.00")
        // ratioCACharges ("1.67"), tresorerieEnMois ("3.33"), dureeCompteCourantDebiteur (0)
        // source ("MANUEL"), notes
        
        // When
        // TODO : Sauvegarder
        
        // Then
        // TODO : Vérifier que l'ID n'est pas null
        // TODO : Vérifier entrepriseId
        // TODO : Vérifier chiffreAffairesMensuel avec isEqualByComparingTo
        // TODO : Vérifier que capturedAt n'est pas null
        // TODO : Vérifier la source
    }
    
    @Test
    void shouldFindByEntrepriseIdOrderByCapturedAtDesc() {
        // Given
        SituationFinanciere situation1 = new SituationFinanciere();
        // TODO : Créer situation1 avec entrepriseId = 1L, CA = "40000.00"
        // TODO : Sauvegarder
        
        // Pause pour ordre chronologique
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        
        SituationFinanciere situation2 = new SituationFinanciere();
        // TODO : Créer situation2 avec entrepriseId = 1L, CA = "60000.00"
        // TODO : Sauvegarder
        
        // When
        // TODO : Appeler findByEntrepriseIdOrderByCapturedAtDesc(1L)
        
        // Then
        // TODO : Vérifier que la liste contient 2 éléments
        // TODO : Vérifier que le premier est situation2 (plus récent = 60000)
        // TODO : Vérifier que le second est situation1 (plus ancien = 40000)
    }
    
    @Test
    void shouldFindFirstByEntrepriseIdOrderByCapturedAtDesc() {
        // Given
        SituationFinanciere ancienne = new SituationFinanciere();
        // TODO : Créer avec entrepriseId = 1L, CA = "40000.00", source = "MANUEL"
        // TODO : Sauvegarder
        
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        
        SituationFinanciere recente = new SituationFinanciere();
        // TODO : Créer avec entrepriseId = 1L, CA = "60000.00", source = "API"
        // TODO : Sauvegarder
        
        // When
        // TODO : Appeler findFirstByEntrepriseIdOrderByCapturedAtDesc(1L)
        
        // Then
        // TODO : Vérifier que ce n'est pas null
        // TODO : Vérifier que CA = "60000.00" (la plus récente)
        // TODO : Vérifier que source = "API"
    }
    
    @Test
    void shouldReturnEmptyListWhenNoSituationsForEntreprise() {
        // Given
        // TODO : Créer et sauvegarder une situation avec entrepriseId = 1L
        
        // When
        // TODO : Appeler findByEntrepriseIdOrderByCapturedAtDesc(999L)
        
        // Then
        // TODO : Vérifier que la liste est vide
    }
    
    @Test
    void shouldHandleDifferentSources() {
        // Given
        SituationFinanciere manuel = new SituationFinanciere();
        // TODO : Créer avec source = "MANUEL"
        
        SituationFinanciere api = new SituationFinanciere();
        // TODO : Créer avec source = "API"
        
        SituationFinanciere importFile = new SituationFinanciere();
        // TODO : Créer avec source = "IMPORT"
        
        // When
        // TODO : Sauvegarder les trois
        
        // Then
        // TODO : Vérifier que les sources sont correctement sauvegardées
    }
}
package be.profacile.savefunds.repository;

import be.profacile.savefunds.domain.entity.FinancialSituation;
import be.profacile.savefunds.domain.repository.FinancialSituationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests du repository FinancialSituationRepository
 *
 */
@DataJpaTest
@ActiveProfiles("test")
class FinancialSituationRepositoryTest {
    
    @Autowired
    private FinancialSituationRepository financialSituationRepository;
    
    @Test
    void shouldSaveSituation() {
        // Given
        FinancialSituation situation = new FinancialSituation();
        // TODO : DÃ©finir les propriÃ©tÃ©s
        // companyId, monthlyRevenue ("50000.00"), monthlyExpenses ("30000.00")
        // cashBalance ("100000.00"), directorCurrentAccountBalance ("20000.00")
        // ratioCACharges ("1.67"), cashCoverageMonths ("3.33"), directorCurrentAccountDebtorDays (0)
        // source ("MANUEL"), notes
        
        // When
        // TODO : Sauvegarder
        
        // Then
        // TODO : VÃ©rifier que l'ID n'est pas null
        // TODO : VÃ©rifier companyId
        // TODO : VÃ©rifier monthlyRevenue avec isEqualByComparingTo
        // TODO : VÃ©rifier que capturedAt n'est pas null
        // TODO : VÃ©rifier la source
    }
    
    @Test
    void shouldFindByCompanyIdOrderByCapturedAtDesc() {
        // Given
        FinancialSituation situation1 = new FinancialSituation();
        // TODO : CrÃ©er situation1 avec companyId = 1L, CA = "40000.00"
        // TODO : Sauvegarder
        
        // Pause pour ordre chronologique
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        
        FinancialSituation situation2 = new FinancialSituation();
        // TODO : CrÃ©er situation2 avec companyId = 1L, CA = "60000.00"
        // TODO : Sauvegarder
        
        // When
        // TODO : Appeler findByCompanyIdOrderByCapturedAtDesc(1L)
        
        // Then
        // TODO : VÃ©rifier que la liste contient 2 Ã©lÃ©ments
        // TODO : VÃ©rifier que le premier est situation2 (plus rÃ©cent = 60000)
        // TODO : VÃ©rifier que le second est situation1 (plus ancien = 40000)
    }
    
    @Test
    void shouldFindFirstByCompanyIdOrderByCapturedAtDesc() {
        // Given
        FinancialSituation ancienne = new FinancialSituation();
        // TODO : CrÃ©er avec companyId = 1L, CA = "40000.00", source = "MANUEL"
        // TODO : Sauvegarder
        
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        
        FinancialSituation recente = new FinancialSituation();
        // TODO : CrÃ©er avec companyId = 1L, CA = "60000.00", source = "API"
        // TODO : Sauvegarder
        
        // When
        // TODO : Appeler findFirstByCompanyIdOrderByCapturedAtDesc(1L)
        
        // Then
        // TODO : VÃ©rifier que ce n'est pas null
        // TODO : VÃ©rifier que CA = "60000.00" (la plus rÃ©cente)
        // TODO : VÃ©rifier que source = "API"
    }
    
    @Test
    void shouldReturnEmptyListWhenNoSituationsForCompany() {
        // Given
        // TODO : CrÃ©er et sauvegarder une situation avec companyId = 1L
        
        // When
        // TODO : Appeler findByCompanyIdOrderByCapturedAtDesc(999L)
        
        // Then
        // TODO : VÃ©rifier que la liste est vide
    }
    
    @Test
    void shouldHandleDifferentSources() {
        // Given
        FinancialSituation manuel = new FinancialSituation();
        // TODO : CrÃ©er avec source = "MANUEL"
        
        FinancialSituation api = new FinancialSituation();
        // TODO : CrÃ©er avec source = "API"
        
        FinancialSituation importFile = new FinancialSituation();
        // TODO : CrÃ©er avec source = "IMPORT"
        
        // When
        // TODO : Sauvegarder les trois
        
        // Then
        // TODO : VÃ©rifier que les sources sont correctement sauvegardÃ©es
    }
}

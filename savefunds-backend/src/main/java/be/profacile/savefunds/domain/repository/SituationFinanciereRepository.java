package be.profacile.savefunds.domain.repository;

import be.profacile.savefunds.domain.entity.SituationFinanciere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SituationFinanciereRepository extends JpaRepository<SituationFinanciere, Long> {
    List<SituationFinanciere> findByEntrepriseIdOrderByCapturedAtDesc(Long entrepriseId);
    SituationFinanciere findFirstByEntrepriseIdOrderByCapturedAtDesc(Long entrepriseId);
}
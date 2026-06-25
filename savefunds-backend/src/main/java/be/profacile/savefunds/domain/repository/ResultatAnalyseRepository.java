package be.profacile.savefunds.domain.repository;

import be.profacile.savefunds.domain.entity.ResultatAnalyse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ResultatAnalyseRepository extends JpaRepository<ResultatAnalyse, Long> {
    Optional<ResultatAnalyse> findByAnalyse_Id(Long analyseId);
}

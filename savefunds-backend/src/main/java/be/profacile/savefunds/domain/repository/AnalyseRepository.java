package be.profacile.savefunds.domain.repository;

import be.profacile.savefunds.domain.entity.AnalysePrelevement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyseRepository extends JpaRepository<AnalysePrelevement, Long> {
    List<AnalysePrelevement> findByEntreprise_IdOrderByCreatedAtDesc(Long entrepriseId);
}
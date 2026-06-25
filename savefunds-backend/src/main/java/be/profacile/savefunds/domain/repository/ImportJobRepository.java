package be.profacile.savefunds.domain.repository;

import be.profacile.savefunds.domain.entity.ImportJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportJobRepository extends JpaRepository<ImportJob, Long> {
}

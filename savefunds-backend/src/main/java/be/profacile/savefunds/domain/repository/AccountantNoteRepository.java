package be.profacile.savefunds.domain.repository;

import be.profacile.savefunds.domain.entity.AccountantNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountantNoteRepository extends JpaRepository<AccountantNote, Long> {
    Optional<AccountantNote> findFirstByEntrepriseIdOrderByUpdatedAtDescCreatedAtDesc(Long entrepriseId);
}

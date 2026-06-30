package be.profacile.savefunds.domain.repository;

import be.profacile.savefunds.domain.entity.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntrepriseRepository extends JpaRepository<Entreprise, Long> {
    Optional<Entreprise> findFirstByUserIdOrderByCreatedAtDesc(Long userId);

    default Optional<Entreprise> findByUserId(Long userId) {
        return findFirstByUserIdOrderByCreatedAtDesc(userId);
    }

    List<Entreprise> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUserIdAndNumeroEntreprise(Long userId, String numeroEntreprise);
}

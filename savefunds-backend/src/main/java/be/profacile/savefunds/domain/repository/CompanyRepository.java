package be.profacile.savefunds.domain.repository;

import be.profacile.savefunds.domain.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findFirstByUserIdOrderByCreatedAtDesc(Long userId);

    default Optional<Company> findByUserId(Long userId) {
        return findFirstByUserIdOrderByCreatedAtDesc(userId);
    }

    List<Company> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUserIdAndEnterpriseNumber(Long userId, String enterpriseNumber);
}

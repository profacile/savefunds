package be.profacile.savefunds.domain.repository;

import be.profacile.savefunds.domain.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value = """
            select * from entreprises company
            where regexp_replace(company.numero_entreprise, '[^0-9]', '', 'g') = :digits
            order by company.created_at desc
            limit 1
            """, nativeQuery = true)
    Optional<Company> findFirstByEnterpriseNumberDigits(@Param("digits") String digits);
}

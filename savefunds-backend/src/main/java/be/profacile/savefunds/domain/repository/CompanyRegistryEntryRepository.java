package be.profacile.savefunds.domain.repository;

import be.profacile.savefunds.domain.entity.CompanyRegistryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRegistryEntryRepository extends JpaRepository<CompanyRegistryEntry, Long> {
    Optional<CompanyRegistryEntry> findByEnterpriseNumber(String enterpriseNumber);

    @Query("""
            select entry from CompanyRegistryEntry entry
            where lower(entry.name) like lower(concat('%', :query, '%'))
               or lower(entry.enterpriseNumber) like lower(concat('%', :query, '%'))
               or lower(entry.city) like lower(concat('%', :query, '%'))
            order by entry.active desc, entry.name asc
            """)
    List<CompanyRegistryEntry> search(@Param("query") String query);
}

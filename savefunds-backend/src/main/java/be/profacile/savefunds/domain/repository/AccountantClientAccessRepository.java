package be.profacile.savefunds.domain.repository;

import be.profacile.savefunds.domain.entity.AccountantClientAccess;
import be.profacile.savefunds.domain.enums.AccountantClientAccessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountantClientAccessRepository extends JpaRepository<AccountantClientAccess, Long> {
    List<AccountantClientAccess> findByAccountantIdOrderByUpdatedAtDescCreatedAtDesc(Long accountantId);

    List<AccountantClientAccess> findByAccountantIdAndStatusOrderByUpdatedAtDescCreatedAtDesc(Long accountantId, AccountantClientAccessStatus status);

    List<AccountantClientAccess> findByCompany_UserIdAndStatusInOrderByCreatedAtDesc(Long userId, Collection<AccountantClientAccessStatus> statuses);

    Optional<AccountantClientAccess> findByAccountantIdAndCompanyId(Long accountantId, Long companyId);
}

package be.profacile.savefunds.domain.repository;

import be.profacile.savefunds.domain.entity.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    List<BankTransaction> findByEntrepriseIdOrderByTransactionDateDescIdDesc(Long entrepriseId);
}

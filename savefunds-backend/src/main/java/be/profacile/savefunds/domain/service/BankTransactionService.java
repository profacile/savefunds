package be.profacile.savefunds.domain.service;

import be.profacile.savefunds.domain.entity.BankTransaction;
import be.profacile.savefunds.domain.entity.FinancialSnapshot;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BankTransactionService {
    List<BankTransaction> importAndClassify(Long entrepriseId, FinancialSnapshot snapshot, MultipartFile file);

    List<BankTransaction> findByEntreprise(Long entrepriseId);
}

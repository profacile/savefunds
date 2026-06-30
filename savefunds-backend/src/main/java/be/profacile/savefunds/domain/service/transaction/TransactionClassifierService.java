package be.profacile.savefunds.domain.service.transaction;

import be.profacile.savefunds.domain.entity.Entreprise;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransactionClassifierService {
    TransactionClassificationResult classify(
            Entreprise entreprise,
            LocalDate transactionDate,
            String description,
            BigDecimal amount
    );
}

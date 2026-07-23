package be.profacile.savefunds.domain.service.transaction;

import be.profacile.savefunds.domain.entity.Company;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransactionClassifierService {
    TransactionClassificationResult classify(
            Company company,
            LocalDate transactionDate,
            String description,
            BigDecimal amount
    );
}

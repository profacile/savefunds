package be.profacile.savefunds.domain.service.financial;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class BelgianAccountingMapper {

    public AccountingBuckets map(Map<String, BigDecimal> balancesByAccount) {
        AccountingBuckets buckets = new AccountingBuckets();

        balancesByAccount.forEach((accountCode, amount) -> {
            String normalized = normalize(accountCode);
            if (normalized.startsWith("70")) {
                buckets.addRevenue(amount);
            } else if (startsWithAny(normalized, "60", "61", "62", "63", "64")) {
                buckets.addExpense(amount.abs());
            } else if (startsWithAny(normalized, "550", "551", "552", "53", "57")) {
                buckets.addCash(amount);
            } else if (normalized.startsWith("400")) {
                buckets.addCustomerReceivable(amount);
            } else if (normalized.startsWith("440")) {
                buckets.addSupplierDebt(amount.abs());
            } else if (startsWithAny(normalized, "416", "489")) {
                buckets.addCurrentAccount(amount);
            } else {
                buckets.addIgnoredAccount(normalized);
            }
        });

        return buckets;
    }

    private String normalize(String accountCode) {
        return accountCode == null ? "" : accountCode.replaceAll("[^0-9]", "");
    }

    private boolean startsWithAny(String value, String... prefixes) {
        for (String prefix : prefixes) {
            if (value.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static class AccountingBuckets {
        private BigDecimal revenue = BigDecimal.ZERO;
        private BigDecimal expenses = BigDecimal.ZERO;
        private BigDecimal cash = BigDecimal.ZERO;
        private BigDecimal currentAccount = BigDecimal.ZERO;
        private BigDecimal supplierDebt = BigDecimal.ZERO;
        private BigDecimal customerReceivables = BigDecimal.ZERO;
        private final Map<String, Integer> ignoredAccounts = new LinkedHashMap<>();

        public void addRevenue(BigDecimal value) {
            revenue = revenue.add(value.abs());
        }

        public void addExpense(BigDecimal value) {
            expenses = expenses.add(value.abs());
        }

        public void addCash(BigDecimal value) {
            cash = cash.add(value);
        }

        public void addCurrentAccount(BigDecimal value) {
            currentAccount = currentAccount.add(value);
        }

        public void addSupplierDebt(BigDecimal value) {
            supplierDebt = supplierDebt.add(value.abs());
        }

        public void addCustomerReceivable(BigDecimal value) {
            customerReceivables = customerReceivables.add(value.abs());
        }

        public void addIgnoredAccount(String accountCode) {
            if (!accountCode.isBlank()) {
                ignoredAccounts.merge(accountCode, 1, Integer::sum);
            }
        }

        public BigDecimal getRevenue() {
            return revenue;
        }

        public BigDecimal getExpenses() {
            return expenses;
        }

        public BigDecimal getCash() {
            return cash;
        }

        public BigDecimal getCurrentAccount() {
            return currentAccount;
        }

        public BigDecimal getSupplierDebt() {
            return supplierDebt;
        }

        public BigDecimal getCustomerReceivables() {
            return customerReceivables;
        }

        public Map<String, Integer> getIgnoredAccounts() {
            return ignoredAccounts;
        }
    }
}

package be.profacile.savefunds.service.financial;

import be.profacile.savefunds.domain.service.financial.BelgianAccountingMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BelgianAccountingMapperTest {

    private final BelgianAccountingMapper mapper = new BelgianAccountingMapper();

    @Test
    void mapsBelgianAccountingAccountsIntoFinancialBuckets() {
        BelgianAccountingMapper.AccountingBuckets buckets = mapper.map(Map.of(
                "70", BigDecimal.valueOf(120000),
                "61", BigDecimal.valueOf(-30000),
                "62", BigDecimal.valueOf(-40000),
                "550", BigDecimal.valueOf(15000),
                "416", BigDecimal.valueOf(-3000),
                "440", BigDecimal.valueOf(-8000),
                "400", BigDecimal.valueOf(12000)
        ));

        assertThat(buckets.getRevenue()).isEqualByComparingTo("120000");
        assertThat(buckets.getExpenses()).isEqualByComparingTo("70000");
        assertThat(buckets.getCash()).isEqualByComparingTo("15000");
        assertThat(buckets.getCurrentAccount()).isEqualByComparingTo("-3000");
        assertThat(buckets.getSupplierDebt()).isEqualByComparingTo("8000");
        assertThat(buckets.getCustomerReceivables()).isEqualByComparingTo("12000");
    }
}

package be.profacile.savefunds.validation;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires du validateur PositiveAmountValidator.
 */
class PositiveAmountValidatorTest {

    /**
     * Instance du validateur Ã  tester.
     */
    private final PositiveAmountValidator validator = new PositiveAmountValidator();

    /**
     * VÃ©rifie qu'un montant strictement positif est acceptÃ©.
     */
    @Test
    void shouldReturnTrueForPositiveAmount() {
        assertTrue(validator.isValid(new BigDecimal("100.50"), null));
    }

    /**
     * VÃ©rifie que zÃ©ro est refusÃ©.
     */
    @Test
    void shouldReturnFalseForZeroAmount() {
        assertFalse(validator.isValid(BigDecimal.ZERO, null));
    }

    /**
     * VÃ©rifie qu'un montant nÃ©gatif est refusÃ©.
     */
    @Test
    void shouldReturnFalseForNegativeAmount() {
        assertFalse(validator.isValid(new BigDecimal("-5.00"), null));
    }

    /**
     * VÃ©rifie qu'une valeur null est acceptÃ©e.
     * Le contrÃ´le null peut Ãªtre gÃ©rÃ© sÃ©parÃ©ment avec @NotNull.
     */
    @Test
    void shouldReturnTrueForNullValue() {
        assertTrue(validator.isValid(null, null));
    }
}
